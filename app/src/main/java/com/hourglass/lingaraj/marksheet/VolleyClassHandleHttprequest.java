package com.hourglass.lingaraj.marksheet;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lingaraj on 8/20/15.
 */
public class VolleyClassHandleHttprequest extends Application {

    private static VolleyClassHandleHttprequest sInstance;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);

        sInstance = this;
    }

    public synchronized static VolleyClassHandleHttprequest getInstance() {

        return sInstance;
    }

    public RequestQueue getRequestQueue() {

        return mRequestQueue;
    }
}

