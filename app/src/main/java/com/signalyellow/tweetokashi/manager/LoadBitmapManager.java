package com.signalyellow.tweetokashi.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class LoadBitmapManager {

    static final String TAG = "LoadBitmapManager";

    private static final int MEM_CACHE_SIZE = 3*1024*1024; // MB
    private static final int THREAD_MAX_NUM = 3;

    private static BlockingQueue<LoadBitmapItem> downloadQueue;
    private static Handler mHandler;
    private android.util.LruCache<String,Bitmap> mLruCache;

    public LoadBitmapManager(){
        mLruCache = new android.util.LruCache<String,Bitmap>(MEM_CACHE_SIZE){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
              return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        downloadQueue = new LinkedBlockingQueue<>();
        for(int i=0 ; i<THREAD_MAX_NUM ; i++) {
            new Thread(new DownloadWorker()).start();
        }

        mHandler = new LoadBitmapHandler();
    }

    private class LoadBitmapHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            LoadBitmapItem item = (LoadBitmapItem)msg.obj;

            if(item != null && item.getImgView().getTag().toString().equals(item.getUrl()) && item.getBitmap() != null) {

                item.getImgView().setImageBitmap(item.getBitmap());

                if (mLruCache.get(item.getUrl()) == null) {
                    mLruCache.put(item.getUrl(), item.getBitmap());
                }

            }
        }
    }

    public void downloadBitmap(ImageView imgView, String url) {
        Bitmap bitmap = mLruCache.get(url);

        if(bitmap != null){
            imgView.setImageBitmap(bitmap);
            return;
        }

        LoadBitmapItem item = new LoadBitmapItem();
        item.setImgView(imgView);
        item.setUrl(url);
        downloadQueue.offer(item);
    }

    private static class DownloadWorker implements Runnable {
        @Override
        public void run() {
            for(;;) {
                Bitmap bitmap=null;
                LoadBitmapItem item;

                try {
                    item = downloadQueue.take();
                } catch (Exception e){
                    Log.e(TAG, "", e);
                    continue;
                }

                try{
                    BufferedInputStream in = new BufferedInputStream(
                            (InputStream) (new URL(item.getUrl())).getContent());
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e){
                    Log.e(TAG, "", e);
                }
                item.setBitmap(bitmap);

                /*
                 * 取得した画像情報でメッセージを作って投げる
                 */
                Message msg = new Message();
                msg.obj = item;
                mHandler.sendMessage(msg);
            }
        }
    }
}