package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.valueOf;

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
    private DatabaseReference mDatabase;
    // Intent
    String tripReservationUid;
    // Buttons
    Button datosPago, cancelarViaje;

    //------------------------------------------------
    //                  On Create
    //------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__mi__viaje__pasajero);

        //----------------- Form fields -----------------
        idViaje = findViewById(R.id.miViajeIDText);
        origen = findViewById(R.id.viajeOrigenText);
        destino = findViewById(R.id.miViajeDestinoText);
        puntoPartida = findViewById(R.id.editPuntoPartida);
        horaPartida = findViewById(R.id.editHoraPartida);

        //----------------- Firebase -----------------
        userAuth = FirebaseAuth.getInstance();
        currentUserUid = userAuth.getUid();

        //----------------- Trip information -----------------
        // Search for trip UID
        getTripUid();

        //----------------- Button -----------------
        Button ExplorarViaje = findViewById(R.id.buttonMiViajeMV4);
        ExplorarViaje.setOnClickListener(v -> startActivity(new Intent(v.getContext(), Activity_ExplorarViajes.class)));

        //----------------- Cancel trip -----------------
        cancelarViaje = findViewById(R.id.botonCancelarViaje);
        cancelarViaje.setOnClickListener( view -> {
            // If user press this button, the reserved seats, shuld be returned to
            // the original trip in the DB.
            Log.i("Tuid", tripReservationUid);
            // Change 'ViajeAcitvo' in user
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid).child("viajeActivo");
            mDatabase.setValue("false");

            // Return reserved seats in trip
            mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("pasajeros").child(currentUserUid).child("cantidadReservas");
            mDatabase.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String value = valueOf(task.getResult().getValue());
                    int reservedSeats = Integer.parseInt(value);

                    mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("cuposDisponibles");
                    mDatabase.get().addOnCompleteListener(task12 -> {
                        if (!task12.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task12.getException());
                        }
                        else {
                            String value1 = valueOf(task12.getResult().getValue());
                            int availableSeats = Integer.parseInt(value1);
                            int seats = reservedSeats + availableSeats;
                            mDatabase.setValue(seats);

                            mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("pasajeros").child(currentUserUid);
                            mDatabase.removeValue();
                        }
                    });
                }
            });

            Toast.makeText(this, "Se ha cancelado el viaje con éxito", Toast.LENGTH_SHORT).show();
        });

        //----------------- Payment details -----------------
        datosPago = findViewById(R.id.MiViaje_VerDatosPagoBtn);
        datosPago.setOnClickListener( view -> {
            // Get payment info from DB
            mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("pasajeros").child(currentUserUid).child("cantidadReservas");
            mDatabase.get().addOnCompleteListener(task22 -> {
                if (!task22.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task22.getException());
                } else {
                    Log.i("DATAP", task22.getResult().getValue().toString());
                    int reservedSeats = Integer.parseInt(String.valueOf(task22.getResult().getValue()));

                    mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("valorViaje");
                    mDatabase.get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task1.getException());
                        }
                        else {
                            Log.i("DATA1", task1.getResult().getValue().toString());
                            int tripCost = Integer.parseInt(String.valueOf(task1.getResult().getValue()));
                            String value = String.valueOf(tripCost * reservedSeats);

                            mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(tripReservationUid).child("uidConductor");
                            mDatabase.get().addOnCompleteListener(task11 -> {
                                if (!task11.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task11.getException());
                                }
                                else {
                                    Log.i("DATA2", task11.getResult().getValue().toString());
                                    String uidDriver = String.valueOf(task11.getResult().getValue());
                                    Intent pago = new Intent(view.getContext(), Activity_Pago.class);
                                    pago.putExtra("costo", value);
                                    pago.putExtra("llaveReserva", tripReservationUid);
                                    pago.putExtra("conductor", uidDriver);
                                    startActivity(pago);
                                }
                            });
                        }
                    });
                }
            });
        });

    }

    //------------------------------------------------
    //                 Methods
    //------------------------------------------------
    private void getTripUid() {
        mDatabase = FirebaseDatabase.getInstance().getReference("routes");
        mDatabase.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot d : task.getResult().getChildren()) {
                    Boolean ok = false;
                    for (DataSnapshot pasajero : d.child("pasajeros").getChildren()) {
                        String p = String.valueOf(pasajero.getKey());
                        if (currentUserUid.equals(p)) {
                            tripReservationUid = p;
                            Log.i("DATA", "encontrada");

                            // Fill form fields
                            // Get Reservation details
                            DataSnapshot dataSnapshot = d;
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
                            // Return value
                            ok = true;
                            break;
                        }
                    }
                    if (ok) {
                        break;
                    }
                }
            }
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
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, Activity_Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}