package com.firebasetest.firebaseauth2019.Vista.PrincipalView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebasetest.firebaseauth2019.Presentador.PrincipalPresenter.PresenterPrincipal;
import com.firebasetest.firebaseauth2019.R;
import com.firebasetest.firebaseauth2019.Vista.LoginView.VistaLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private PresenterPrincipal presenterPrincipal;
    private EditText nombreProd, precioProd;
    private Dialog dialog;
    private Button signOut;
    private final int PICK_PHOTO = 1;
    private ImageView imagenProducto;
    private Uri filePath;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_principal);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        presenterPrincipal = new PresenterPrincipal(this, mDatabase, mAuth, mStorageRef);
        presenterPrincipal.welcomeMessage();
        initRecycler();

        signOut = (Button) findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, VistaLogin.class));
            }
        });

    }

    private void initRecycler() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        presenterPrincipal.cargarRecyclerView(mRecyclerView);

    }

    public void cargarProducto() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_row);
        nombreProd = dialog.findViewById(R.id.etxtNombredeproducto);
        precioProd = dialog.findViewById(R.id.etxtPreciodeproducto);
        Button cargarProducto = dialog.findViewById(R.id.btnAddProducto);
        cargarProducto.setOnClickListener(this);
        //cargar imagen
        ImageView cargarFotoProducto = dialog.findViewById(R.id.imageViewProducto);
        cargarFotoProducto.setOnClickListener(this);
        imagenProducto = dialog.findViewById(R.id.imageViewProducto);

        dialog.show();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab:
                cargarProducto();
                break;

            case R.id.btnAddProducto:

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Añadiendo producto...(puede tardar algunos minutos, no cierre la aplicacion)");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String nombreProducto = nombreProd.getText().toString().trim();
                float precioProducto = Float.parseFloat(precioProd.getText().toString().trim());
                presenterPrincipal.cargarProductoFirebase(nombreProducto, precioProducto, dialog,progressDialog,filePath);
                break;

            case R.id.imageViewProducto:
                abrirFotoGaleria();

                break;
        }

    }

    private void abrirFotoGaleria() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), PICK_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

             filePath = data.getData();

            try {
                Bitmap bitmapImagen = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagenProducto.setImageBitmap(bitmapImagen);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
