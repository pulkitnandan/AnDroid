package com.eudessess.webbrowser.gesture;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eudessess.webbrowser.R;

import java.io.File;
import java.util.ArrayList;

public class GestureActivity extends Activity {
    private GestureLibrary gLib;
    private static final String TAG = "com.Fast";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestures);
        String path = Environment.getExternalStorageDirectory().toString()+"/gestures";
        File f = new File(path);
        gLib = GestureLibraries.fromFile(f);
        if (!gLib.load()) {
            Log.w(TAG, "could not load gesture library");
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(handleGestureListener);
    }

    /**
     * our gesture listener
     */
    private GestureOverlayView.OnGesturePerformedListener handleGestureListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureView,
                                       Gesture gesture) {

            ArrayList<Prediction> predictions = gLib.recognize(gesture);

            // one prediction needed
            if (predictions.size() > 0) {
                Prediction prediction = predictions.get(0);
                // checking prediction
                if (prediction.score > 1.0) {
                    // and action
                    Toast.makeText(GestureActivity.this, prediction.name,
                            Toast.LENGTH_SHORT).show();
                    Intent a = new Intent();
                    Bundle ans = new Bundle();
                    ans.putString("gurl", prediction.name);
                    a.putExtras(ans);
                    setResult(RESULT_OK, a);
                    finish();
                }
            }

        }
    };
}