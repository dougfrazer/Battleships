package com.awesome.battleships;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

import com.awesome.battleships.BattleshipsView;
import com.awesome.battleships.BattleshipsView.BattleThread;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Battleships extends Activity {
	
	private static final int MENU_START = 0;
	private static final int MENU_STOP = 1;
	private BattleThread mThread;
	private BattleshipsView mView;
	private EditText mUsername;
	private EditText mPassword;
	private EditText mServer;
	private BattleshipsSocket mSocket;
	private Button mOKButton;
	private Button mCancelButton;
	private TextView mStatusText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.welcome_layout);
        
        mUsername = (EditText) findViewById(R.id.username_entry);
        mServer = (EditText) findViewById(R.id.server_entry);
        mPassword = (EditText) findViewById(R.id.password_entry);
        mOKButton = (Button) findViewById(R.id.ok);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mStatusText = (TextView) findViewById(R.id.status_text);
        
        mOKButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(createSocket() == false)
            		return;
            	else
            		setGame();
            }});
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            }});
    }
    
    private boolean createSocket () {
    	/*if (mUsername.getText().toString().equalsIgnoreCase("")) {
    		mStatusText.setText("Please fill in a username");
    		mStatusText.setVisibility(1);
    		return false;
    	}
    	if (mPassword.getText().toString().equalsIgnoreCase("")) {
    		mStatusText.setText("Please fill in a password");
    		mStatusText.setVisibility(1);
    		return false;
    	}
    	if (mServer.getText().toString().equalsIgnoreCase("")) {
    		mStatusText.setText("Please fill in a server");
    		mStatusText.setVisibility(1);
    		return false;
    	}*/
    	try {
    		mSocket = new BattleshipsSocket();
    	} catch (UnknownHostException e) {
    		mStatusText.setText("Unknown Host");
    		mStatusText.setVisibility(1);
    		return false;
    	} catch (IOException e) {
    		mStatusText.setText("General IO Error");
    		mStatusText.setVisibility(1);
    		return false;
    	} catch (SecurityException e) {
    		mStatusText.setText("Got a security error");
    		mStatusText.setVisibility(1);
    		return false;
    	}
    	mStatusText.setVisibility(0);
    	return true;
    }
    
    public void setGame () {
        setContentView(R.layout.battleship_layout);
        mView = (BattleshipsView) findViewById(R.id.battleships);
        mView.setTextView((TextView) findViewById(R.id.status_text));
        mThread = mView.getThread();
    }
}