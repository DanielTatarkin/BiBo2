package com.max.explo.bibo;

import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.max.explo.bibo.model.ContactCard;


public class ContactActivity extends AppCompatActivity {

    private EditText name;
    private EditText phone;
    private EditText email;
    private EditText companyName;
    private Button exportButton;
    private Button saveButton;
    private boolean editing;
    private int CARD_NUMBER;
    private boolean emailRead;
    private boolean phoneRead;
    private boolean addButtonFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        name = findViewById(R.id.nameCardText);
        phone = findViewById(R.id.phoneText);
        email = findViewById(R.id.emailText);
        companyName = findViewById(R.id.companyNameText);
        exportButton = findViewById(R.id.exportButton);
        saveButton = findViewById(R.id.saveButton);
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                intent.putExtra(OcrCaptureActivity.UseFlash, false);

                startActivityForResult(intent, MainActivity.RC_OCR_CAPTURE);
            }
        });

        Intent data = getIntent();
        email.setText(data.getStringExtra("EMAIL"));
        phone.setText(data.getStringExtra("PHONE"));
        name.setText(data.getStringExtra("NAME"));

        if(data.getAction() == MainActivity.ACTION_POPULATE) {
            CARD_NUMBER = data.getIntExtra("CARD_NUMBER",-1);
            switch (CARD_NUMBER){
                case 0:
                    populateCard((ContactCard) data.getParcelableExtra("CARD1_CARD"));
                    break;
                case 1:
                    populateCard((ContactCard) data.getParcelableExtra("CARD2_CARD"));
                    break;
                case 2:
                    populateCard((ContactCard) data.getParcelableExtra("CARD3_CARD"));
                    break;
                case 3:
                    populateCard((ContactCard) data.getParcelableExtra("CARD4_CARD"));
                    break;
                case 4:
                    populateCard((ContactCard) data.getParcelableExtra("CARD5_CARD"));
                    break;
            }
        }



        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates a new Intent to insert a contact
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                // Sets the MIME type to match the Contacts Provider
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email.getText())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phone.getText())
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString())
                        .putExtra(ContactsContract.Intents.Insert.COMPANY, companyName.getText().toString());

                startActivity(intent);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactCard contactCard = new ContactCard(name.getText().toString(),
                        companyName.getText().toString(),
                        phone.getText().toString(),
                        email.getText().toString());
                Intent test = new Intent();
                test.putExtra("CONTACT_CARD", contactCard);
                if (editing) test.putExtra("CARD_NUMBER", CARD_NUMBER);
                setResult(RESULT_OK, test);
                finish();
            }
        });


    }

    private void populateCard(ContactCard contactCard){
        name.setText(contactCard.getName());
        phone.setText(contactCard.getPhone());
        email.setText(contactCard.getEmail());
        companyName.setText(contactCard.getCompanyName());
        editing = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    getEmail(text);
                    if (emailRead) email.setText(getEmail(text));
                    getPhone(text);
                    if (phoneRead) phone.setText(getPhone(text));
                    if (!emailRead && !phoneRead) name.setText(getName(text));
                    editing = true;
                }
            }
        }
    }

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
}