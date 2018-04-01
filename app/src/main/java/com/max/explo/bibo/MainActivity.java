package com.max.explo.bibo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;


public class MainActivity extends AppCompatActivity {

    private boolean autoFocus = true;
    private boolean useFlash = false;
    private Button cameraButton;
    private Button card1;
    private Button card2;
    private Button card3;
    private Button card4;
    private Button card5;
    private String email = "";
    private String phone = "";


    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private static final String CARD_KEY = "CARD_KEY";
    private boolean emailRead = false;
    private boolean phoneRead = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraB);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);

                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);

        card1.setOnClickListener(cardHandler);
        card2.setOnClickListener(cardHandler);
        card3.setOnClickListener(cardHandler);
        card4.setOnClickListener(cardHandler);
        card5.setOnClickListener(cardHandler);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Log.d(TAG, "Text read: " + text);
                    email = getEmail(text);
                    phone = getPhone(text);
                    if (emailRead) {
                        debugToast("Email", email);
                    } if (phoneRead) {
                        debugToast("Phone", phone);
                    } if (!emailRead && !phoneRead){
                        debugToast("ERROR", "nothing found");
                    } else {
                        phoneRead = false;
                        emailRead = false;
                    }
                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void debugToast(String action, String text) {
        Toast.makeText(this, action +" is " + text, Toast.LENGTH_LONG).show();
    }

    public View.OnClickListener cardHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);

            switch (v.getId()) {
                case R.id.card1:
                    break;
                case R.id.card2:
                    break;
                case R.id.card3:
                    break;
                case R.id.card4:
                    break;
                case R.id.card5:
                    break;
            }

        }
    };

    private String getEmail(String text) {
        String[] ctext = text.split(" |\n");
        String cemail = "";
        for (String s : ctext) {
            for(int i = 0; i < s.length(); i++){
                if(s.charAt(i) == '@'){
                    cemail = s;
                    emailRead = true;
                    return cemail;
                }
            }
        }
        return cemail;
    }

    private void phoneSaved(String action, String text) {
        String tphone = getPhone(text);
        if (tphone != "") { Toast.makeText(this, action +" is " + tphone, Toast.LENGTH_SHORT).show(); }
        phoneRead = false;
    }

    private String getPhone(String text) {
        String[] ctext = text.split("\n");
        for (String s : ctext) {
            String cnumb = "";
            for (int i = 0; i < s.length(); i++){
                if(!(s.charAt(i) < '0' || s.charAt(i) > '9')){
                    cnumb += s.charAt(i);
                }
            }
            if (cnumb.length() == 10 || cnumb.length() == 11) {
                phoneRead = true;
                return cnumb;
            }
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
