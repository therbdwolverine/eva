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
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends Activity {

    public static Map<String, AssetFileDescriptor> titleFds = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            titleFds.put("Timelapse", getAssets().openFd("timelapse.mp4"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_home);

    }
}
