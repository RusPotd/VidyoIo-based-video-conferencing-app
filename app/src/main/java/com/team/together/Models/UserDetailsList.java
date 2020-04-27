package com.team.together.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetailsList implements Parcelable {

    private String user_id;
    private String date_created;
    private String username;
    private String profile_img;
    private String phone_number;
    private String about;

    public UserDetailsList(String user_id, String date_created, String username, String profile_img, String phone_number, String about) {
        this.user_id = user_id;
        this.date_created = date_created;
        this.username = username;
        this.profile_img = profile_img;
        this.phone_number = phone_number;
        this.about = about;
    }

    public UserDetailsList() {

    }

    protected UserDetailsList(Parcel in) {
        user_id = in.readString();
        date_created = in.readString();
        username = in.readString();
        profile_img = in.readString();
        phone_number = in.readString();
        about = in.readString();
    }

    public static final Creator<UserDetailsList> CREATOR = new Creator<UserDetailsList>() {
        @Override
        public UserDetailsList createFromParcel(Parcel in) {
            return new UserDetailsList(in);
        }

        @Override
        public UserDetailsList[] newArray(int size) {
            return new UserDetailsList[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return "UserDetailsList{" +
                "user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                ", username='" + username + '\'' +
                ", profile_img='" + profile_img + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", about='" + about + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(date_created);
        dest.writeString(username);
        dest.writeString(profile_img);
        dest.writeString(phone_number);
        dest.writeString(about);
    }
}
