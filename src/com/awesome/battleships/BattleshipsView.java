package com.awesome.battleships;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

class BattleshipsView extends SurfaceView implements SurfaceHolder.Callback {
	class BattleThread extends Thread {
		private Drawable mShip;
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		private int mShipLocX = 120;
		private int mShipLocY = 100;
		private LinkedList<Bullet> bullets;
		private TextView mStatusText;
		private static final int STATE_RUNNING = 0;
		private static final int STATE_PAUSED = 1;
		private static final double MIN_BULLET_FIRE_TIME = 400;
		
		
		// The speed of stuff measured in pixels/second
		private static final int SHIP_SPEED = 10;
		private static final int BULLET_SPEED = 20;
		
		private boolean mMoveLeft = false;
		private boolean mMoveRight = false;
		private boolean mMoveUp = false;
		private boolean mMoveDown = false;
		private boolean mFiring = false;
		
		private int mState;
		private double mLastTime = 0;
		private double mLastFired = 0;
		
		public BattleThread (SurfaceHolder holder, Context context, Handler handler) {
			Resources res = context.getResources();
			mShip = res.getDrawable(R.drawable.ship);
			mSurfaceHolder = holder; 
			bullets = new LinkedList<Bullet>();
		}
		
		public void setTextView(TextView t) {
			mStatusText = t;
		}
		
		public void doStart() {
			mLastTime = System.currentTimeMillis() + 100;
			mState = STATE_RUNNING;
		}
		
		public void doDraw(Canvas canvas) {
			// Set all the proper parameters
			Paint bullet_paint = new Paint();
			bullet_paint.setColor(Color.BLACK);
			mShip.setBounds(mShipLocX,
							mShipLocY,
							mShipLocX + mShip.getIntrinsicWidth(),
							mShipLocY + mShip.getIntrinsicHeight());
			
			// Draw everything from bottom up
			canvas.drawColor(Color.BLUE);
			mShip.draw(canvas);
			
		/*	int i = 0;
			for (Bullet bullet : bullets) {
				if(bullet.x > canvas.getWidth()) 
					bullets.remove(i);
				i++;
			}
			*/
			for (Bullet bullet : bullets) {	
				canvas.drawCircle(bullet.x, bullet.y, 3, bullet_paint);
			}
			
			canvas.restore();
		}
		
		@Override
		public void run () {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                    	if (mState == STATE_RUNNING) updateShip();
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
		}
		
		public void setRunning(boolean b) {
			mRun = b;
		}

		private void updateShip () {
			long now = System.currentTimeMillis();
			if (mLastTime > now) return;
			double elapsed = (now - mLastTime)/1000.0;
			double d = elapsed * SHIP_SPEED;
			double b = elapsed * BULLET_SPEED;
			// Need to come up with a better way of handling speed,
			// doubles are too low and you cant move in positive
			// directions
			d = 4;
			b = 8;
			// Do some sanity checking/fixing
			if (mMoveLeft && mMoveRight) {
				mMoveLeft = false;
				mMoveRight = false;
			} else if (mMoveUp && mMoveDown) {
				mMoveUp = false;
				mMoveDown = false;
			}
			
			// Process movement, allow for diagonal movement
			if(mMoveLeft && mMoveUp) {          mShipLocY -= d/2; mShipLocX -= d/2; }
			else if (mMoveLeft && mMoveDown) {  mShipLocY += d/2; mShipLocX -= d/2; }
			else if (mMoveRight && mMoveUp) {   mShipLocY -= d/2; mShipLocX += d/2; }
			else if (mMoveRight && mMoveDown) { mShipLocY += d/2; mShipLocX += d/2; }
			else if (mMoveLeft) {               mShipLocX -= d; }
			else if (mMoveRight) {              mShipLocX += d; }
			else if (mMoveUp) {                 mShipLocY -= d; }
			else if (mMoveDown) {               mShipLocY += d; }
			
			if(mShipLocX < 0) mShipLocX = 0;
			if(mShipLocY < 0) mShipLocY = 0;
			
			
			
			// Fire some bullets
			if (mFiring && now - mLastFired > MIN_BULLET_FIRE_TIME) {
				bullets.add(new Bullet(mShipLocX + mShip.getIntrinsicWidth(),
									   mShipLocY + mShip.getIntrinsicHeight()/2,
									   100));
				mLastFired = now;
			}
			for (Bullet bullet : bullets) {
				bullet.x += b;
			}

			mLastTime = now;
			
		}
		
		boolean doKeyDown(int keyCode, KeyEvent msg) {
			synchronized (mSurfaceHolder) {
				mStatusText.setText(keyCode + " down");
				if(keyCode == KeyEvent.KEYCODE_DPAD_UP) mMoveUp = true;
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) mMoveLeft = true;
				if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) mMoveRight = true; 				
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) mMoveDown = true;
				if(keyCode == KeyEvent.KEYCODE_SPACE) mFiring = true;
				return true;
			}
		}

		boolean doKeyUp(int keyCode, KeyEvent msg) {
			synchronized (mSurfaceHolder) {
				mStatusText.setText(keyCode + " up");
				if(keyCode == KeyEvent.KEYCODE_DPAD_UP) mMoveUp = false;
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) mMoveLeft = false;
				if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) mMoveRight = false; 				
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) mMoveDown = false;
				if(keyCode == KeyEvent.KEYCODE_SPACE) mFiring = false;
				return true;
			}
		}

		public void setSurfaceSize(int width, int height) {
		}
	}

	private BattleThread thread;
	
	public BattleshipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		thread = new BattleThread(holder, context, new Handler(){});
		setFocusable(true);
		setClickable(true);
	}

	public void setTextView(TextView t) {
		t.setText("TextView applied to thread");
		thread.setTextView(t);
	}
	
	public BattleThread getThread() {
		return thread;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		return thread.doKeyDown(keyCode, msg);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg) {
		return thread.doKeyUp(keyCode, msg);
	}
	
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
		}
		
	}
	
	public class Bullet {
		public int x;
		public int y;
		public int damage = 1;
		public int owner;
		public int rotation;
		
		public Bullet () {
		}
		public Bullet (int xpos, int ypos, int rotation) {
			x = xpos;
			y = ypos; 
			this.rotation = rotation;
		}
		public Bullet (int xpos, int ypos, int rotation, int damage, int owner) {
			x = xpos;
			y = ypos;
			this.rotation = rotation;
			this.damage = damage;
			this.owner = owner;
		}
	}
}
