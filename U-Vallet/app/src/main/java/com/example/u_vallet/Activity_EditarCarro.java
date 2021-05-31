package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_EditarCarro extends AppCompatActivity {
    int IMAGE_PICKER_REQUEST = 1;
    private int STORAGE_PERMISSION_CODE = 1;
    private Uri imagenUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private ImageView mImageView;
    private TextView placa;
    private TextView marca;
    private TextView modelo;
    private TextView capacidad;
    private boolean imageInclude = false;
    boolean imagenSeleccionada = false;
    boolean marcaSeleccionada = false;
    boolean modeloSeleccionado = false;
    String modeloCarro = null;
    String marcaCarro = null;
    ImageView imagenCarro;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    // JSON api response
    JSONObject JSONModelosCarros = null;
    HashSet<String> marcasCarros = new HashSet<String>();
    ArrayList<String> carBrands = new ArrayList<String>();
    HashMap<String, ArrayList<String>> carModels = new HashMap<String, ArrayList<String>>();
    ArrayList<String> marcainicial = new ArrayList<>();
    ArrayList<String> modeloinicial = new ArrayList<>();
    String placaantigua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        currentUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_editar_carro);
        mImageView = (ImageView) findViewById(R.id.imagenFotoPerfil);
        placa = (TextView) findViewById(R.id.EditarCarro_Placa);
        marca = (TextView) findViewById(R.id.EditarCarro_Marca);
        modelo = (TextView) findViewById(R.id.EditarCarro_Modelo);
        capacidad = (TextView) findViewById(R.id.AgregarCarro_Capacidad);
        placa.setText(getIntent().getStringExtra("placa"));
        placaantigua = getIntent().getStringExtra("placa");
        marca.setText(getIntent().getStringExtra("marca"));
        modelo.setText(getIntent().getStringExtra("modelo"));
        capacidad.setText(getIntent().getStringExtra("capacidad"));
        modelo.setEnabled(false);
        marca.setEnabled(false);
        placa.setEnabled(false);

        StorageReference profileRef = mStorageRef.child("cars/"+mAuth.getCurrentUser().getUid()+"/"+placa.getText().toString()+"/car.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(mImageView);
            }
        });

        Button EditarFoto = (Button) findViewById(R.id.btn_EditarCarro_EditarFoto);
        EditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and launch the new activity with an Intent
                if(ContextCompat.checkSelfPermission( Activity_EditarCarro.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_EditarCarro.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }else{
                    requestStoragePermission();
                }
            }
        });
        Button Guardar = (Button) findViewById(R.id.btn_EditarCarro_GuardarCambios);
        Guardar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateCarInfo(currentUser);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(v.getContext(), Activity_MisCarros.class);
                startActivity(intent);
            }
        }));

    }

    // Method to validate user input
    private boolean validateForm( String placaCarro, String capacidadCarro ) {

        // Check for empty fields
        if (placaCarro.isEmpty()) {
            placa.setError("Vacío o inválido");
            return false;
        }
       /* if (!marcaSeleccionada) {
            Toast.makeText(Activity_EditarCarro.this, "Marca inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!modeloSeleccionado) {
            Toast.makeText(Activity_EditarCarro.this, "Modelo inválido", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (capacidadCarro.isEmpty()) {
            capacidad.setError("Vacío o inválido");
            return false;
        }
        if (!imagenSeleccionada) {
            Toast.makeText(Activity_EditarCarro.this, "Por favor seleccione una foto para el carro", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for the plate
        Pattern pattern = Pattern.compile("[a-zA-Z]{3} ?- ?[0-9]{3}");
        Matcher matcher = pattern.matcher(placaCarro);
        if (!matcher.find()) {
            placa.setError("Placa inválida, formato requerido: abc - 123");
            return false;
        }

        // Return true if nothing invalid was found
        return true;
    }

    private void overWriteCarInfo(FirebaseUser currentUser){
        currentUser = mAuth.getCurrentUser();
        String key = placaantigua;
        Log.d("UID_OW",key+"/");
        myRef = database.getReference("cars/"+mAuth.getCurrentUser().getUid()+"/");
        Carro carro = new Carro(marca.getText().toString(),placa.getText().toString(),modelo.getText().toString(),Integer.parseInt(capacidad.getText().toString()));
        //Usuario usuario = new Usuario (uid, userName, name, contraseña, fechaNacimiento, telefono, direccion);
        Map<String, Object> valoresUsuario = carro.toMap();
        Map<String, Object> actualizacionHijos = new HashMap<>();
        actualizacionHijos.put(key,valoresUsuario);
        myRef.updateChildren(actualizacionHijos);
    }
    private void updateCarInfo(FirebaseUser currentUser) throws ParseException {
        if(validateForm(placa.getText().toString(),capacidad.getText().toString())){
            uploadImageToFirebase(imagenUri);
            overWriteCarInfo(currentUser);;
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImageToFirebase(Uri imagenUri){
        StorageReference fileRef = mStorageRef.child("cars/"+mAuth.getCurrentUser().getUid()+"/"+placa.getText().toString()+"/car.jpg");
        fileRef.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                        Toast.makeText(Activity_EditarCarro.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity_EditarCarro.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido para el acceso a su galeria")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Activity_EditarCarro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        mImageView.setImageBitmap(selectedImage);
                        imagenUri = imageUri;
                        imagenSeleccionada = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}