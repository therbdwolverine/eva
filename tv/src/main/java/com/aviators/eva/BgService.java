package com.aviators.eva;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.ImageReader;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviators.eva.libscreenshotter.ScreenshotCallback;
import com.aviators.eva.libscreenshotter.Screenshotter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class BgService  extends Service{

    private static int value =0;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service Started");


       /* IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VIBRATE_SETTING_CHANGED");
        registerReceiver(receiver, filter);*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



    private void takeScreenshot(Intent intent) {
        Screenshotter.getInstance().setSize(360,360).takeScreenshot(getBaseContext(),
                HomeActivity.RESULT_FIRST_USER, intent, new ScreenshotCallback() {
            @Override
            public void onScreenshot(Bitmap bitmap) {
                saveBitmap(bitmap);
                Toast.makeText(BgService.this, "Screenshot Captured!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveBitmap(Bitmap bitmap){
        FileOutputStream out = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().toString(), "EvaScreenshot.png");
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //TODO Put in 5 Offers inside toast
    public Toast generateCustomToast() {
        LayoutInflater layoutInflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.customtoast, null);


       /* ImageView toastimg= (ImageView)
                layout.findViewById(R.id.toastImg);*/
       /* toastimg.setImageResource(R.drawable.anyimage);
        TextView toastmes= (TextView)
                layout.findViewById(R.id.toastMes);*/
        //toastmes.setText("Here your ur toast message");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return toast;
    }

    public void showToastWithTimer(final Toast mToastToShow) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 10000;

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }


}
