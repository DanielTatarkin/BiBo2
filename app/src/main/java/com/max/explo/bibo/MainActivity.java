package com.max.explo.bibo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.max.explo.bibo.model.ContactCard;

import java.lang.reflect.Array;


public class MainActivity extends AppCompatActivity {

    public static final String CARD_KEY = "CARD_KEY";
    public static final String ACTION_POPULATE = "ACTION_POPULATE";

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
    ContactCard[] cardList = new ContactCard[5];


    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private boolean emailRead = false;
    private boolean phoneRead = false;
    int cardIndex = 0;


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
                    Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                    if (emailRead) {
                        debugToast("Email", email);
                        intent.putExtra("EMAIL",email);
                    } if (phoneRead) {
                        debugToast("Phone", phone);
                        intent.putExtra("PHONE",phone);
                    }

                    if (!emailRead && !phoneRead){
//                        debugToast("ERROR", "nothing found");
                        intent.putExtra("NAME",getName(text));
                    } else {
//                        debugToast("ERROR", "here");
                    }
//                    startActivityForResult(intent,requestCode);
                    trySaving(getName(text), email, phone, intent);
                    phoneRead = false;
                    emailRead = false;

                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
            }
        } else if (requestCode == 6001) {
            Parcelable parcelable = data.getParcelableExtra("CONTACT_CARD");
            cardList[cardIndex] = (ContactCard) parcelable;
            switch (cardIndex) {
                case 0:
                    card1.setText(cardList[cardIndex].getName());
                    break;
                case 1:
                    card2.setText(cardList[cardIndex].getName());
                    break;
                case 2:
                    card3.setText(cardList[cardIndex].getName());
                    break;
                case 3:
                    card4.setText(cardList[cardIndex].getName());
                    break;
                case 4:
                    card5.setText(cardList[cardIndex].getName());
                    break;
            }
            cardIndex++;
            if (cardIndex == 5) cardIndex = 0;
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void debugToast(String action, String text) {
        Toast.makeText(this, action +" is " + text, Toast.LENGTH_LONG).show();
    }

    private void trySaving(String name, String email, String phone, Intent intent){
        startActivityForResult(intent,6001);
    }

    public View.OnClickListener cardHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);

            switch (v.getId()) {
                case R.id.card1:
                    if (card1.getText() != ""){
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER",1);
                        intent.putExtra("CARD1_CARD",cardList[0]);
                        startActivity(intent);
                    }
                    break;
                case R.id.card2:
                    if (card2.getText() != ""){
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER",2);
                        intent.putExtra("CARD2_CARD",cardList[1]);
                        startActivity(intent);
                    }
                    break;
                case R.id.card3:
                    if (card3.getText() != ""){
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER",3);
                        intent.putExtra("CARD3_CARD",cardList[2]);
                        startActivity(intent);
                    }
                    break;
                case R.id.card4:
                    if (card4.getText() != ""){
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER",4);
                        intent.putExtra("CARD4_CARD",cardList[3]);
                        startActivity(intent);
                    }
                    break;
                case R.id.card5:
                    if (card5.getText() != ""){
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER",5);
                        intent.putExtra("CARD5_CARD",cardList[4]);
                        startActivity(intent);
                    }
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
        return "";
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

    private String getName(String text) {
        String[] ctext = text.split("\n");
        return ctext[0];
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
