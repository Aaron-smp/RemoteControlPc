package com.enka.prueba.net;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.enka.prueba.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConexionCliente {

    private String ipServidor;
    private Socket socketCliente;
    private PrintWriter salida;
    private BufferedReader entrada;
    private boolean running;
    private ExecutorService executorService;
    private Activity mainActivity;
    private Thread hiloDeRed;

    public ConexionCliente(Activity activity){
        this.running = false;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainActivity = activity;
        this.hiloDeRed = new Thread(() -> {
            Broadcasting broadcasting = new Broadcasting();
            broadcasting.start();
            try {
                broadcasting.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.ipServidor = broadcasting.getIpRecepcionada();
            iniciarConexion();
        });
        hiloDeRed.start();
    }

    public void iniciarConexion(){
        try {
            try {
                this.socketCliente = new Socket(ipServidor, 7777);
                this.salida = new PrintWriter(socketCliente.getOutputStream(), true);
                this.entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                this.running = true;
                informarConexion();
            } catch (IOException e) {
                alertMessage("No se ha podido sincronizar con el servidor.");
            }

            enviarMensaje(CodigosTransferencia.ACTUAL_VOLUME);
            String line;
            while (running && (line = entrada.readLine()) != null) {
                JSONObject response = new JSONObject(line);
                System.out.println("Mensaje recibido: ".concat(line));
                switch (response.getString(CodigosTransferencia.ID)){
                    case CodigosTransferencia.ACTUAL_VOLUME:
                        String volumenActual = response.getString(CodigosTransferencia.ACTUAL_VOLUME);
                        TextView volumeLevel = mainActivity.findViewById(R.id.numLevel);
                        volumeLevel.setText(volumenActual);
                }
            }
        } catch (IOException e) {
            TextView textConectivity = mainActivity.findViewById(R.id.conection);
            textConectivity.setText("Sin conexión");
            reiniciarConexion();
        } catch (JSONException e) {
            throw new RuntimeException("Problema con la creación del JSON de respuesta,  " + e);
        }
    }

    private void informarConexion() throws IOException {
        enviarMensaje("Cliente iniciado desde dispositivo móvil.");
        TextView textConectivity = mainActivity.findViewById(R.id.conection);
        textConectivity.setText("Conectado a ".concat(ipServidor));
    }

    public void enviarMensaje(String message) {
        if (running && salida != null) {
           executorService.submit(() -> {
               salida.println(message);
           });
        }
    }

    private void alertMessage(String message){
        mainActivity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("Aviso");
            builder.setMessage(message);

            builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public void reiniciarConexion(){
        //TODO Se reinicia el hilo para reintentar la conexión
        hiloDeRed = new Thread(() -> {
            iniciarConexion();
        });
        hiloDeRed.start();
    }

    public void closeConnection() {
        running = false;
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socketCliente != null) socketCliente.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
