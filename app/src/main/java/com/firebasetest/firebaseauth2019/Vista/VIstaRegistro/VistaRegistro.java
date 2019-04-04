package com.firebasetest.firebaseauth2019.Vista.VIstaRegistro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebasetest.firebaseauth2019.Presentador.RegistroPresenter.PresentadorRegistro;
import com.firebasetest.firebaseauth2019.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VistaRegistro extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtxtNombre, mEtxtUsuario, mEtxtEmail, mEtxtPassword, mEtxtConfirmarpass;
    private PresentadorRegistro presentadorRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        presentadorRegistro = new PresentadorRegistro(this,mAuth,mDatabase);

        mEtxtNombre = findViewById(R.id.etNombre);
        mEtxtUsuario = findViewById(R.id.etNombredeUsuario);
        mEtxtEmail = findViewById(R.id.etEmail);
        mEtxtPassword = findViewById(R.id.etPassword);
        mEtxtConfirmarpass = findViewById(R.id.etPassword2);
        Button mRegisterbtn = findViewById(R.id.btnRegistrar);
        mRegisterbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnRegistrar:
                String nombre = mEtxtNombre.getText().toString().trim();
                String usuario = mEtxtUsuario.getText().toString().trim();
                String email = mEtxtEmail.getText().toString().trim();
                String pass = mEtxtPassword.getText().toString().trim();
                String confirmpass = mEtxtConfirmarpass.getText().toString().trim();

                if(pass.equals(confirmpass)){

                    presentadorRegistro.signUpuser(email,pass,nombre,usuario);

                }else{
                    Toast.makeText(this,"Las contraseñas no son iguales",Toast.LENGTH_LONG).show();
                }

                break;

        }

    }
}
