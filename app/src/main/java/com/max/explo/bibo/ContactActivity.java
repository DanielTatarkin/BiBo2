package com.max.explo.bibo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

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

        Intent data = getIntent();
        email.setText(data.getStringExtra("EMAIL"));
        phone.setText(data.getStringExtra("PHONE"));
        name.setText(data.getStringExtra("NAME"));

        if(data.getAction() == MainActivity.ACTION_POPULATE) {
            int CARD_NUMBER = data.getIntExtra("CARD_NUMBER",-1);
            switch (CARD_NUMBER){
                case 1:
                    populateCard((ContactCard) data.getParcelableExtra("CARD1_CARD"));
                    break;
                case 2:
                    populateCard((ContactCard) data.getParcelableExtra("CARD2_CARD"));
                    break;
                case 3:
                    populateCard((ContactCard) data.getParcelableExtra("CARD3_CARD"));
                    break;
                case 4:
                    populateCard((ContactCard) data.getParcelableExtra("CARD4_CARD"));
                    break;
                case 5:
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
}