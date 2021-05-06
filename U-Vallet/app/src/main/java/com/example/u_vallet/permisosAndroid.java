package com.example.u_vallet;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class permisosAndroid {
    public static void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        int permission = ContextCompat.checkSelfPermission(context, permiso);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, "Se requiere habilitar los permisos", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }
}
