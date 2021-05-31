package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Pago extends AppCompatActivity {

    private DatabaseReference mRef;

    private TextView valor;
    private TextView celular;
    private Button continuar;

    public static final String PATH_USER = "users/";

    private String uidConductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__pago);

        valor = (TextView)findViewById(R.id.tvValor);
        celular = (TextView)findViewById(R.id.tvCelular);
        continuar = (Button) findViewById(R.id.buttonContinuar);

        String costo = getIntent().getExtras().getString("costo");
        valor.setText("$ "+costo);
        String key = getIntent().getExtras().getString("llaveReserva");
        uidConductor = getIntent().getExtras().getString("conductor");
        Log.d("USPRUEBA", "uid Pago: "+uidConductor);
        getData();
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Activity_Mi_Viaje_Pasajero.class);
                intent.putExtra("llaveReserva" , key);
                intent.putExtra("uidconductor",uidConductor);
                intent.putExtra("costo", costo);
                startActivity(intent);
            }
        });
    }
    private void getData(){
        mRef = FirebaseDatabase.getInstance().getReference(PATH_USER);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long telefono = snapshot.child(uidConductor).child("telefono").getValue(Long.class);
                celular.setText(String.valueOf(telefono));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}