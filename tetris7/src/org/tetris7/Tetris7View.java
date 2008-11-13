package org.tetris7;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Tetris7View extends SurfaceView implements SurfaceHolder.Callback {
	
	class TetrisThread extends Thread {
		
		volatile boolean stayAlive = true;
		
		public TetrisThread(SurfaceHolder holder, Context context) {
		// TODO Auto-generated constructor stub
		}
		
		@Override
		public void run() {
			while (stayAlive) {
				updateState();
				drawStuff();
			}
		}
		
		private void updateState() {
		// TODO Auto-generated method stub
		
		}
		
		private void drawStuff() {
		// TODO Auto-generated method stub
		
		}
	}
	
	private final TetrisThread thread;
	
	public Tetris7View(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		// create thread only; it's started in surfaceCreated()
		thread = new TetrisThread(holder, context);
		
		setFocusable(true); // make sure we get key events
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	
	public void restoreState(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	
	}
	
}
