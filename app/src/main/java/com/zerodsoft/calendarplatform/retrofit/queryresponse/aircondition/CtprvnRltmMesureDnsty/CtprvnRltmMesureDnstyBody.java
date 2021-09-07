package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.AirConditionRoot;

import java.util.List;

public class CtprvnRltmMesureDnstyBody extends AirConditionRoot implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<CtprvnRltmMesureDnstyItem> item;


    protected CtprvnRltmMesureDnstyBody(Parcel in)
    {
        item = in.createTypedArrayList(CtprvnRltmMesureDnstyItem.CREATOR);
    }

    public static final Creator<CtprvnRltmMesureDnstyBody> CREATOR = new Creator<CtprvnRltmMesureDnstyBody>()
    {
        @Override
        public CtprvnRltmMesureDnstyBody createFromParcel(Parcel in)
        {
            return new CtprvnRltmMesureDnstyBody(in);
        }

        @Override
        public CtprvnRltmMesureDnstyBody[] newArray(int size)
        {
            return new CtprvnRltmMesureDnstyBody[size];
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
        parcel.writeTypedList(item);
    }

    public void setItem(List<CtprvnRltmMesureDnstyItem> item)
    {
        this.item = item;
    }

    public List<CtprvnRltmMesureDnstyItem> getItem()
    {
        return item;
    }
}
