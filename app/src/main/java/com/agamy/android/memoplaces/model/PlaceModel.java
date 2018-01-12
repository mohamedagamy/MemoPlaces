package com.agamy.android.memoplaces.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by agamy on 12/24/2017.
 */

public class PlaceModel implements Parcelable{

    private int id;
    private double latitude;
    private double longitude;
    private String address;
    private String country;

    public PlaceModel(String address) {
        this.address = address;
    }


    public PlaceModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlaceModel(int id , double latitude, double longitude, String address, String country) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.country = country;
    }

    public PlaceModel(double latitude, double longitude, String address, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.country = country;
    }




    public PlaceModel(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    protected PlaceModel(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }

    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public LatLng getLatLng()
    {
        return new LatLng(latitude , longitude);
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(address);
    }

    @Override
    public String toString() {
        return address;
    }
}
