package com.example.u_vallet.ui.rol;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_vallet.Activity_ExplorarViajes;
import com.example.u_vallet.Activity_MisCarros;
import com.example.u_vallet.R;


public class RolFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_roles, container, false);
        Button botonConductor = (Button) root.findViewById(R.id.Roles_BotonConductor);
        Button botonPasajero = (Button) root.findViewById(R.id.Roles_BotonPasajero);

        botonConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConductor = new Intent (v.getContext(), Activity_MisCarros.class);
                startActivity(intentConductor);
            }
        });

        botonPasajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPasajero = new Intent (v.getContext(), Activity_ExplorarViajes.class);
                startActivity(intentPasajero);
            }
        });
        return root;
    }
}