package com.cirruslogic.hearingaids;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Wifi extends ActionBarActivity {

    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient;
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    private String mServerMessage;
    // sends message received notifications
    // while this is true, the server will continue running


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        arrayList = new ArrayList<String>();

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button send = (Button)findViewById(R.id.button10);
        Button Active= (Button)findViewById(R.id.button11);
        Button Mute= (Button)findViewById(R.id.button12);
        Button Disconnet = (Button)findViewById(R.id.button13);

        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.listView2);
        mAdapter = new MyCustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);


        // connect to the server
        new connectTask().execute("");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString();

                //add the text in the arrayList
                arrayList.add("User: " + message);

                //sends the message to the server
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(message);
                }

                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

        Active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "active";

                //add the text in the arrayList
                arrayList.add("User: " + message);

                //sends the message to the server
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(message);
                }

                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

        Mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "mute";

                //add the text in the arrayList
                arrayList.add("User: " + message);

                //sends the message to the server
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(message);
                }

                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

        Disconnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mTcpClient.stopClient();
            msg("Wifi Disconnect");
                startActivity(new Intent(Wifi.this, DeviceList.class));
            }
        });

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
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
}
