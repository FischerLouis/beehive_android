package com.beehive.tools;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.beehive.application.BeehiveApplication;
 
public class VolleySingleton {
	
	//int memClass = ( ( ActivityManager )context.getSystemService( Context.ACTIVITY_SERVICE ) ).getMemoryClass();
	//int cacheSize = 1024 * 1024 * memClass / 8;
	
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
 
    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(BeehiveApplication.getAppContext());
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(20);
            
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
            
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url.substring(6), bitmap);
            }
            
        });
    }
 
    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }
 
    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }
 
    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }
 
}