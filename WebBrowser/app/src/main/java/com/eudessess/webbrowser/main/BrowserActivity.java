package com.eudessess.webbrowser.main;


import com.eudessess.webbrowser.gesture.GestureActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eudessess.webbrowser.R;
import com.eudessess.webbrowser.database.InbuiltDatabase;

import java.io.File;
import java.io.FileOutputStream;

public class BrowserActivity extends Activity implements View.OnClickListener,
        View.OnLongClickListener {
    EditText search;
    String gTitle;
    Button bsearch;
    AlertDialog ad;
    File path;
    AlertDialog.Builder builder;
    ImageButton newTab;
    String title, link, homepage, image_title, gurl;
    boolean js, popUP, loadIM, history;
    HorizontalScrollView scroll;
    ImageButton pre, next, ibCancel, ibBookmark, ibBackward, ibForward,
            ibRefresh, ibHomepage;
    LinearLayout menu, webSpace;
    LinearLayout[] newTabs = new LinearLayout[10];
    TextView[] newText = new TextView[10];
    TextView tab;
    LinearLayout.LayoutParams params;
    int i = 1, width, height, pheight, pwidth;
    WebView wv[] = new WebView[10], wvpres;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    InbuiltDatabase hist;
    Bitmap b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsettings();
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_browser);
        search = (EditText) findViewById(R.id.etSearch);
        scroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        newTab = (ImageButton) findViewById(R.id.ibNewTab);
        bsearch = (Button) findViewById(R.id.bSearch);
        ibBookmark = (ImageButton) findViewById(R.id.ibBookmark);
        ibBackward = (ImageButton) findViewById(R.id.ibBackward);
        ibForward = (ImageButton) findViewById(R.id.ibForward);
        ibRefresh = (ImageButton) findViewById(R.id.ibRefresh);
        ibHomepage = (ImageButton) findViewById(R.id.ibHomepage);
        tab = (TextView) findViewById(R.id.textView1);
        wv[0] = (WebView) findViewById(R.id.wvx);
        wvpres = wv[0];
        ibBackward.setOnClickListener(this);
        ibForward.setOnClickListener(this);
        ibHomepage.setOnClickListener(this);
        ibRefresh.setOnClickListener(this);
        bsearch.setOnClickListener(this);

        newTab.setOnClickListener(this);
        ibBookmark.setOnClickListener(this);
        wvpres.setOnLongClickListener(this);

        search.setOnClickListener(this);

        webSettings();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener
                .setOnShakeListener(new ShakeEventListener.OnShakeListener() {

                    public void onShake() {
                        wvpres.reload();
                        Toast.makeText(BrowserActivity.this,
                                "Reloading...", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void prefsettings() {
        SharedPreferences gotPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        homepage = gotPrefs.getString("Homepage", "http://www.google.com");

        js = gotPrefs.getBoolean("javaSC", true);
        popUP = gotPrefs.getBoolean("popUP", true);
        loadIM = gotPrefs.getBoolean("loadIM", true);
        history = gotPrefs.getBoolean("history", true);
    }

    private void webSettings() {
        if (js == false)
            wvpres.getSettings().setJavaScriptEnabled(false);
        if (popUP == false)
            wvpres.getSettings()
                    .setJavaScriptCanOpenWindowsAutomatically(false);
        if (loadIM == false)
            wvpres.getSettings().setLoadsImagesAutomatically(false);

        wvpres.getSettings().setLoadWithOverviewMode(true);
        wvpres.getSettings().setUseWideViewPort(true);

        wvpres.setWebViewClient(new Web() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                gTitle = view.getTitle();
                gurl = url;
                search.setText(view.getUrl());
                wvpres.requestFocus();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                width = metrics.widthPixels;
                height = metrics.heightPixels;

                Picture picture = view.capturePicture();
                pheight = picture.getHeight();
                pwidth = picture.getWidth();
                path = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                path.mkdirs();
                b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                Canvas c = new Canvas(b);

                picture.draw(c);
                image_title = path + "/" + view.getTitle() + "_"
                        + System.currentTimeMillis() + ".jpg";

            }

        });

        final Activity activity = this;

        wvpres.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                tab.setText(view.getTitle());
                activity.setTitle("Loading... " + progress + "%");
                activity.setProgress(progress * 100);
                if (progress == 100) {
                    activity.setTitle(view.getTitle());
                    saveHistory();
                }

            }
        });

        wvpres.loadUrl(homepage);
    }

    protected void saveHistory() {
        if (history == true) {
            try {
                hist = new InbuiltDatabase(this, "history");
                hist.insertRow(gTitle, gurl);
                hist.close();
            } catch (Exception ex) {
                Log.e("problem", ex.getMessage());
            }
        }

    }

    EditText etUrl;
    EditText etTitle;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.etSearch:
                search.requestFocus();
                break;

            case R.id.ibNewTab:
                createNewTab();
                break;

            case R.id.bSearch:
                String url = search.getText().toString();
                wvpres.loadUrl(url);

                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(search.getWindowToken(), 0);
                break;

            case R.id.ibBookmark:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);

                final LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                alert.setTitle("Save the bookmark...");
                final TextView t1 = new TextView(this);
                etTitle = new EditText(this);
                final TextView t2 = new TextView(this);
                etUrl = new EditText(this);
                etTitle.setText(gTitle);
                etUrl.setText(search.getText());
                t1.setText("Title");
                t2.setText("URL");
                final LinearLayout layouthori = new LinearLayout(this);
                layouthori.setOrientation(LinearLayout.HORIZONTAL);
                final Button ok = new Button(this);
                final Button cancel = new Button(this);
                ok.setText("Save");
                cancel.setText("Cancel");
                ok.setOnClickListener(this);
                cancel.setOnClickListener(this);

                layout.addView(t1);
                layout.addView(etTitle);
                layout.addView(t2);
                layout.addView(etUrl);
                layouthori.addView(ok);
                layouthori.addView(cancel);
                layout.addView(layouthori);
                alert.setView(layout);
                ok.setId(R.id.bSend);
                cancel.setId(R.id.cancel1);
                ad = alert.create();
                ad.show();
                break;
            case R.id.ibBackward:
                wvpres.goBack();
                String current = wvpres.getUrl();
                search.setText(current);
                break;
            case R.id.ibForward:
                wvpres.goForward();
                String currfor = wvpres.getUrl();
                search.setText(currfor);
                break;
            case R.id.ibRefresh:
                wvpres.reload();
                break;
            case R.id.ibHomepage:
                wvpres.loadUrl(homepage);

                break;
            case R.id.wvx:
                wvpres.requestFocus();
                break;
            case R.string.ok:
                InbuiltDatabase bdb = new InbuiltDatabase(this, "BOOKMARK");
                try {
                    bdb.insertRow(etTitle.getText().toString(), etUrl.getText()
                            .toString());
                    Toast.makeText(this, "Bookmark Successfully Added...",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    String problem = "Bookmark Adding failed" + "\n"
                            + ex.getMessage();
                    Toast.makeText(this, problem, Toast.LENGTH_SHORT);
                }
                ad.dismiss();
                break;
            case R.string.save:
                FileOutputStream fos = null;

                try {

                    fos = new FileOutputStream(image_title);

                    if (fos != null) {
                        b.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                        fos.close();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ad.dismiss();
                break;
            case R.string.cancel:
                ad.dismiss();
                break;

        }

    }

    private void createNewTab() {
        menu = (LinearLayout) findViewById(R.id.home_menu);
        newTabs[i] = new LinearLayout(this);
        newTabs[i].setBackgroundResource(R.drawable.tab);
        params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        newTabs[i].setLayoutParams(params);
        newTabs[i].setId(R.string.tab + i);
        newText[i] = new TextView(this);
        LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT);
        newText[i].setLayoutParams(tvlp);
        newText[i].setId(R.string.text + i);
        newText[i].setText("new" + i);
        newText[i].setWidth(80);
        newText[i].setSingleLine(true);
        newText[i].setTextColor(Color.BLACK);
        newText[i].setGravity(Gravity.CENTER);
        // /////////////////////////////////////////////////////////////////////
        newText[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newText[i - 1].setText("hello");
            }
        });

        ImageButton im = new ImageButton(this);
        LinearLayout.LayoutParams imlp = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT);
        im.setLayoutParams(imlp);
        im.setId(R.string.imagebutton + i);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((View) v.getParent()).setVisibility(View.GONE);
            }
        });

        Bitmap ent = BitmapFactory.decodeResource(getResources(),
                R.drawable.cancel);
        im.setImageBitmap(ent);
        im.setBackgroundColor(Color.TRANSPARENT);
        newTabs[i].addView(newText[i]);
        newTabs[i].addView(im);

        webSpace = (LinearLayout) findViewById(R.id.webSpace);
        wv[i] = new WebView(this);
        LinearLayout.LayoutParams wvlp = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        wv[i].setLayoutParams(wvlp);
        wv[i].setId(R.string.webview + i);
        webSpace.removeView(wvpres);
        webSpace.addView(wv[i]);
        wvpres = wv[i];
        webSettings();
        menu.addView(newTabs[i]);
        i++;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.aboutus:
                Intent i = new Intent("com.eudessess.webbrowser.menu.aboutus");
                startActivity(i);
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.bookmark:
                Intent in = new Intent("com.eudessess.webbrowser.menu.bookmark");
                startActivity(in);
                break;
            case R.id.setting:
                Intent s = new Intent("com.eudessess.webbrowser.menu.setting");
                startActivity(s);
                break;
            case R.id.history:
                Intent hist = new Intent("com.eudessess.webbrowser.menu.history");
                startActivity(hist);
                break;
            case R.id.more:
                Intent g = new Intent(BrowserActivity.this,
                        GestureActivity.class);
                startActivityForResult(g, 0);
                break;
            case R.id.add_ges:
                Intent ges = new Intent("com.Fast.GESTUREBUILDERACTIVITY");
                startActivity(ges);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        Bundle extras = getIntent().getExtras();
        if (extras != null)

        {
            String url = extras.getString("urlSelected");
            wvpres.loadUrl(url);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            String v = b.getString("gurl");
            wvpres.loadUrl(v);

            // tv.setText(v);
        }
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onStop();
    }

    public void loader(String name) {
        // TODO Auto-generated method stub
        wvpres.loadUrl("http://www.iiita.ac.in");

    }

    String mUrl;

    @Override
    public boolean onLongClick(View arg0) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle("Save the webshot...");
        final LinearLayout layout1 = new LinearLayout(this);
        final TextView t1 = new TextView(this);
        final ImageView ivs = new ImageView(this);
        int maxX = (int) ((pwidth / 2) - (width / 2));
        int maxY = (int) ((pheight / 2) - (height / 2));
        // set scroll limits
        final int maxLeft = (maxX * -1);
        final int maxRight = maxX;
        final int maxTop = (maxY * -1);
        final int maxBottom = maxY;

        // set touchlistener
        ivs.setOnTouchListener(new View.OnTouchListener() {
            float downX
                    ,
                    downY;
            int totalX
                    ,
                    totalY;
            int scrollByX
                    ,
                    scrollByY;

            public boolean onTouch(View view, MotionEvent event) {
                float currentX, currentY;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        currentX = event.getX();
                        currentY = event.getY();
                        scrollByX = (int) (downX - currentX);
                        scrollByY = (int) (downY - currentY);

                        // scrolling to left side of image (pic moving to the right)
                        if (currentX > downX) {
                            if (totalX == maxLeft) {
                                scrollByX = 0;
                            }
                            if (totalX > maxLeft) {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX < maxLeft) {
                                scrollByX = maxLeft - (totalX - scrollByX);
                                totalX = maxLeft;
                            }
                        }

                        // scrolling to right side of image (pic moving to the left)
                        if (currentX < downX) {
                            if (totalX == maxRight) {
                                scrollByX = 0;
                            }
                            if (totalX < maxRight) {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX > maxRight) {
                                scrollByX = maxRight - (totalX - scrollByX);
                                totalX = maxRight;
                            }
                        }

                        // scrolling to top of image (pic moving to the bottom)
                        if (currentY > downY) {
                            if (totalY == maxTop) {
                                scrollByY = 0;
                            }
                            if (totalY > maxTop) {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY < maxTop) {
                                scrollByY = maxTop - (totalY - scrollByY);
                                totalY = maxTop;
                            }
                        }

                        // scrolling to bottom of image (pic moving to the top)
                        if (currentY < downY) {
                            if (totalY == maxBottom) {
                                scrollByY = 0;
                            }
                            if (totalY < maxBottom) {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY > maxBottom) {
                                scrollByY = maxBottom - (totalY - scrollByY);
                                totalY = maxBottom;
                            }
                        }

                        ivs.scrollBy(scrollByX, scrollByY);
                        downX = currentX;
                        downY = currentY;
                        break;

                }

                return true;
            }
        });
        t1.setText(image_title);
        ivs.setImageBitmap(b);
        layout1.addView(ivs);

        final LinearLayout layouthori = new LinearLayout(this);
        layouthori.setOrientation(LinearLayout.HORIZONTAL);
        final Button ok = new Button(this);
        final Button cancel = new Button(this);
        ok.setText("Save");
        cancel.setText("Cancel");
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        layout.addView(t1);
        layouthori.addView(ok);
        layouthori.addView(cancel);
        layout.addView(layouthori);
        layout.addView(layout1);
        alert.setView(layout);
        //ok.setId(R.string.save);
        //cancel.setId(R.string.cancel);
        ad = alert.create();
        ad.show();
        return true;
    }
}

