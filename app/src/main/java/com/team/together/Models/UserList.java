package com.team.together.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserList implements Parcelable {

    private String user_id;
    private String phone_number;
    private String date_created;


    public UserList(String user_id, String phone_number, String date_created) {
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.date_created = date_created;
    }

    protected UserList(Parcel in) {
        user_id = in.readString();
        phone_number = in.readString();
        date_created = in.readString();
    }

    public static final Creator<UserList> CREATOR = new Creator<UserList>() {
        @Override
        public UserList createFromParcel(Parcel in) {
            return new UserList(in);
        }

        @Override
        public UserList[] newArray(int size) {
            return new UserList[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public UserList(){

    }

    @Override
    public String toString() {
        return "UserList{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(phone_number);
        parcel.writeString(date_created);
    }
}
