package com.zerodsoft.scheduleweather.activity.map.fragment.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

public class SearchData implements Parcelable
{
    private String searchWord;
    private LocalApiPlaceParameter parameter;

    public SearchData(String searchWord, LocalApiPlaceParameter parameter)
    {
        this.searchWord = searchWord;
        this.parameter = parameter;
    }

    protected SearchData(Parcel in)
    {
        searchWord = in.readString();
        parameter = in.readParcelable(LocalApiPlaceParameter.class.getClassLoader());
    }

    public static final Creator<SearchData> CREATOR = new Creator<SearchData>()
    {
        @Override
        public SearchData createFromParcel(Parcel in)
        {
            return new SearchData(in);
        }

        @Override
        public SearchData[] newArray(int size)
        {
            return new SearchData[size];
        }
    };

    public LocalApiPlaceParameter getParameter()
    {
        return parameter;
    }

    public String getSearchWord()
    {
        return searchWord;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(searchWord);
        parcel.writeParcelable(parameter, i);
    }
}
