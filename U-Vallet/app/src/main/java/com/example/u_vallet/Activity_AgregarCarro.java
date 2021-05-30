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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_AgregarCarro extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
    EditText modelo;
    EditText capacidad;
    ImageView imagenCarro;
    String marcaCarro = null;
    boolean imagenSeleccionada = false;
    boolean marcaSeleccionada = false;
    // JSON api response
    JSONObject JSONModelosCarros = null;
    HashSet<String> modelosCarros = new HashSet<String>();

    //-----------------------------------------------
    //---------------  On create  -------------------
    //-----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_carro);

        // Create Firebase Data Base instance
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        // Car image
        imagenCarro = (ImageView) findViewById(R.id.AgregarCarro_ImagenCarro);

        // Logic to add a new car
        Button agregarCarro = (Button) findViewById(R.id.btn_AgregarCarro_GuardarCambios);
        agregarCarro.setOnClickListener( view -> {
            // Get input fields
            placa = (EditText) findViewById(R.id.AgregarCarro_Placa);
            modelo = (EditText) findViewById(R.id.AgregarCarro_Modelo);
            capacidad = (EditText) findViewById(R.id.AgregarCarro_Capacidad);

            // Convert values in input
            String marcaCarro = "Marca carro";
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

        //-----------  Volley REST service call  -------------
        restService();

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
//            marca.setError("Vacío");
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
            placa.setError("Placa inválida, formato requerido: abc - 123");
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

    private void restService() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getMakes&year=2000&sold_in_us=1";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("RESP", String.valueOf(response.length()));
                        Log.i("RESP", response);

                        // Convert String to JSONObject
                        response = response.substring(2, response.length() - 2);
                        try {
                            JSONModelosCarros = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        inflateSpinner();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Activity_AgregarCarro.this, "Fallo al consumir los servicios REST", Toast.LENGTH_SHORT).show();
                Log.i("VOLLEY", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void inflateSpinner() {

        Log.i("SPINNER", "SPINNER CALLED");

        // Parse JSON data from API response
        try {
            JSONArray carModelsArray = JSONModelosCarros.getJSONArray("Makes");
            for (int i = 0; i < carModelsArray.length(); i++) {
                // Traverse JSON object by keys
                JSONObject car = carModelsArray.getJSONObject(i);
                Iterator key = car.keys();
                while (key.hasNext()) {
                    String k = key.next().toString();
                    String val = car.getString(k);
                    if (k.equals("make_display")) {
                        modelosCarros.add(val);
                    }
                }
            }
        } catch ( Exception e ) {
            Log.i("ERROR", "INFLATING SPINNER ... " + e.toString());
        }

        // Debug
        Log.d("VALUE", modelosCarros.toString());

        // Convert HashSet into an array of Strings
        ArrayList<String> cars = new ArrayList<String>();
        for (String marca : modelosCarros) {
            cars.add(marca);
        }

        // Inflate Spinner
        Spinner carsSpinner = (Spinner) findViewById(R.id.AgregarCarro_Marcas);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cars);
        // Specify the layout to use when the list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        carsSpinner.setAdapter(arrayAdapter);
        //
        carsSpinner.setOnItemSelectedListener(this);

        Log.i("Spinner", "Finished inflating spinner");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i("ITEM", String.valueOf(parent.getItemAtPosition(pos)));
        marcaSeleccionada = true;
        marcaCarro = String.valueOf(parent.getItemAtPosition(pos));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        marcaSeleccionada = false;
        marcaCarro = null;
    }

}