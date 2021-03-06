package com.example.miniact6;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleSocket extends Activity implements View.OnClickListener {

    private static final String CLASSTAG = SimpleSocket.class.getSimpleName();

    private EditText ipAddress;
    private EditText port;
    private EditText socketInput;
    private TextView socketOutput;
    Handler h;
    //private static final String SERVER_IP = "10.0.2.2";

    @Override
    public void onCreate(final Bundle icicle){
        super.onCreate(icicle);
        this.setContentView(R.layout.simple_socket);

        Button socketButton;

        this.ipAddress = findViewById(R.id.socket_ip);
        this.port = findViewById(R.id.socket_port);
        this.socketInput = findViewById(R.id.socket_input);
        this.socketOutput = findViewById(R.id.socket_output);
        socketButton = findViewById(R.id.socket_button);

        this.h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case 0:
                        socketOutput.setText((String) msg.obj);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        socketButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        socketOutput.setText("");
        final Thread tr = new Thread(){
            public void run(){
                try{
                    //InetAddress serverAddr = InetAddress.getByName(ipAddress.getText().toString());
                    InetAddress serverAddr = InetAddress.getByName(InetAddress.getLocalHost().toString());
                            String output = callSocket(serverAddr, port.getText().toString(), socketInput.getText().toString());
                    Message lmsg = new Message();
                    lmsg.obj = output;
                    lmsg.what = 0;
                    SimpleSocket.this.h.sendMessage(lmsg);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        };
        tr.start();
    }

    private String callSocket(final InetAddress ad, final String port, final String socketData) {
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        String output = null;

        try{
            socket = new Socket(ad, Integer.parseInt(port));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input = socketData;
            writer.write(input + "\n", 0, input.length() + 1);
            writer.flush();

            output = reader.readLine();
            Log.d("NetworkExplorer".toString()," " + SimpleSocket.CLASSTAG + " output - " + output);

            writer.write("EXIT\n", 0, 5);
            writer.flush();
        } catch(IOException e){
            Log.d("NetworkExplorer".toString()," " + SimpleSocket.CLASSTAG + " IOException calling socket", e);
        } finally {
            try{
                if(writer!=null) writer.close();
            } catch (IOException e){}
            try{
                if(writer!=null) reader.close();
            } catch (IOException e){}
            try{
                if(writer!=null) socket.close();
            } catch (IOException e){}
        }
        return output;
    }
}
