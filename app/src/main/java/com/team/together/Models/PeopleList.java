package com.team.together.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class PeopleList implements Parcelable {
    private String phone_number;//phone_number

    public PeopleList(String phone_number) {
        this.phone_number = phone_number;
    }

    public PeopleList() {
    }

    protected PeopleList(Parcel in) {
        phone_number = in.readString();
    }

    public static final Creator<PeopleList> CREATOR = new Creator<PeopleList>() {
        @Override
        public PeopleList createFromParcel(Parcel in) {
            return new PeopleList(in);
        }

        @Override
        public PeopleList[] newArray(int size) {
            return new PeopleList[size];
        }
    };

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "PeopleList{" +
                "phone_number='" + phone_number + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone_number);
    }
}
