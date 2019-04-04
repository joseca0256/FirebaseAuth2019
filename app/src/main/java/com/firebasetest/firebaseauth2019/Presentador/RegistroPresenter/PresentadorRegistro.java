package com.firebasetest.firebaseauth2019.Presentador.RegistroPresenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebasetest.firebaseauth2019.Vista.PrincipalView.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class PresentadorRegistro {

    private String TAG = "PresentadorRegistro";
    private Context mContext;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public PresentadorRegistro(Context mContext, FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.mContext = mContext;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    public void signUpuser(final String email, String password, final String nombrecompleto, final String username) {

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Registrando...");
        dialog.setCancelable(false);
        dialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Map<String, Object> crearUsuario = new HashMap<>();
                            crearUsuario.put("nombre", nombrecompleto);
                            crearUsuario.put("username", username);
                            crearUsuario.put("email", email);
                            mDatabase.child("Usuarios").child(task.getResult().getUser().getUid()).updateChildren(crearUsuario);
                            dialog.dismiss();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        // ...
                    }
                });


    }
}
