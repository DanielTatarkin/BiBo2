package com.max.explo.bibo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.max.explo.bibo.model.ContactCard;

public class ContactActivity extends AppCompatActivity {

    private EditText name;
    private EditText phone;
    private EditText email;
    private EditText companyName;
    private Button exportButton;
    private Button saveButton;

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
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactCard contactCard = new ContactCard(  name.getText().toString(),
                                                            companyName.getText().toString(),
                                                            phone.getText().toString(),
                                                            email.getText().toString());
                Intent data = getIntent();
                Parcelable[] parcelables = data.getParcelableArrayExtra(MainActivity.CARD)
                data.putPa
            }
        });
    }
}