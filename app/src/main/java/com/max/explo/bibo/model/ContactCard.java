package com.max.explo.bibo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactCard implements Parcelable{

    private String name, companyName, phone, email = "";

    public ContactCard() {
    }


    public ContactCard(String name, String companyName, String phone, String email) {
        this.name = name;
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name,
                                            this.companyName,
                                            this.phone,
                                            this.email});
    }

    public ContactCard(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        this.name = data[0];
        this.companyName = data[1];
        this.phone = data[2];
        this.email = data[3];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ContactCard createFromParcel(Parcel in) {
            return new ContactCard(in);
        }

        public ContactCard[] newArray(int size) {
            return new ContactCard[size];
        }
    };
}
