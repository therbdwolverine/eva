/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.aviators.eva;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aviators.eva.libscreenshotter.Screenshotter;
import com.aviators.eva.src.videoapi.Offers;
import com.aviators.eva.src.videoapi.OffersResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.StringUtils;
import com.google.gson.Gson;
import com.google.protobuf.Field;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;

/**
 * PlaybackOverlayActivity for video playback that loads PlaybackOverlayFragment
 */
public class PlaybackOverlayActivity extends Activity implements
        PlaybackOverlayFragment.OnPlayPauseClickedListener {
    private static final String TAG = "PlaybackOverlayActivity";

    private VideoView mVideoView;
    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private MediaSession mSession;

    private boolean remoteCentreButtonPressed = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_controls);
        loadViews();
        setupCallbacks();
        mSession = new MediaSession(this, "LeanbackSampleApp");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mSession.setActive(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.suspend();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        PlaybackOverlayFragment playbackOverlayFragment = (PlaybackOverlayFragment) getFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    playbackOverlayFragment.togglePlayback(false);
                } else {
                    playbackOverlayFragment.togglePlayback(true);
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Implementation of OnPlayPauseClickedListener
     */
    public void onFragmentPlayPause(Movie movie, int position, Boolean playPause) {
        mVideoView.setVideoPath(movie.getVideoUrl());

        if (position == 0 || mPlaybackState == LeanbackPlaybackState.IDLE) {
            setupCallbacks();
            mPlaybackState = LeanbackPlaybackState.IDLE;
        }

        if (playPause && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            if (position > 0) {
                mVideoView.seekTo(position);
                mVideoView.start();
            }
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            mVideoView.pause();
        }
        updatePlaybackState(position);
        updateMetadata(movie);
    }

    private void updatePlaybackState(int position) {
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LeanbackPlaybackState.PAUSED) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, position, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;

        if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private void updateMetadata(final Movie movie) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        String title = movie.getTitle().replace("_", " -");

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,
                movie.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                movie.getCardImageUrl());

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, movie.getStudio());

        Glide.with(this)
                .load(Uri.parse(movie.getCardImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);
    }

    private void setupCallbacks() {

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg = "";
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_inaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    mVideoView.start();
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaybackState = LeanbackPlaybackState.IDLE;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                // Try to play behind launcher, but if it fails, stop playback.
                stopPlayback();
            }
        } else {
            requestVisibleBehind(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSession.release();
    }


    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            if (remoteCentreButtonPressed && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                remoteCentreButtonPressed = false;
                OffersResponse offersResponse = takeScreenshotAndInvokeEva();
                if (offersResponse != null) {
                    Toast.makeText(getApplicationContext(), "Looking for Expedia Offers in the region", Toast.LENGTH_LONG).show();
                }
                Toast toast = generateCustomToast(offersResponse);
                showToastWithTimer(toast);
            }
        return false;
    }


    //TODO Put in 5 Offers inside toast
    public Toast generateCustomToast(OffersResponse offersResponse) {
        LayoutInflater layoutInflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.customtoast, null);

        Offers offer = offersResponse.getOffers()[0];
        double totalPrice = Double.parseDouble(offer.getTotalPrice());
        double savings = Double.parseDouble(offer.getPercentSavings());

        TextView marketingText = (TextView) layout.findViewById(R.id.marketingtext);
        marketingText.setText("SAVE BIG!! Vacation Packages to " + offersResponse.getDestination());

        TextView hotelname = (TextView) layout.findViewById(R.id.hotel_description);
        hotelname.setText(offer.getHotelName());

        RatingBar ratingbar = (RatingBar) layout.findViewById(R.id.ratingBar1);
        ratingbar.setRating(Float.parseFloat(offer.getHotelStarRating()));

        TextView percentsavings = (TextView) layout.findViewById(R.id.percent);
        percentsavings.setText("Save " + (int) savings + "%");

        TextView crossoutpricings = (TextView) layout.findViewById(R.id.crossoutprice);
        crossoutpricings.setText(getCrossOutPrice(savings, totalPrice));
        crossoutpricings.setPaintFlags(crossoutpricings.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        TextView totalprice = (TextView) layout.findViewById(R.id.totalprice);
        totalprice.setText(offer.getCurrency() + " " + offer.getTotalPrice());

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return toast;
    }

    public void showToastWithTimer(final Toast mToastToShow) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 20000;

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 2000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
                remoteCentreButtonPressed = true;
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv) {
        try {

            URL myFileUrl = new URL(fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private OffersResponse takeScreenshotAndInvokeEva() {

        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Toast.makeText(getApplicationContext(), "Looking for Expedia Offers in the region", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Looking for Expedia Offers in the region", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Looking for Expedia Offers in the region", Toast.LENGTH_SHORT).show();
            mediaMetadataRetriever.setDataSource(getApplicationContext(), getmUri(mVideoView));

            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);


            String stringresponse = "{\n" +
                    "    \"Offers\": [\n" +
                    "        {\n" +
                    "            \"Currency\": \"USD\",\n" +
                    "            \"PerPassengerPackagePrice\": \"3378.0\",\n" +
                    "            \"TotalPrice\": \"6757.62\",\n" +
                    "            \"PerPassengerSavings\": \"1645.0\",\n" +
                    "            \"TotalSavings\": \"3291.17\",\n" +
                    "            \"PercentSavings\": \"33.0\",\n" +
                    "            \"TravelStartDate\": \"08/02/2017\",\n" +
                    "            \"TravelEndDate\": \"08/07/2017\",\n" +
                    "            \"HotelName\": \"Sheraton Grand Hotel, Dubai\",\n" +
                    "            \"HotelStarRating\": \"5.0\",\n" +
                    "            \"HotelImageUrl\": \"https://images.trvl-media.com/hotels/9000000/8800000/8797400/8797323/8797323_227_t.jpg\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Destination\": \"Dubai\"\n" +
                    "}";
             ;

           // OffersResponse response = new Gson().fromJson(new EvaTask().execute(imageString).get(), OffersResponse.class);
            OffersResponse response = new Gson().fromJson(stringresponse, OffersResponse.class);
            if(response == null || response.getDestination() == null || response.getOffers() == null) {
                Utils.showToast(getApplicationContext(), "Sorry!! No Offers found for the Region");
            }
            return response;


        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return null;
    }

    private String getCrossOutPrice(double savings, double totalPrice1) {
        double c = (totalPrice1 * 100.0) / (100.0 - savings);
        Double d = Double.valueOf(c);
        return BigDecimal.valueOf(d)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }

    private Uri getmUri(VideoView videoView) {

        Uri mUri = null;
        try

        {
            java.lang.reflect.Field mUriField = VideoView.class.getDeclaredField("mUri");
            mUriField.setAccessible(true);
            mUri = (Uri) mUriField.get(videoView);
        } catch (
                Exception e) {
        }
        return mUri;

    }


    private class MediaSessionCallback extends MediaSession.Callback {
    }
}
