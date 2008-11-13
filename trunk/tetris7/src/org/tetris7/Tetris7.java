package org.tetris7;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Tetris7 extends Activity {
	private Tetris7View view;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// tell system to use the layout defined in our XML file
		setContentView(R.layout.tetris_layout);
		
		// get handles to the LunarView from XML, and its LunarThread
		view = (Tetris7View) findViewById(R.id.tetris);
		
		// give the LunarView a handle to the TextView used for messages

		
		view.restoreState(savedInstanceState);
		
	}
	
	enum OptionsMenuItems {
		MenuStart,
		MenuPause,
		MenuResume,
		MenuStop,
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, OptionsMenuItems.MenuStart.ordinal(), 0, R.string.menu_start);
		menu.add(0, OptionsMenuItems.MenuPause.ordinal(), 0, R.string.menu_pause);
		menu.add(0, OptionsMenuItems.MenuResume.ordinal(), 0, R.string.menu_resume);
		menu.add(0, OptionsMenuItems.MenuStop.ordinal(), 0, R.string.menu_stop);
		
		return true;
	}
	
	/**
	 * Invoked when the user selects an item from the Menu.
	 * 
	 * @param item
	 *            the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == OptionsMenuItems.MenuStart.ordinal()) {
			start();
			return true;
		} else if (item.getItemId() == OptionsMenuItems.MenuPause.ordinal()) {
			pause();
			return true;
		} else if (item.getItemId() == OptionsMenuItems.MenuResume.ordinal()) {
			resume();
			return true;
		} else if (item.getItemId() == OptionsMenuItems.MenuStop.ordinal()) {
			stop();
			return true;
		}
		return false;
	}
	
	private void stop() {
	// TODO Auto-generated method stub
	
	}
	
	private void resume() {
	// TODO Auto-generated method stub
	
	}
	
	private void pause() {
	// TODO Auto-generated method stub
	
	}
	
	private void start() {
	// TODO Auto-generated method stub
	
	}
}