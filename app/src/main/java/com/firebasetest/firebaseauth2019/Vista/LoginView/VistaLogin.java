package com.firebasetest.firebaseauth2019.Vista.LoginView;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebasetest.firebaseauth2019.Presentador.LoginPresenter.PresentadorLogin;
import com.firebasetest.firebaseauth2019.Vista.PrincipalView.HomeActivity;
import com.firebasetest.firebaseauth2019.R;
import com.firebasetest.firebaseauth2019.Vista.VIstaRegistro.VistaRegistro;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class VistaLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //TAG ------------------------------------------------------------------------------------------
    private static final String TAG = "VistaLogin";
    private static final int SIGN_IN_GOOGLE_CODE = 1;

    //Firebase Authentication-----------------------------------------------------------------------
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private DatabaseReference mDatabase;
    private PresentadorLogin presentadorLogin;


    //Botones y edit texts del xml------------------------------------------------------------------

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnCreateAccount;
    private Button btnSignIn;
    private SignInButton btnSignInGoogle;
    private LoginButton btnSignInFacebook;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        //Metodo incialize--------------------------------------------------------------------------
        inicialize();

        //presentador login

        presentadorLogin = new PresentadorLogin(this, mAuth, mDatabase);


        //Elementos vindeados-----------------------------------------------------------------------
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignInGoogle = (SignInButton) findViewById(R.id.btnSignInGoogle);
        btnSignInFacebook = (LoginButton) findViewById(R.id.btnSignInFacebook);
        edtEmail = (EditText) findViewById(R.id.edtxEmail);
        edtPassword = (EditText) findViewById(R.id.edtxPassword);


        //onclick para crear cuenta-----------------------------------------------------------------
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCreateAccount();

            }
        });

        //Onclick para loguear cuenta---------------------------------------------------------------
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                presentadorLogin.signUser(email,password);


            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        btnSignInFacebook.setReadPermissions("email", "public_profile");
        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Sucess Token: " + loginResult.getAccessToken().getToken());
                setSignInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Error");
                error.printStackTrace();
            }
        });
    }

    public void gohome() {

        Intent siguiente = new Intent(this, HomeActivity.class);
        startActivity(siguiente);
        finish();
    }
    public void goCreateAccount() {

        Intent siguiente = new Intent(this, VistaRegistro.class);
        startActivity(siguiente);
        finish();
    }

    //metodo inicialize, para chequear al usuario si esta logueado o no--------------------------------
    private void inicialize() {


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    startActivity(new Intent(VistaLogin.this, HomeActivity.class));
                } else {

                    Log.w(TAG, "onAuthStateChanged - signed_out");


                }
            }
        };

        //inicializacion de google Account

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    //------------------------------------------------------------------------------------------------
    //Metodo para crear cuenta---------------------------------------------------------------------------

    private void createAccount() {

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VistaLogin.this, "Create Account Success", Toast.LENGTH_LONG).show();
                    gohome();
                } else {
                    Toast.makeText(VistaLogin.this, "Create Account UnSuccess", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //metodo la loguear cuenta---------------------------------------------------------------------------------


    private void setSignInGoogleFirebase(GoogleSignInResult googleSignInResult) {

        if (googleSignInResult.isSuccess()) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(VistaLogin.this, "Google Authentication Success", Toast.LENGTH_LONG).show();
                        gohome();
                    } else {
                        Toast.makeText(VistaLogin.this, "Google Authentication UnSuccess", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } else {

            Toast.makeText(VistaLogin.this, "Google Sign In UnSuccess", Toast.LENGTH_LONG).show();

        }

    }

    private void setSignInFacebookFirebase(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(VistaLogin.this, "Facebook Authentication Success", Toast.LENGTH_LONG).show();
                    gohome();
                } else {
                    Toast.makeText(VistaLogin.this, "Facebook Authentication UnSuccess", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_GOOGLE_CODE) {

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            setSignInGoogleFirebase(googleSignInResult);

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut() {
        mAuth.signOut();
        if (Auth.GoogleSignInApi != null) {

            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {

                        Intent siguiente = new Intent(VistaLogin.this, HomeActivity.class);
                        startActivity(siguiente);
                        finish();

                    } else {

                        Toast.makeText(VistaLogin.this, "Error", Toast.LENGTH_LONG).show();


                    }
                }
            });
        }

        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }

    }
}
