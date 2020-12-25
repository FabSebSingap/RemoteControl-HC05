package com.bluetooth.myremotehc05;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //Bluetooth Intialization
    //Get the HCxx Mac adress. For HC0x use AT command AT+ADDR? +ADDR:14:3:60932
    //HC-05 UUID "00001101-0000-1000-8000-00805F9B34FB"
    public final static String MODULE_MAC = "00:14:03:06:09:32";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bta;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    ConnectedThread btt = null;
    Button btnFwd, btnRight, btnLeft, btnBack, btnStop;
    TextView response;
    boolean lightflag = false;
    boolean relayFlag = true;
    public Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("[BLUETOOTH]", "Creating listeners");
        response = (TextView) findViewById(R.id.response);
        btnFwd = (Button) findViewById(R.id.forward_btn);
        btnRight= (Button) findViewById(R.id.right_btn);
        btnLeft= (Button) findViewById(R.id.left_btn);
        btnBack = (Button) findViewById(R.id.rear_btn);
        btnStop = (Button) findViewById(R.id.stop_btn);


        btnFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (!lightflag) {
                        String sendtxt = "F";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car move forward");
                        lightflag = true;
                    } else {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (!lightflag) {
                        String sendtxt = "R";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car turn Right");
                        lightflag = true;
                    } else {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (!lightflag) {
                        String sendtxt = "L";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car turn Left");
                        lightflag = true;
                    } else {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (!lightflag) {
                        String sendtxt = "B";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car move Back");
                        lightflag = true;
                    } else {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (!lightflag) {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = true;
                    } else {
                        String sendtxt = "S";
                        btt.write(sendtxt.getBytes());
                        response.setText("Car Stop");
                        lightflag = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
        bta = BluetoothAdapter.getDefaultAdapter();

        //if bluetooth is not enabled then create Intent for user to turn it on
        if(!bta.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }else{
            initiateBluetoothProcess();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            initiateBluetoothProcess();
        }
    }

    public void initiateBluetoothProcess(){

        if(bta.isEnabled()){

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]","Connected to: "+mmDevice.getName());
            }catch(IOException e){
                try{mmSocket.close();}catch(IOException c){return;}
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                        String txt = (String)msg.obj;
                        if(response.getText().toString().length() >= 30){
                            response.setText("");
                            response.setText(txt);
                        }else{
                            response.setText("\n" + txt);
                        }
                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mmSocket,mHandler);
            btt.start();
        }
    }

}