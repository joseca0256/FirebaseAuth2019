package com.firebasetest.firebaseauth2019.Presentador.LoginPresenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebasetest.firebaseauth2019.Vista.LoginView.VistaLogin;
import com.firebasetest.firebaseauth2019.Vista.PrincipalView.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class PresentadorLogin {

    private String TAG = "PresentadorLogin";
    private Context mContext;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public PresentadorLogin(Context mContext, FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.mContext = mContext;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    public void signUser(String email,String password){

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Ingresando...");
        dialog.setCancelable(false);
        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext, "Authentication Success", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }else {
                    dialog.dismiss();
                    Toast.makeText(mContext, "Authentication UnSuccess", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
