package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.SgisRoot;

public class TransCoordResult implements Parcelable
{
    @Expose
    @SerializedName("toSrs")
    private String toStr;

    @Expose
    @SerializedName("posX")
    private String posX;

    @Expose
    @SerializedName("posY")
    private String posY;

    protected TransCoordResult(Parcel in)
    {
        toStr = in.readString();
        posX = in.readString();
        posY = in.readString();
    }

    public static final Creator<TransCoordResult> CREATOR = new Creator<TransCoordResult>()
    {
        @Override
        public TransCoordResult createFromParcel(Parcel in)
        {
            return new TransCoordResult(in);
        }

        @Override
        public TransCoordResult[] newArray(int size)
        {
            return new TransCoordResult[size];
        }
    };

    public String getToStr()
    {
        return toStr;
    }

    public void setToStr(String toStr)
    {
        this.toStr = toStr;
    }

    public String getPosX()
    {
        return posX;
    }

    public void setPosX(String posX)
    {
        this.posX = posX;
    }

    public String getPosY()
    {
        return posY;
    }

    public void setPosY(String posY)
    {
        this.posY = posY;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(toStr);
        parcel.writeString(posX);
        parcel.writeString(posY);
    }
}
