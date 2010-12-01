package com.awesome.battleships;

import com.awesome.battleships.BattleshipsView;
import com.awesome.battleships.BattleshipsView.BattleThread;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Battleships extends Activity {
	
	private static final int MENU_START = 0;
	private static final int MENU_STOP = 1;
	private BattleThread mThread;
	private BattleshipsView mView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battleship_layout);
        
        mView = (BattleshipsView) findViewById(R.id.battleships);
        mView.setTextView((TextView) findViewById(R.id.status_text));
        mThread = mView.getThread();
    }
}