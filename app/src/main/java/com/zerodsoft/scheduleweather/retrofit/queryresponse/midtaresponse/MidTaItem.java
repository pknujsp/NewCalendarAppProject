package com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidTaItem implements Parcelable
{
    @Expose
    @SerializedName("regId")
    private String regId;


    @Expose
    @SerializedName("taMax3")
    private String taMax3;


    @Expose
    @SerializedName("taMax4")
    private String taMax4;


    @Expose
    @SerializedName("taMax5")
    private String taMax5;


    @Expose
    @SerializedName("taMax6")
    private String taMax6;


    @Expose
    @SerializedName("taMax7")
    private String taMax7;


    @Expose
    @SerializedName("taMax8")
    private String taMax8;


    @Expose
    @SerializedName("taMax9")
    private String taMax9;

    @Expose
    @SerializedName("taMax10")
    private String taMax10;


    @Expose
    @SerializedName("taMin3")
    private String taMin3;


    @Expose
    @SerializedName("taMin4")
    private String taMin4;


    @Expose
    @SerializedName("taMin5")
    private String taMin5;


    @Expose
    @SerializedName("taMin6")
    private String taMin6;


    @Expose
    @SerializedName("taMin7")
    private String taMin7;


    @Expose
    @SerializedName("taMin8")
    private String taMin8;


    @Expose
    @SerializedName("taMin9")
    private String taMin9;

    @Expose
    @SerializedName("taMin10")
    private String taMin10;


    protected MidTaItem(Parcel in)
    {
        regId = in.readString();
        taMax3 = in.readString();
        taMax4 = in.readString();
        taMax5 = in.readString();
        taMax6 = in.readString();
        taMax7 = in.readString();
        taMax8 = in.readString();
        taMax9 = in.readString();
        taMax10 = in.readString();
        taMin3 = in.readString();
        taMin4 = in.readString();
        taMin5 = in.readString();
        taMin6 = in.readString();
        taMin7 = in.readString();
        taMin8 = in.readString();
        taMin9 = in.readString();
        taMin10 = in.readString();
    }

    public static final Creator<MidTaItem> CREATOR = new Creator<MidTaItem>()
    {
        @Override
        public MidTaItem createFromParcel(Parcel in)
        {
            return new MidTaItem(in);
        }

        @Override
        public MidTaItem[] newArray(int size)
        {
            return new MidTaItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(regId);
        parcel.writeString(taMax3);
        parcel.writeString(taMax4);
        parcel.writeString(taMax5);
        parcel.writeString(taMax6);
        parcel.writeString(taMax7);
        parcel.writeString(taMax8);
        parcel.writeString(taMax9);
        parcel.writeString(taMax10);
        parcel.writeString(taMin3);
        parcel.writeString(taMin4);
        parcel.writeString(taMin5);
        parcel.writeString(taMin6);
        parcel.writeString(taMin7);
        parcel.writeString(taMin8);
        parcel.writeString(taMin9);
        parcel.writeString(taMin10);
    }

    public String[] getMinArr()
    {
        String[] arr = {taMin3, taMin4, taMin5, taMin6, taMin7, taMin8, taMin9, taMin10};
        return arr;
    }

    public String[] getMaxArr()
    {
        String[] arr = {taMax3, taMax4, taMax5, taMax6, taMax7, taMax8, taMax9, taMax10};
        return arr;
    }
}
