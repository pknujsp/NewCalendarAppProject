package com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidLandFcstItem implements Parcelable
{
    //중기육상예보
    @Expose
    @SerializedName("regId")
    private String regId;

    @Expose
    @SerializedName("rnSt10")
    private String rnSt10;

    @Expose
    @SerializedName("rnSt3Am")
    private String rnSt3Am;

    @Expose
    @SerializedName("rnSt3Pm")
    private String rnSt3Pm;

    @Expose
    @SerializedName("rnSt4Am")
    private String rnSt4Am;

    @Expose
    @SerializedName("rnSt4Pm")
    private String rnSt4Pm;

    @Expose
    @SerializedName("rnSt5Am")
    private String rnSt5Am;

    @Expose
    @SerializedName("rnSt5Pm")
    private String rnSt5Pm;

    @Expose
    @SerializedName("rnSt6Am")
    private String rnSt6Am;

    @Expose
    @SerializedName("rnSt6Pm")
    private String rnSt6Pm;

    @Expose
    @SerializedName("rnSt7Am")
    private String rnSt7Am;

    @Expose
    @SerializedName("rnSt7Pm")
    private String rnSt7Pm;

    @Expose
    @SerializedName("rnSt8")
    private String rnSt8;

    @Expose
    @SerializedName("rnSt9")
    private String rnSt9;

    @Expose
    @SerializedName("wf10")
    private String wf10;

    @Expose
    @SerializedName("wf3Am")
    private String wf3Am;

    @Expose
    @SerializedName("wf3Pm")
    private String wf3Pm;

    @Expose
    @SerializedName("wf4Am")
    private String wf4Am;

    @Expose
    @SerializedName("wf4Pm")
    private String wf4Pm;

    @Expose
    @SerializedName("wf5Am")
    private String wf5Am;

    @Expose
    @SerializedName("wf5Pm")
    private String wf5Pm;

    @Expose
    @SerializedName("wf6Am")
    private String wf6Am;

    @Expose
    @SerializedName("wf6Pm")
    private String wf6Pm;

    @Expose
    @SerializedName("wf7Am")
    private String wf7Am;

    @Expose
    @SerializedName("wf7Pm")
    private String wf7Pm;

    @Expose
    @SerializedName("wf8")
    private String wf8;

    @Expose
    @SerializedName("wf9")
    private String wf9;

    protected MidLandFcstItem(Parcel in)
    {
        regId = in.readString();
        rnSt10 = in.readString();
        rnSt3Am = in.readString();
        rnSt3Pm = in.readString();
        rnSt4Am = in.readString();
        rnSt4Pm = in.readString();
        rnSt5Am = in.readString();
        rnSt5Pm = in.readString();
        rnSt6Am = in.readString();
        rnSt6Pm = in.readString();
        rnSt7Am = in.readString();
        rnSt7Pm = in.readString();
        rnSt8 = in.readString();
        rnSt9 = in.readString();
        wf10 = in.readString();
        wf3Am = in.readString();
        wf3Pm = in.readString();
        wf4Am = in.readString();
        wf4Pm = in.readString();
        wf5Am = in.readString();
        wf5Pm = in.readString();
        wf6Am = in.readString();
        wf6Pm = in.readString();
        wf7Am = in.readString();
        wf7Pm = in.readString();
        wf8 = in.readString();
        wf9 = in.readString();
    }

    public static final Creator<MidLandFcstItem> CREATOR = new Creator<MidLandFcstItem>()
    {
        @Override
        public MidLandFcstItem createFromParcel(Parcel in)
        {
            return new MidLandFcstItem(in);
        }

        @Override
        public MidLandFcstItem[] newArray(int size)
        {
            return new MidLandFcstItem[size];
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
        parcel.writeString(rnSt10);
        parcel.writeString(rnSt3Am);
        parcel.writeString(rnSt3Pm);
        parcel.writeString(rnSt4Am);
        parcel.writeString(rnSt4Pm);
        parcel.writeString(rnSt5Am);
        parcel.writeString(rnSt5Pm);
        parcel.writeString(rnSt6Am);
        parcel.writeString(rnSt6Pm);
        parcel.writeString(rnSt7Am);
        parcel.writeString(rnSt7Pm);
        parcel.writeString(rnSt8);
        parcel.writeString(rnSt9);
        parcel.writeString(wf10);
        parcel.writeString(wf3Am);
        parcel.writeString(wf3Pm);
        parcel.writeString(wf4Am);
        parcel.writeString(wf4Pm);
        parcel.writeString(wf5Am);
        parcel.writeString(wf5Pm);
        parcel.writeString(wf6Am);
        parcel.writeString(wf6Pm);
        parcel.writeString(wf7Am);
        parcel.writeString(wf7Pm);
        parcel.writeString(wf8);
        parcel.writeString(wf9);
    }

    public String getRegId()
    {
        return regId;
    }

    public MidLandFcstItem setRegId(String regId)
    {
        this.regId = regId;
        return this;
    }

    public String getRnSt10()
    {
        return rnSt10;
    }

    public MidLandFcstItem setRnSt10(String rnSt10)
    {
        this.rnSt10 = rnSt10;
        return this;
    }

    public String getRnSt3Am()
    {
        return rnSt3Am;
    }

    public MidLandFcstItem setRnSt3Am(String rnSt3Am)
    {
        this.rnSt3Am = rnSt3Am;
        return this;
    }

    public String getRnSt3Pm()
    {
        return rnSt3Pm;
    }

    public MidLandFcstItem setRnSt3Pm(String rnSt3Pm)
    {
        this.rnSt3Pm = rnSt3Pm;
        return this;
    }

    public String getRnSt4Am()
    {
        return rnSt4Am;
    }

    public MidLandFcstItem setRnSt4Am(String rnSt4Am)
    {
        this.rnSt4Am = rnSt4Am;
        return this;
    }

    public String getRnSt4Pm()
    {
        return rnSt4Pm;
    }

    public MidLandFcstItem setRnSt4Pm(String rnSt4Pm)
    {
        this.rnSt4Pm = rnSt4Pm;
        return this;
    }

    public String getRnSt5Am()
    {
        return rnSt5Am;
    }

    public MidLandFcstItem setRnSt5Am(String rnSt5Am)
    {
        this.rnSt5Am = rnSt5Am;
        return this;
    }

    public String getRnSt5Pm()
    {
        return rnSt5Pm;
    }

    public MidLandFcstItem setRnSt5Pm(String rnSt5Pm)
    {
        this.rnSt5Pm = rnSt5Pm;
        return this;
    }

    public String getRnSt6Am()
    {
        return rnSt6Am;
    }

    public MidLandFcstItem setRnSt6Am(String rnSt6Am)
    {
        this.rnSt6Am = rnSt6Am;
        return this;
    }

    public String getRnSt6Pm()
    {
        return rnSt6Pm;
    }

    public MidLandFcstItem setRnSt6Pm(String rnSt6Pm)
    {
        this.rnSt6Pm = rnSt6Pm;
        return this;
    }

    public String getRnSt7Am()
    {
        return rnSt7Am;
    }

    public MidLandFcstItem setRnSt7Am(String rnSt7Am)
    {
        this.rnSt7Am = rnSt7Am;
        return this;
    }

    public String getRnSt7Pm()
    {
        return rnSt7Pm;
    }

    public MidLandFcstItem setRnSt7Pm(String rnSt7Pm)
    {
        this.rnSt7Pm = rnSt7Pm;
        return this;
    }

    public String getRnSt8()
    {
        return rnSt8;
    }

    public MidLandFcstItem setRnSt8(String rnSt8)
    {
        this.rnSt8 = rnSt8;
        return this;
    }

    public String getRnSt9()
    {
        return rnSt9;
    }

    public MidLandFcstItem setRnSt9(String rnSt9)
    {
        this.rnSt9 = rnSt9;
        return this;
    }

    public String getWf10()
    {
        return wf10;
    }

    public MidLandFcstItem setWf10(String wf10)
    {
        this.wf10 = wf10;
        return this;
    }

    public String getWf3Am()
    {
        return wf3Am;
    }

    public MidLandFcstItem setWf3Am(String wf3Am)
    {
        this.wf3Am = wf3Am;
        return this;
    }

    public String getWf3Pm()
    {
        return wf3Pm;
    }

    public MidLandFcstItem setWf3Pm(String wf3Pm)
    {
        this.wf3Pm = wf3Pm;
        return this;
    }

    public String getWf4Am()
    {
        return wf4Am;
    }

    public MidLandFcstItem setWf4Am(String wf4Am)
    {
        this.wf4Am = wf4Am;
        return this;
    }

    public String getWf4Pm()
    {
        return wf4Pm;
    }

    public MidLandFcstItem setWf4Pm(String wf4Pm)
    {
        this.wf4Pm = wf4Pm;
        return this;
    }

    public String getWf5Am()
    {
        return wf5Am;
    }

    public MidLandFcstItem setWf5Am(String wf5Am)
    {
        this.wf5Am = wf5Am;
        return this;
    }

    public String getWf5Pm()
    {
        return wf5Pm;
    }

    public MidLandFcstItem setWf5Pm(String wf5Pm)
    {
        this.wf5Pm = wf5Pm;
        return this;
    }

    public String getWf6Am()
    {
        return wf6Am;
    }

    public MidLandFcstItem setWf6Am(String wf6Am)
    {
        this.wf6Am = wf6Am;
        return this;
    }

    public String getWf6Pm()
    {
        return wf6Pm;
    }

    public MidLandFcstItem setWf6Pm(String wf6Pm)
    {
        this.wf6Pm = wf6Pm;
        return this;
    }

    public String getWf7Am()
    {
        return wf7Am;
    }

    public MidLandFcstItem setWf7Am(String wf7Am)
    {
        this.wf7Am = wf7Am;
        return this;
    }

    public String getWf7Pm()
    {
        return wf7Pm;
    }

    public MidLandFcstItem setWf7Pm(String wf7Pm)
    {
        this.wf7Pm = wf7Pm;
        return this;
    }

    public String getWf8()
    {
        return wf8;
    }

    public MidLandFcstItem setWf8(String wf8)
    {
        this.wf8 = wf8;
        return this;
    }

    public String getWf9()
    {
        return wf9;
    }

    public MidLandFcstItem setWf9(String wf9)
    {
        this.wf9 = wf9;
        return this;
    }
}
