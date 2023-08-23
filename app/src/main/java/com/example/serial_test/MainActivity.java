package com.example.serial_test;



import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {


private Thermometer thermometer;
private float temperature;
private Timer timer;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private TextView mTextView;
    private Handler mHandler;
    private Thread mThread;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MESSAGE_READ = 1;



    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thermometer = (Thermometer) findViewById(R.id.thermometer);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        String message = (String) msg.obj;
                        temperature = Float.parseFloat(message);
                        System.out.println(temperature);
                        //mTextView.setText(message);
                        thermometer.setCurrentTemp(temperature);
                        getSupportActionBar().setTitle("Aktualna temperatura: " + temperature + "°C");
                        break;
                }
            }
        };
        // Inicjalizacja BluetoothAdaptera
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Ustawienie adresu urządzenia Bluetooth
        String deviceAddress = "43:43:A2:12:1F:AC"; // adres urządzenia
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        // Utworzenie wątku do obsługi Bluetooth
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Utworzenie połączenia z urządzeniem Bluetooth

                    mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    mSocket.connect();

                    // Otwarcie strumienia wejściowego do odbierania danych
                    mInputStream = mSocket.getInputStream();

                    // Pętla odbierająca dane
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while (true) {
                        bytes = mInputStream.read(buffer);
                        String message = new String(buffer, 0, bytes);
                        mHandler.obtainMessage(MESSAGE_READ, message).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Nic nie odebrano!");
                    Toast.makeText(MainActivity.this, "Utracono połączenie z Pico W", Toast.LENGTH_LONG).show();
                }
            }
        });
        mThread.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        // Zamknięcie strumienia wejściowego i połączenia Bluetooth
        try {
            mInputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}