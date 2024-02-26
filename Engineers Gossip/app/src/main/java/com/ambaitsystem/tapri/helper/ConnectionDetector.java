package com.ambaitsystem.tapri.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
public class ConnectionDetector 
{
	//Context object
    private Context _context;
    
    //constructor called
    public ConnectionDetector(Context context)
    {
        this._context = context;
    }
    
    //To check the internet connection
    public boolean isConnectingToInternet()
    {
    	//ConnectivityManager refers to CONNECTIVITY_SERVICE
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
          if (connectivity != null) 
          {
              //get all network information
        	  NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                	  
                	  //compare networkinfo state is connected
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
              
          }
          return false;
     }
}
