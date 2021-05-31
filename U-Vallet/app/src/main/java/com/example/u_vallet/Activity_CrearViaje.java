package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Activity_CrearViaje extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDataBase2;
    private DatabaseReference mRef2;
    private String correoUserAutenticado;

    private EditText origenET;
    private EditText destinoET;
    private EditText cuposET;
    private EditText puntoDeEncuentroET;
    private EditText valorViajeET;
    private Button botonHora;
    private EditText campoHora;

    private static final int NOTIFICATION_CODE = 200;
    private static final String NOTIFICATION_CHANNEL = "NOTIFICATION";
    private boolean initialState = true;


    private int dia,mes,anio,hora,minutos;
    private TimePickerDialog.OnTimeSetListener mTimeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_viaje);


        String direccionO = getIntent().getExtras().getString("direccionO_2");
        String direccionD = getIntent().getExtras().getString("direccionD_2");
        String placaCarro = getIntent().getExtras().getString("placa");
        TextView placa = (TextView)findViewById(R.id.campoPlacaCarro);
        //Log.i("PLACA",placaCarro);
        placa.setText(placaCarro);
        //String origenAux[] = origen.split(",");
        Button botonMiViaje = (Button) findViewById(R.id.buttonMiViajeCV);
        Button botonMisCarros = (Button) findViewById(R.id.buttonMisCarrosCV);
        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViaje);

        origenET = (EditText) findViewById(R.id.origen);
        destinoET = (EditText)findViewById(R.id.destino);
        cuposET = (EditText)findViewById(R.id.editCupos);
        puntoDeEncuentroET = (EditText)findViewById(R.id.editPuntoEncuentro);
        valorViajeET = (EditText)findViewById(R.id.editValorViaje);

        origenET.setText(direccionO);
        destinoET.setText(direccionD);
        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Conductor.class);
                startActivity(intentMiViaje);
            }
        });

        botonMisCarros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMisCarros = new Intent(v.getContext(), Activity_MisCarros.class);
                startActivity(intentMisCarros);
            }
        });

        //obtener hora partida
        botonHora = (Button)findViewById(R.id.botonHora);
        campoHora = (EditText)findViewById(R.id.editHoraPartida);
        botonHora.setOnClickListener(this);

        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
                createNotificationChannel();
                loadViaje();


            }
        });

    }

    private void loadViaje(){
        String key = getIntent().getExtras().getString("Route_2");
        mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(key);
        mRef2 = FirebaseDatabase.getInstance().getReference("users/");
        mAuth = FirebaseAuth.getInstance();
        Log.i("CORREO",mAuth.getCurrentUser().getEmail());
        correoUserAutenticado = mAuth.getCurrentUser().getEmail();
        if(validateForm()) {

            mRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap :snapshot.getChildren() ){
                        String correo = snap.child("username").getValue(String.class);
                        if(correo.equals(correoUserAutenticado)){
                            mDatabase.child("nombreConductor").setValue(snap.child("name").getValue(String.class));
                            mRef2.child(snap.child("uid").getValue(String.class)).child("viajeActivo").setValue("true");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mDatabase.child("originDirection").setValue(origenET.getText().toString());
            mDatabase.child("destinationDirection").setValue(destinoET.getText().toString());
            mDatabase.child("cuposDisponibles").setValue(Double.parseDouble(cuposET.getText().toString()));
            mDatabase.child("horaViaje").setValue(campoHora.getText().toString());
            mDatabase.child("puntoEncuentro").setValue(puntoDeEncuentroET.getText().toString());
            mDatabase.child("valorViaje").setValue(Double.parseDouble(valorViajeET.getText().toString()));
            mDatabase.child("status").setValue("active");
            Intent intentMiViajeConduc = new Intent(getApplicationContext(), Activity_Mi_Viaje_Conductor.class);
            intentMiViajeConduc.putExtra("Route_3", key);
            startActivity(intentMiViajeConduc);
        }
    }

    private boolean validateForm(){
        boolean valid = true;

        String cupos = cuposET.getText().toString();
        String hora =  campoHora.getText().toString();
        String puntoE = puntoDeEncuentroET.getText().toString();
        String valor = valorViajeET.getText().toString();
        if(TextUtils.isEmpty(cupos)){
            cuposET.setError("Requerido");
            valid = false;
        }else{
            cuposET.setError(null);
        }
        if(TextUtils.isEmpty(hora)){
            campoHora.setError("Requerido");
            valid = false;
        }else{
            campoHora.setError(null);
        }
        if(TextUtils.isEmpty(puntoE)){
            puntoDeEncuentroET.setError("Requerido");
            valid = false;
        }else{
            puntoDeEncuentroET.setError(null);
        }
        if(TextUtils.isEmpty(valor)){
            valorViajeET.setError("Requerido");
            valid = false;
        }else{
            valorViajeET.setError(null);
        }

        return valid;
    }

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
        }
        return super.onOptionsItemSelected(item);
    }

    //boton para la hora partida
    @Override
    public void onClick(View v) {
        if(v == botonHora){
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minutos = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if(minute <10)
                        campoHora.setText(hourOfDay+":"+"0"+minute);
                    else
                        campoHora.setText(hourOfDay+":"+minute);
                }
            },hora,minutos,false);
            timePickerDialog.show();
        }
    }
    private void createNotification(){
        Intent miViajeConductor = new Intent(this, Activity_Mi_Viaje_Conductor.class);
        miViajeConductor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, miViajeConductor, 0);
        String notificationMessage = " Se creo el viaje exitosamente";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        notificationBuilder.setContentTitle("NOTIFICACION DE CONDUCTOR");
        notificationBuilder.setColor(Color.BLUE);
        notificationBuilder.setContentText(notificationMessage);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0,notificationBuilder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION";
            String description = "NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }
}