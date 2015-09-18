package com.cirruslogic.hearingaids;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import android.os.Handler;



public class HearingAidsControl extends ActionBarActivity {

    Button btnOn, btnOff,btnDis,btnF1,btnF2,btnSend;
    SeekBar Volum;
    TextView dB;
    TextView recChar;
    String address = null;
    EditText InputText;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    BluetoothDevice mBluetoothDevice;
    private boolean isBtConnected = false;
    private ConnectedThread mConnectedThread;
    String readMessage;

    //SPP UUID.
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_hearing_aids_control);

        //call the widgtes
        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        btnF1=(Button)findViewById(R.id.button5);
        btnF2=(Button)findViewById(R.id.button6);
        btnSend =(Button)findViewById(R.id.button7);
        Volum = (SeekBar)findViewById(R.id.seekBar);
        InputText =(EditText)findViewById(R.id.editText1);
        recChar = (TextView)findViewById(R.id.textView5);
        dB= (TextView)findViewById(R.id.textView4);

        new ConnectBT().execute(); //Call the class to connect
        //commands to be sent to bluetooth
        //Send command to kill the wifi process
   /*     if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("m".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
*/


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToPairedDevice(String.valueOf(InputText.getText()));//,mBluetoothDevice);
                InputText.setText("");
            }
        });
        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOn();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOff();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
        btnF1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Function1(); //do function1
            }
        });
        btnF2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Function2(); //co function2
            }
        });


        Volum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    dB.setText(String.valueOf(progress));
                    try
                    {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    }
                    catch (IOException e)
                    {

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }

    private void turnOff()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("mute".toString().getBytes());
                //btSocket.getOutputStream().write(mute);
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOn()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("active".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void Function1()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("F1".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void Function2()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("F2".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void sendDataToPairedDevice(String message)// , BluetoothDevice device)
    {
        byte[] toSend = message.getBytes();
        if (btSocket!=null)
        {
            try {
//                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
//                OutputStream mmOutStream = socket.getOutputStream();
//                mmOutStream.write(toSend);
                btSocket.getOutputStream().write(toSend);
                // Your Data is sent to  BT connected paired device ENJOY.
            }
            catch (IOException e) {

            }
        }

    }
    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hearing_aids_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(HearingAidsControl.this, "Connecting", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed.Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
                // stat a
                //Start a thread to listen
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();

            }
            progress.dismiss();
        }
    }




   private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String temp = new String(readBuf, 0, msg.arg1);
                    readMessage = temp;
                    recChar.setText(readMessage);
                  String mm = temp.substring(temp.length()-1, temp.length());
                    if (!(mm.equals(".")))
                        readMessage = readMessage + temp;
                    else {
                        recChar.setText(readMessage);
                        readMessage = "";
                    }
                    break;
            }
        }
    };

    private class ConnectedThread extends Thread {
       private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {

           mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                //Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                   // Read from the InputStream
                   bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (IOException e)
                {
                   //Log.e(TAG, "disconnected", e);
                    //connectionLost();
                    // Start the service over to restart listening mode
                    //BluetoothChatService.this.start();
                    break;
                }
            }
        }
    }
}

