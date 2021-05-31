package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    String IDViaje = " ";
    private String correoUserAutenticado;
    private int disponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__reservar__viaje);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();

        String valor = getIntent().getExtras().getString("precio");
        IDViaje = getIntent().getExtras().getString("idviaje");
        disponibles = Integer.parseInt(getIntent().getExtras().getString("cupos"));


        Button botonCalcularViaje = (Button) findViewById(R.id.botonCalcularTotal);
        botonCalcularViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reservas = (EditText)findViewById(R.id.campoReservas);
                EditText valorTotal = (EditText)findViewById(R.id.campoValorTotal);
                int total = Integer.parseInt(reservas.getText().toString()) * Integer.parseInt(valor);
                valorTotal.setText(String.valueOf(total));
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

    private void agregarReservas(){


        mRef = mDataBase.getReference(PathRoute);
        correoUserAutenticado = mAuth.getCurrentUser().getEmail();
        EditText res = (EditText)findViewById(R.id.campoReservas);
        int reservas =  Integer.parseInt(res.getText().toString());
        if(disponibles != 0 && disponibles >= reservas){
            disponibles = disponibles - reservas;
            mRef.child(IDViaje).child("cuposDisponibles").setValue(disponibles);

            mRef2 = FirebaseDatabase.getInstance().getReference("users/");
            mRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap :snapshot.getChildren() ){
                        String correo = snap.child("username").getValue(String.class);
                        if(correo.equals(correoUserAutenticado)){
                            //mRef.child("nombreConductor").setValue(snap.child("name").getValue(String.class));
                            mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("nombre").setValue(snap.child("name").getValue(String.class));
                            mRef.child(IDViaje).child("pasajeros").child(mAuth.getUid()).child("cantidadReservas").setValue(reservas);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(getBaseContext(), "No fue posible reservas cupo(s)", Toast.LENGTH_SHORT).show();
        }



    }
}