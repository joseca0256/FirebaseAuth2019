package com.firebasetest.firebaseauth2019.Presentador.PrincipalPresenter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebasetest.firebaseauth2019.Adaptadores.RecyclerProductoAdapter;
import com.firebasetest.firebaseauth2019.Modelo.ProductoModel;
import com.firebasetest.firebaseauth2019.Modelo.UserModel;
import com.firebasetest.firebaseauth2019.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PresenterPrincipal {


    private Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private RecyclerProductoAdapter mAdapter;


    public PresenterPrincipal(Context mContext, DatabaseReference mDatabase, FirebaseAuth mAuth, StorageReference mStorageRef) {
        this.mContext = mContext;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.mStorageRef = mStorageRef;
    }

    public void welcomeMessage() {

        mDatabase.child("Usuarios").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Toast.makeText(mContext, "Bienvenido " + userModel.getNombre(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void cargarRecyclerView(final RecyclerView mRecyclerView) {

        mDatabase.child("Usuarios").child(mAuth.getCurrentUser().getUid()).child("productos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<ProductoModel> arrayListProductos = new ArrayList<>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ProductoModel productoModel = snapshot.getValue(ProductoModel.class);

                    //obtener datos firebase

                    String nombreprod = productoModel.getNombreProducto();
                    float precioproducto = productoModel.getPrecioProducto();
                    String imagenprod = productoModel.getImagen();

                    //set datos al array
                    productoModel.setNombreProducto(nombreprod);
                    productoModel.setPrecioProducto(precioproducto);
                    productoModel.setImagen(imagenprod);

                    arrayListProductos.add(productoModel);
                }

                mAdapter = new RecyclerProductoAdapter(mContext, R.layout.producto_row, arrayListProductos);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void cargarProductoFirebase(final String nombreProducto, final float precioProducto, final Dialog dialog, final ProgressDialog progressDialog, Uri filePath) {

        if (filePath != null) {

            final StorageReference fotoRef = mStorageRef.child("fotos").child(mAuth.getCurrentUser().getUid()).child(filePath.getLastPathSegment());
            fotoRef.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw new Exception();

                    }
                    return fotoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadLink = task.getResult();
                        Map<String, Object> producto = new HashMap<>();
                        producto.put("nombreProducto", nombreProducto);
                        producto.put("precioProducto", precioProducto);
                        producto.put("imagen", downloadLink.toString());
                        mDatabase.child("Usuarios").child(mAuth.getCurrentUser().getUid()).child("productos").push().updateChildren(producto).addOnCompleteListener(new OnCompleteListener<Void>() {


                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                dialog.dismiss();
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Se cargo el producto correctamente", Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Error al cargar el producto " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });


                    }

                }
            });

        }


    }
}
