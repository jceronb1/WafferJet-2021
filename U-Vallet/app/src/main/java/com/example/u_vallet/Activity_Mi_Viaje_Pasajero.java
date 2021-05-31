package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_Mi_Viaje_Pasajero extends AppCompatActivity {
    //------------------------------------------------
    //                  Attributes
    //------------------------------------------------
    // Form fields
    private TextView idViaje;
    private TextView origen;
    private TextView destino;
    private TextView puntoPartida;
    private TextView horaPartida;
    // Firebase
    private FirebaseAuth userAuth;
    private String currentUserUid;
    private String currentUserId;
    private DatabaseReference mDatabase;
    // Intent
    String tripReservationUid;

    //------------------------------------------------
    //                  On Create
    //------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__mi__viaje__pasajero);

        //----------------- Intent -----------------
        tripReservationUid = "-Mb-xsX7tvsQ8AiR6K2c"; // getIntent().getStringExtra("llaveReserva");

        //----------------- Firebase -----------------
        userAuth = FirebaseAuth.getInstance();
        currentUserUid = userAuth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid);

        //----------------- Form fields -----------------
        idViaje = findViewById(R.id.miViajeIDText);
        origen = findViewById(R.id.viajeOrigenText);
        destino = findViewById(R.id.miViajeDestinoText);
        puntoPartida = findViewById(R.id.editPuntoPartida);
        horaPartida = findViewById(R.id.editHoraPartida);

        //----------------- Fill form fields -----------------
        ValueEventListener routeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Reservation details
                String _origen = dataSnapshot.child("originDirection").getValue( String.class );
                String _destino = dataSnapshot.child("destinationDirection").getValue( String.class );
                String _puntoPartida = dataSnapshot.child("puntoEncuentro").getValue( String.class );
                String _horaPartida = dataSnapshot.child("horaViaje").getValue( String.class );
                // Set values to form fields
                idViaje.setText( tripReservationUid );
                origen.setText( _origen );
                destino.setText( _destino );
                puntoPartida.setText( _puntoPartida );
                horaPartida.setText( _horaPartida );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.i("loadPost:onCancelled", databaseError.toException().toString());
            }
        };
        mDatabase.addValueEventListener(routeListener);

        //----------------- Button -----------------
        Button ExplorarViaje = findViewById(R.id.buttonMiViajeMV4);
        ExplorarViaje.setOnClickListener(v -> startActivity(new Intent(v.getContext(), Activity_ExplorarViajes.class)));

        //----------------- Cancel trip -----------------
        Button cancelarViaje = findViewById(R.id.botonCancelarViaje);
        cancelarViaje.setOnClickListener( view -> {
            // If user press this button, the reserved seats, shuld be returned to
            // the original trip in the DB.
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid).child("viajeActivo");
            mDatabase.setValue("false");

            Toast.makeText(this, "Se ha cancelado el viaje con éxito", Toast.LENGTH_SHORT).show();
        });

        //----------------- Payment details -----------------
        Button datosPago = findViewById(R.id.MiViaje_VerDatosPagoBtn);
        datosPago.setOnClickListener( view -> {
            Toast.makeText(this, "Se miran los datos de pago", Toast.LENGTH_SHORT).show();
        });
    }

    //------------------------------------------------
    //                  Menu
    //------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity__navegation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.menuCambiarRol){
            Intent intent = new Intent(this, Activity_Roles.class);
            startActivity(intent);
        }else if (itemClicked == R.id.menuEditarPerfil){
            Intent intent = new Intent( this, Activity_EditarPerfil.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}