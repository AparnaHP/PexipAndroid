package com.pexip.android.wrapper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pexip.android.wrapper.util.RunTimeHandler;

import java.util.Objects;


public class PexipCustomizedView extends AppCompatActivity {

    private WebView webView;
    private Activity activity;
    private ImageButton videoButton,audioButton,toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pexip_customized);

        webView = findViewById(R.id.webView);
        activity=this;


        findViewById(R.id.endCallButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl("javascript:disconnectFromCurrentMeeting()");
                activity.finish();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl("javascript:disconnectFromCurrentMeeting()");
                activity.finish();
            }
        });

        videoButton = findViewById(R.id.muteVideoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag().equals("1")) {
                    webView.loadUrl("javascript:muteVideo(true)");
                    v.setTag("0");
                    videoButton.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.video_off));

                }
                else {
                    webView.loadUrl("javascript:muteVideo(false)");
                    v.setTag("1");
                    videoButton.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.video));
                }
            }
        });

        audioButton = findViewById(R.id.muteAudioButton);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag().equals("1")) {
                    webView.loadUrl("javascript:muteAudio(true)");
                    v.setTag("0");
                    audioButton.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.microphone_off));
                }
                else {
                    webView.loadUrl("javascript:muteAudio(false)");
                    v.setTag("1");
                    audioButton.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.microphone));
                }
            }
        });


        toggleButton = findViewById(R.id.toggle);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    webView.loadUrl("javascript:toggleSelfview()");

            }
        });



        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            initLandscapeView();
        }
        else
        {
            initPortraitView();
        }


        enablePanel(false);
        checkPermission();

    }

    private void checkPermission()
    {
        RunTimeHandler runTimeHandler = new RunTimeHandler(this, true);
        if (runTimeHandler.validatePermission(195))
        {
            enablePanel(true);
        }
    }

    private void enablePanel(boolean isEnable)
    {
        videoButton.setEnabled(isEnable);
        audioButton.setEnabled(isEnable);
        toggleButton.setEnabled(isEnable);

        if(isEnable)
        setUp();

    }



    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void setUp()
    {
        WebSettings settings = webView.getSettings();
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        settings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }

            public void onProgressChanged(WebView view, int progress)
            {
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:init('"+getIntent().getStringExtra("node")+"','"+getIntent().getStringExtra("conference")+"','"+getIntent().getStringExtra("domain")+"','"+getIntent().getIntExtra("bw",576)+"','"+getIntent().getStringExtra("userName")+"')");

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    webView.loadUrl("javascript:orientation(false)");
                }
                else
                {
                    webView.loadUrl("javascript:orientation(true)");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return true;
            }
        });


        webView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showHideControlPanel();
                return false;
            }
        });

        webView.loadUrl("file:///android_asset/index.html");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initLandscapeView();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            initPortraitView();
        }
    }

    private void showHideControlPanel()
    {
        final View view = findViewById(R.id.controlPanel);
        view.setVisibility(View.VISIBLE);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            view.postDelayed(new Runnable() {
                public void run() {
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    view.setVisibility(View.INVISIBLE);
                }
            }, 5000);
        }
    }

    private void initPortraitView()
    {
        findViewById(R.id.webViewLayout).getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.pexipWebViewHieght);

        LinearLayout controlPanel = findViewById(R.id.controlPanel);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) controlPanel.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.BELOW, R.id.webViewLayout);
        controlPanel.setLayoutParams(params);

        ImageViewCompat.setImageTintList(audioButton, ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        ImageViewCompat.setImageTintList(videoButton, ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        audioButton.setBackground(getDrawable(R.drawable.pexip_small_button));
        videoButton.setBackground(getDrawable(R.drawable.pexip_small_button));

        webView.loadUrl("javascript:orientation(true)");

        showHideControlPanel();

    }

    private void initLandscapeView()
    {
        findViewById(R.id.webViewLayout).getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout controlPanel = findViewById(R.id.controlPanel);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) controlPanel.getLayoutParams();
        params.removeRule(RelativeLayout.BELOW);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        controlPanel.setLayoutParams(params);

        ImageViewCompat.setImageTintList(audioButton, ColorStateList.valueOf(getColor(R.color.white)));
        ImageViewCompat.setImageTintList(videoButton, ColorStateList.valueOf(getColor(R.color.white)));
        audioButton.setBackground(getDrawable(R.drawable.pexip_small_land_button));
        videoButton.setBackground(getDrawable(R.drawable.pexip_small_land_button));

        webView.loadUrl("javascript:orientation(false)");

        showHideControlPanel();
    }


    @Override
    public void onDestroy()
    {
        webView.loadUrl("javascript:disconnectFromCurrentMeeting()");
        super.onDestroy();
    }

    class WebViewJavaScriptInterface{

        private final Context context;

        WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void closeActivity(){
            Toast.makeText(context, activity.getString(R.string.cannot_start_meeting), Toast.LENGTH_LONG).show();
            activity.finish();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        onRequestPermissionsResult(requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onRequestPermissionsResult(int requestCode) {
        if (requestCode == 195 && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //never ask again, allow
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                enablePanel(true);
            } else {
                new RunTimeHandler(this, true).permissionDenied();
            }
        }
    }
}
