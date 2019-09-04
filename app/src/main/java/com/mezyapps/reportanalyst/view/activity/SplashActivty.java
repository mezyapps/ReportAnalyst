package com.mezyapps.reportanalyst.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.mezyapps.reportanalyst.network_class.LicenseSession;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class SplashActivty extends AppCompatActivity {

    private SessionManager session;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static int SPLASH_TIME_OUT = 100;
    private String TAG = "tag";
    private LicenseSession licenseSession;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);

        find_View_Ids();
        events();

    }

    private void find_View_Ids() {
        licenseSession=new LicenseSession(getApplicationContext());
        session = new SessionManager(getApplicationContext());
    }

    private void events() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            handlerCall();
        }
    }

    private void checkPermissions() {
        RxPermissions.getInstance(SplashActivty.this)
                .request(
                        android.Manifest.permission.CALL_PHONE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        initialize(aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void initialize(Boolean isAppInitialized) {
        if (isAppInitialized) {
            handlerCall();

        } else {
            /* If one Of above permission not grant show alert (force to grant permission)*/
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivty.this);
            builder.setTitle("Alert");
            builder.setMessage("All permissions necessary");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkPermissions();
                }
            });

            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void handlerCall() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (licenseSession.isLicenseedIn() && session.isLoggedIn()) {
                    Intent intent = new Intent(SplashActivty.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (licenseSession.isLicenseedIn()){
                        Intent intent = new Intent(SplashActivty.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(SplashActivty.this, LincenseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 3000);
    }

}