package com.enka.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.enka.prueba.net.CodigosTransferencia;
import com.enka.prueba.net.ConexionCliente;

public class MainActivity extends AppCompatActivity {

    private ConexionCliente conexionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexionCliente = new ConexionCliente(this);
        eventosVolumen();
        eventoShutdownPc();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        conexionCliente.closeConnection();
    }

    private void eventoShutdownPc(){
        Button shutDownButton = findViewById(R.id.btnApagar);
        shutDownButton.setOnClickListener((v) -> {
            conexionCliente.enviarMensaje(CodigosTransferencia.SHUTDOWN_PC);
        });
    }

    private void eventosVolumen(){
        Button upVolumeButton = findViewById(R.id.subir);
        TextView volumen = findViewById(R.id.numLevel);
        upVolumeButton.setOnClickListener((v) -> {
            conexionCliente.enviarMensaje(CodigosTransferencia.VOLUME_UP);
            int numVolume = Integer.parseInt(volumen.getText().toString())+2;
            volumen.setText(String.valueOf(numVolume));
        });

        Button downVolumeButton = findViewById(R.id.bajar);
        downVolumeButton.setOnClickListener((v) -> {
            conexionCliente.enviarMensaje(CodigosTransferencia.VOLUME_DOWN);
            int numVolume = Integer.parseInt(volumen.getText().toString())-2;
            volumen.setText(String.valueOf(numVolume));
        });

        Button reconectionButton = findViewById(R.id.reconectar);
        reconectionButton.setOnClickListener((v) -> {
            conexionCliente.reiniciarConexion();
        });
    }
}