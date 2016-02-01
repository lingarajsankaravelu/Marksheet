package com.hourglass.lingaraj.marksheet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lingaraj on 1/12/16.
 */
public class InternetConnectionDetector
{
    /**
     * Calling ths isConnectingToInternet from any class or Adapter will give the internet connectivity status of the Device(i.e connected or disconnected)
     */

    private Context tcontext;

    public InternetConnectionDetector(Context context){
        this.tcontext = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) tcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

}
