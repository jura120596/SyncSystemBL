package com.example.fordecosport.blut;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.fordecosport.AppStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

;

public class TransferDataThread extends Thread {
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;
    private byte[] buffer;


    public TransferDataThread(BluetoothSocket socket) {
        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        buffer = new byte[256];
        while (true) {
            try {
                readData();
            } catch (IOException e) {
                AppStatus.connectStatus = false;
                e.printStackTrace();
                break;
            }
        }
    }
    public String readData() throws IOException {
        int size = in.read(buffer);
        String message = new String(buffer, 0, size);
        Log.d("Bluetooth out", "Message : " + message);
        return message;
    }

    public void sendData(byte[] byteArray) throws IOException {
        out.write(byteArray);
    }

    public void closeStreams() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
    }
}
