package ch.ethz.inf.vs.piremote.core;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

import ch.ethz.inf.vs.piremote.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private InetAddress ip;
    private int port;

    private EditText ipView;
    private EditText portView;
    private Button connect;
    public ClientCore clientCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ipView = (EditText) findViewById(R.id.IP);
        portView = (EditText) findViewById(R.id.port);
        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(checkInputs()){
            clientCore = new ClientCore(ip,port);
            clientCore.onCreate();
            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
        }

    }

    protected boolean checkInputs(){
        ipView.setError(null);
        portView.setError(null);

        boolean ret = true;

        String intermedIP = ipView.getText().toString();
        String intermedPort = portView.getText().toString();

        if(TextUtils.isEmpty(intermedIP)) {
            ipView.setError("Please Enter Valid IP Address");
            ipView.requestFocus();
            ret = false;
        } else if (!validIP(intermedIP)){
            ipView.setError("Invalid IP Address");
            ipView.requestFocus();
            ret = false;
        }

        if (TextUtils.isEmpty(intermedPort)) {
            portView.setError("Please Enter Valid Port");
            portView.requestFocus();
            ret = false;
        } else if (!validPort(intermedPort)){
            portView.setError("Invalid Port");
            portView.requestFocus();
            ret = false;
        }

        return ret;
    }
    protected boolean validIP(String intermedIP) {
        try {
            ip = InetAddress.getByName(intermedIP);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected boolean validPort(String intermedPort){
        try {
            port = Integer.valueOf(intermedPort);
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
