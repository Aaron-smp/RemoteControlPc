package com.enka.prueba.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Broadcasting extends Thread{
    private String ipRecepcionada;

    public Broadcasting(){
    }

    public String getIpRecepcionada() {
        return ipRecepcionada;
    }

    @Override
    public void run(){
        int port = 8888;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            // Habilitar el broadcast
            socket.setBroadcast(true);
            // Mensaje de descubrimiento que será enviado en broadcast
            String message = "¿Hay algún servidor disponible?";
            byte[] buffer = message.getBytes();

            // Dirección de broadcast
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

            // Crear el paquete para enviar el broadcast
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, port);
            socket.send(packet);

            // Preparar el buffer para recibir la respuesta
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

            // Esperar respuesta del servidor
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            this.ipRecepcionada = response;

            // Cerrar el socket
            socket.close();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
