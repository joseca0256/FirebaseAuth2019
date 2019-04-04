package com.firebasetest.firebaseauth2019.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebasetest.firebaseauth2019.Modelo.ProductoModel;
import com.firebasetest.firebaseauth2019.R;
import com.firebasetest.firebaseauth2019.Vista.ProductoView.VistaProductoDetalle;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class RecyclerProductoAdapter extends RecyclerView.Adapter<RecyclerProductoAdapter.ProductoViewHolder> {

    private Context mContext;
    private int layoutResource;
    private ArrayList<ProductoModel> arrayListProductos;


    public RecyclerProductoAdapter(Context mContext, int layoutResource, ArrayList<ProductoModel> arrayListProductos) {
        this.mContext = mContext;
        this.layoutResource = layoutResource;
        this.arrayListProductos = arrayListProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(mContext).inflate(layoutResource,viewGroup,false);

        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductoViewHolder productoViewHolder, int position) {

        ProductoModel productoModel = arrayListProductos.get(position);

        productoViewHolder.mNombreProducto.setText("Nombre: " + productoModel.getNombreProducto());
        productoViewHolder.mPrecioProducto.setText("Precio: "+(int)productoModel.getPrecioProducto() + " Bs");




        //cargar foto firebase

        Glide
                .with(mContext)
                .load(productoModel.getImagen())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        productoViewHolder.mProgressBar.setVisibility(View.GONE);
                        productoViewHolder.mImagenProducto.setVisibility(View.VISIBLE);
                        productoViewHolder.mImagenProducto.setImageResource(R.drawable.ic_error_black_24dp);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        productoViewHolder.mProgressBar.setVisibility(View.GONE);
                        productoViewHolder.mImagenProducto.setVisibility(View.VISIBLE);
                        return false;
                    }
                })

                .into(productoViewHolder.mImagenProducto);

        //productoViewHolder.mImagenProducto.setImageResource(R.drawable.ic_launcher_background);

    }

    @Override
    public int getItemCount() {

        if (arrayListProductos.size() > 0) {

            return arrayListProductos.size();
        }
        return 0;
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNombreProducto, mPrecioProducto;
        ImageView mImagenProducto;
        ProgressBar mProgressBar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNombreProducto = itemView.findViewById(R.id.nombreProductoRow);
            mPrecioProducto = itemView.findViewById(R.id.precioProductoRow);
            mImagenProducto = itemView.findViewById(R.id.imagenProductoRow);
            mProgressBar = itemView.findViewById(R.id.progress);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, VistaProductoDetalle.class);
            mContext.startActivity(intent);

        }
    }


}
