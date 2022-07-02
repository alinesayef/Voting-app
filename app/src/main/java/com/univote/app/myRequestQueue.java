//this class is used for loading image from server in app
package com.univote.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class myRequestQueue {
    private static myRequestQueue instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    //this method is used to create image loader reference
    private myRequestQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader . ImageCache () {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    //this is the synchronized method for loading image so app not hang while image loading
    public static synchronized myRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new myRequestQueue(context);
        }
        return instance;
    }

    // this is the request constructor
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    //this is get image loader method
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}

//VolleySingleton.java [Accessed 8th September 2020] Available at: https://gist.github.com/ow-ro/aded2af78492f41925eb6f490890640f