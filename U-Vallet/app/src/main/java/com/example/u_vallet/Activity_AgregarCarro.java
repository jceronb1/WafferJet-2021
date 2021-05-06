package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_AgregarCarro extends AppCompatActivity {

    //-----------------------------------------------
    //---------------  Attributes  ------------------
    //-----------------------------------------------
    // Database
    private DatabaseReference firebaseDB;
    // Permissions
    private static final int MEDIA_PERMISSION_CODE = 311;
    private static final int SELECT_IMAGE_CODE = 312;
    // Input fields
    EditText placa;
    EditText marca;
    EditText modelo;
    EditText capacidad;
    ImageView imagenCarro;
    boolean imagenSeleccionada = false;

    //-----------------------------------------------
    //---------------  On create  -------------------
    //-----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_carro);

        // Create Firebase Data Base instance
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        //
        imagenCarro = (ImageView) findViewById(R.id.AgregarCarro_ImagenCarro);

        // Logic to add a new car
        Button agregarCarro = (Button) findViewById(R.id.btn_AgregarCarro_GuardarCambios);
        agregarCarro.setOnClickListener( view -> {
            // Get input fields
            placa = (EditText) findViewById(R.id.AgregarCarro_Placa);
            marca = (EditText) findViewById(R.id.AgregarCarro_Marca);
            modelo = (EditText) findViewById(R.id.AgregarCarro_Modelo);
            capacidad = (EditText) findViewById(R.id.AgregarCarro_Capacidad);

            // Convert values in input
            String marcaCarro = marca.getText().toString();
            String placaCarro = placa.getText().toString();
            String modeloCarro = modelo.getText().toString();
            String capacidadCarro = capacidad.getText().toString();

            // Validate form
            if (!validateForm(marcaCarro, placaCarro, modeloCarro, capacidadCarro)) {
                Toast.makeText(view.getContext(), "Formulario inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user info
            String nombreConductor = "Pedro Perez";
            int idConductor = 123;

            // Write new car
            // writeNewCar(nombreConductor, marcaCarro, placaCarro, modeloCarro, capacidadCarro, idConductor);
            Log.i("Carro", "New car added");

            // Create dialog to inform the user that a new car was added
            String toastMessage = "El carro [ " + marcaCarro + " ] y placa [ " + placaCarro + " ] fue agregado con éxito";
            new AlertDialog.Builder(Activity_AgregarCarro.this)
                    .setTitle("Carro agregado")
                    .setMessage(toastMessage)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent misCarros = new Intent(view.getContext(), Activity_MisCarros.class);
                            startActivity(misCarros);
                        }
                    })
                    .create().show();

        });

        // Logic to select a photo
        Button selectImage = (Button) findViewById(R.id.btn_AgrgarCarro_AgregarFoto);
        selectImage.setOnClickListener( view -> {
            // Check for permissions
            if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, select photo from gallery
                selectImageFromGallery();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(Activity_AgregarCarro.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Create new dialog
                new AlertDialog.Builder(Activity_AgregarCarro.this)
                        .setTitle("Permiso requerido")
                        .setMessage("Este permiso es requerido para el acceso a su galeria")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Activity_AgregarCarro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(Activity_AgregarCarro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_PERMISSION_CODE);
                selectImageFromGallery();
            }
        });

    }

    //-----------------------------------------------
    //------------  On results methods  -------------
    //-----------------------------------------------
    // Once the user has responded the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MEDIA_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.

                }  else {
                    // Create new dialog
                    new AlertDialog.Builder(Activity_AgregarCarro.this)
                            .setTitle("Permiso requerido")
                            .setMessage("Este permiso es requerido para el acceso a su galeria")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(Activity_AgregarCarro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_PERMISSION_CODE);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
                return;
        }
    }

    // Once the user has selected an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        if (resultCode == RESULT_OK &&  requestCode == SELECT_IMAGE_CODE) {
            Log.i("Called", "called");
            imagenCarro.setImageURI(data.getData());
            imagenSeleccionada = true;
        }
    }

    //-----------------------------------------------
    //------------- Permissions methods -------------
    //-----------------------------------------------
    private void requestMediaPermission() {
    }

    //-----------------------------------------------
    //-------------  Methods for DB  ----------------
    //-----------------------------------------------
    public void writeNewCar(String nombreConductor, String marcaCarro, String placa,String modelo,int capacidad ,int idConductor) {
        // Create instance of Car
        Carro carro = new Carro(nombreConductor, marcaCarro, placa, modelo, capacidad, idConductor);
        // Save car in DB
        firebaseDB.child("Carros").child(String.valueOf(idConductor)).setValue(carro);
    }

    //-----------------------------------------------
    //----------------  Methods  --------------------
    //-----------------------------------------------
    // Method to validate user input
    private boolean validateForm(String marcaCarro, String placaCarro,String modeloCarro, String capacidadCarro ) {
        // Check for empty fields

        if (placaCarro.isEmpty()) {
            placa.setError("Vacío o inválido");
            return false;
        }
        if (marcaCarro.isEmpty()) {
            marca.setError("Vacío");
            return false;
        }
        if (modeloCarro.isEmpty()) {
            modelo.setError("Vacío o inválido");
            return false;
        }
        if (capacidadCarro.isEmpty()) {
            capacidad.setError("Vacío o inválido");
            return false;
        }
        if (!imagenSeleccionada) {
            Toast.makeText(Activity_AgregarCarro.this, "Por favor seleccione una foto para el carro", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for the plate
        Pattern pattern = Pattern.compile("[a-zA-Z]{3} ?- ?[0-9]{3}");
        Matcher matcher = pattern.matcher(placaCarro);
        if (!matcher.find()) {
            placa.setError("Placa inválida, abc - 123");
            return false;
        }

        // Return true if nothing invalid was found
        return true;
    }

    private void selectImageFromGallery() {
        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");
        startActivityForResult(selectImageIntent, SELECT_IMAGE_CODE);
    }

}