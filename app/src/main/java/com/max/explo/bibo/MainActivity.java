package com.max.explo.bibo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.max.explo.bibo.model.ContactCard;




public class MainActivity extends AppCompatActivity {


    private EditText mEditTexto;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    public static final String CARD_KEY = "CARD_KEY";
    public static final String ACTION_POPULATE = "ACTION_POPULATE";


    private boolean callOnly = false;
    private boolean mailOnly = false;
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


    public static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private boolean emailRead = false;
    private boolean phoneRead = false;
    int cardIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cameraButton = findViewById(R.id.cameraB);
        Button emailButton = findViewById(R.id.mailB);
        Button phoneButton = findViewById(R.id.phoneB);

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

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);
                mailOnly = true;
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);
                callOnly = true;
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Log.d(TAG, "Text read: " + text);
                    email = getEmail(text);
                    phone = getPhone(text);
                    if(mailOnly){
                        if(emailRead) {
                            prepMail();
                        }
                        mailOnly = false;
                    } else if (callOnly){
                        if(phoneRead){
                            directCall();
                        }
                        callOnly = false;
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                        if (emailRead) {
                            debugToast("Email", email);
                            intent.putExtra("EMAIL", email);
                        }
                        if (phoneRead) {
                            debugToast("Phone", phone);
                            intent.putExtra("PHONE", phone);
                        }

                        if (!emailRead && !phoneRead) {
//                        debugToast("ERROR", "nothing found");
                            intent.putExtra("NAME", getName(text));
                        } else {
//                        debugToast("ERROR", "here");
                        }
//                    startActivityForResult(intent,requestCode);
                        trySaving(getName(text), email, phone, intent);
                    }
                    phoneRead = false;
                    emailRead = false;

                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
            }
        } else if (requestCode == 6003 && resultCode == RESULT_OK) {
            Parcelable parcelable = data.getParcelableExtra("CONTACT_CARD");
            boolean temp = false;
            int tempNumber =0;
            if (data.getIntExtra("CARD_NUMBER",-1) > -1){
                tempNumber = cardIndex;
                cardIndex = data.getIntExtra("CARD_NUMBER",0);
                temp = true;
            }
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
            if (!temp) cardIndex++;
            if (temp) cardIndex = tempNumber;
            if (cardIndex == 5) cardIndex = 4;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void debugToast(String action, String text) {
        Toast.makeText(this, action + " is " + text, Toast.LENGTH_LONG).show();
    }

    public void trySaving(String name, String email, String phone, Intent intent) {
        startActivityForResult(intent, 6003);
    }

    public View.OnClickListener cardHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);

            switch (v.getId()) {
                case R.id.card1:
                    if (card1.getText() != "") {
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER", 0);
                        intent.putExtra("CARD1_CARD", cardList[0]);
                        startActivityForResult(intent, 6003);
                    }
                    break;
                case R.id.card2:
                    if (card2.getText() != "") {
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER", 1);
                        intent.putExtra("CARD2_CARD", cardList[1]);
                        startActivityForResult(intent, 6003);
                    }
                    break;
                case R.id.card3:
                    if (card3.getText() != "") {
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER", 2);
                        intent.putExtra("CARD3_CARD", cardList[2]);
                        startActivityForResult(intent, 6003);
                    }
                    break;
                case R.id.card4:
                    if (card4.getText() != "") {
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER", 3);
                        intent.putExtra("CARD4_CARD", cardList[3]);
                        startActivityForResult(intent, 6003);
                    }
                    break;
                case R.id.card5:
                    if (card5.getText() != "") {
                        intent.setAction(ACTION_POPULATE);
                        intent.putExtra("CARD_NUMBER", 4);
                        intent.putExtra("CARD5_CARD", cardList[4]);
                        startActivityForResult(intent, 6003);
                    }
                    break;
            }

        }
    };

    private String getEmail(String text) {
        String[] ctext = text.split(" |\n");
        String cemail = "";
        for (String s : ctext) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '@') {
                    cemail = s;
                    emailRead = true;
                    return cemail;
                }
            }
        }
        return "";
    }

    private String getPhone(String text) {
        String[] ctext = text.split("\n");
        for (String s : ctext) {
            String cnumb = "";
            for (int i = 0; i < s.length(); i++) {
                if (!(s.charAt(i) < '0' || s.charAt(i) > '9')) {
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

    protected void directCall() {
        final int REQUEST_PHONE_CALL = 1;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        //checks for permission before placing the call
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                //places call
                startActivity(callIntent); //note have to attempt to call twice on the time running on a phone
            }
        }
    }

    protected void prepMail() {
        setContentView(R.layout.email_layout);

        mEditTexto = findViewById(R.id.edit_text_to);
        mEditTexto.setText(email);
        mEditTextSubject = findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);


        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();

            }
        });
    }

    private void sendMail() {
        String[] recipient = new String[]{mEditTexto.getText().toString()};
        //String[] recipients = recipientList.split(",");
        //example1@gmail.com, example2@gmail.com

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}