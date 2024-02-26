package com.ambaitsystem.tapri.activity;

import android.content.Context;
import android.media.MediaPlayer;

public class sound
{
	 	Thread t;
	    int sr = 44100;
	    boolean isRunning = true;
	    double sliderval;
	    boolean FlagReachedToFifty =false;    
	Context context;
    MediaPlayer mp;
    public sound(Context context){
        this.context = context;
    }

	/*public void win(final double PlayAtValue)
	{
		/mp.stop();
		mp = MediaPlayer.create(context, R.raw.);
		mp.start();

		mp.setOnCompletionListener(new OnCompletionListener() {

	        @Override
	        public void onCompletion(MediaPlayer mp) {
	            mp.release();

	        }



	    });
	}
	
	

	
	public void fail(final double PlayAtValue)
	{
		//mp.stop();
		mp = MediaPlayer.create(context, R.raw.fail);
		mp.start();
		
		mp.setOnCompletionListener(new OnCompletionListener() {

	        @Override
	        public void onCompletion(MediaPlayer mp) {
	            mp.release();
	            
	        }

	        
	        
	    });
	}*/
	

}

