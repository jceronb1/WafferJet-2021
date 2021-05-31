package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Reservar_Viaje extends AppCompatActivity {

    private ArrayList<Viaje> viajes = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private DatabaseReference mRef;
    private DatabaseReference mRef2;
    public static final String PathRoute = "routes/";

    String IDViaje;
    private String correoUserAutenticado;
    private int disponibles;
    private String uidconductor;
    EditText reservas;
    TextView ubicacionPasajero;
    EditText valorTotal;
    String lat;
    String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__reservar__viaje);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        String direccion = getIntent().getExtras().getString("direccion");
        lat = getIntent().getExtras().getString("Lat");
        lng = getIntent().getExtras().getString("Lng");
        Log.d("USPRUEBAF", String.valueOf(lat) + "/"+String.valueOf(lng));
        String valor = getIntent().getExtras().getString("precio");
        String res = getIntent().getExtras().getString("reserva");
        IDViaje = getIntent().getExtras().getString("idviaje");
        disponibles = Integer.parseInt(getIntent().getExtras().getString("cuposDisponibles"));
        Log.d("USPUEBA", "Disponibles:" + String.valueOf(disponibles));
        uidconductor = getIntent().getExtras().getString("uidconductor");
        Log.i("IDCONDUCTOR:",uidconductor);
        reservas = (EditText)findViewById(R.id.campoReservas);
        reservas.setText(res);
        valorTotal = (EditText)findViewById(R.id.campoValorTotal);
        ubicacionPasajero = (TextView)findViewById(R.id.tvUbicacionPasajero);
        ubicacionPasajero.setText(direccion);
        valorTotal.setEnabled(false);


        Button botonCalcularViaje = (Button) findViewById(R.id.botonCalcularTotal);
        botonCalcularViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int total = Integer.parseInt(reservas.getText().toString()) * Integer.parseInt(valor);
                valorTotal.setText(String.valueOf(total));
            }
        });

        Button buttonUbicacion = (Button)findViewById(R.id.buttonSeleccionarUbicacion);
        buttonUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Activity_ReservarViaje_Maps.class);
                Log.d("USPUEBA", String.valueOf(disponibles));
                String res = reservas.getText().toString();
                intent.putExtra("reserva", res);
                intent.putExtra("uidconductor", uidconductor);
                intent.putExtra("precio", valor);
                intent.putExtra("Viaje", IDViaje);
                String dispo = String.valueOf(disponibles);
                Log.d("USPUEBA", dispo);
                intent.putExtra("cuposDisponibles", dispo);
                startActivity(intent);
            }
        });

        Button botonReservar = (Button) findViewById(R.id.botonReservar);
        botonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarReservas();
            }
        });

        Button botonCancelar = (Button) findViewById(R.id.cancelarReserva);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getBaseContext(), Activity_ExplorarViajes.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateForm(){
        boolean valid = true;
        String disponible = reservas.getText().toString();
        String valor = valorTotal.getText().toString();
        String direccion = ubicacionPasajero.getText().toString();

        if(TextUtils.isEmpty(disponible)){
            reservas.setError("Requerido");
            valid = false;
        }else{
            reservas.setError(null);
        }
        if(TextUtils.isEmpty(valor)){
            valorTotal.setError("Requerido");
            valid = false;
        }else{
            valorTotal.setError(null);
        }
        if(TextUtils.isEmpty(direccion)){
            ubicacionPasajero.setError("Requerido");
            valid = false;
        }else{
            ubicacionPasajero.setError(null);
        }
        return valid;
    }

    private void agregarReservas(){
        if(validateForm()) {
            mRef = mDataBase.getReference(PathRoute);
            double lati = Double.parseDouble(lat);
            double longi = Double.parseDouble(lng);
            correoUserAutenticado = mAuth.getCurrentUser().getEmail();
            EditText res = (EditText) findViewById(R.id.campoReservas);
            int reservas = Integer.parseInt(res.getText().toString());
            if (disponibles != 0 && disponibles >= reservas) {
                disponibles = disponibles - reservas;
                mRef.child(IDViaje).child("cuposDisponibles").setValue(disponibles);

                mRef2 = FirebaseDatabase.getInstance().getReference("users/");
                mRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String correo = snap.child("username").getValue(String.class);
                            if (correo.equals(correoUserAutenticado)) {
                                mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("nombre").setValue(snap.child("name").getValue(String.class));
                                mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("cantidadReservas").setValue(reservas);
                                mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("latitude").setValue(lati);
                                mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("longitude").setValue(longi);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                mRef2.child(mAuth.getUid()).child("viajeActivo").setValue("true");
                mRef2.child(uidconductor).child("viajeActivo").setValue("true");
            } else {
                Toast.makeText(getBaseContext(), "No fue posible reservas cupo(s)", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent( getBaseContext(), Activity_Pago.class);
            intent.putExtra("costo", valorTotal.getText().toString());
            intent.putExtra("llaveReserva", IDViaje);
            intent.putExtra("conductor", uidconductor);
            startActivity(intent);
        }



    }
}