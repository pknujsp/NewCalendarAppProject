package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReverseGeoCodingItem implements Parcelable
{
    @Expose
    @SerializedName("sido_nm")
    private String sidoName;

    @Expose
    @SerializedName("sido_cd")
    private String sidoCode;

    @Expose
    @SerializedName("sgg_nm")
    private String sggName;

    @Expose
    @SerializedName("sgg_cd")
    private String sggCode;

    @Expose
    @SerializedName("emdong_nm")
    private String emdongName;

    @Expose
    @SerializedName("emdong_cd")
    private String emdongCode;

    @Expose
    @SerializedName("main_no")
    private String mainNo;

    @Expose
    @SerializedName("sub_no")
    private String subNo;

    @Expose
    @SerializedName("adm_dr_cd")
    private String admDrCode;

    @Expose
    @SerializedName("road_nm")
    private String roadName;

    @Expose
    @SerializedName("road_cd")
    private String roadCode;

    @Expose
    @SerializedName("bd_nm")
    private String buildingName;

    @Expose
    @SerializedName("sub_bd_nm")
    private String subBuildingName;

    @Expose
    @SerializedName("road_nm_main_no")
    private String roadNameMainNo;

    @Expose
    @SerializedName("road_nm_sub_no")
    private String roadNameSubNo;

    @Expose
    @SerializedName("full_addr")
    private String fullAddress;


    protected ReverseGeoCodingItem(Parcel in)
    {
        sidoName = in.readString();
        sidoCode = in.readString();
        sggName = in.readString();
        sggCode = in.readString();
        emdongName = in.readString();
        emdongCode = in.readString();
        mainNo = in.readString();
        subNo = in.readString();
        admDrCode = in.readString();
        roadName = in.readString();
        roadCode = in.readString();
        buildingName = in.readString();
        subBuildingName = in.readString();
        roadNameMainNo = in.readString();
        roadNameSubNo = in.readString();
        fullAddress = in.readString();
    }

    public static final Creator<ReverseGeoCodingItem> CREATOR = new Creator<ReverseGeoCodingItem>()
    {
        @Override
        public ReverseGeoCodingItem createFromParcel(Parcel in)
        {
            return new ReverseGeoCodingItem(in);
        }

        @Override
        public ReverseGeoCodingItem[] newArray(int size)
        {
            return new ReverseGeoCodingItem[size];
        }
    };

    public String getSidoName()
    {
        return sidoName;
    }

    public void setSidoName(String sidoName)
    {
        this.sidoName = sidoName;
    }

    public String getSidoCode()
    {
        return sidoCode;
    }

    public void setSidoCode(String sidoCode)
    {
        this.sidoCode = sidoCode;
    }

    public String getSggName()
    {
        return sggName;
    }

    public void setSggName(String sggName)
    {
        this.sggName = sggName;
    }

    public String getSggCode()
    {
        return sggCode;
    }

    public void setSggCode(String sggCode)
    {
        this.sggCode = sggCode;
    }

    public String getEmdongName()
    {
        return emdongName;
    }

    public void setEmdongName(String emdongName)
    {
        this.emdongName = emdongName;
    }

    public String getEmdongCode()
    {
        return emdongCode;
    }

    public void setEmdongCode(String emdongCode)
    {
        this.emdongCode = emdongCode;
    }

    public String getMainNo()
    {
        return mainNo;
    }

    public void setMainNo(String mainNo)
    {
        this.mainNo = mainNo;
    }

    public String getSubNo()
    {
        return subNo;
    }

    public void setSubNo(String subNo)
    {
        this.subNo = subNo;
    }

    public String getAdmDrCode()
    {
        return admDrCode;
    }

    public void setAdmDrCode(String admDrCode)
    {
        this.admDrCode = admDrCode;
    }

    public String getRoadName()
    {
        return roadName;
    }

    public void setRoadName(String roadName)
    {
        this.roadName = roadName;
    }

    public String getRoadCode()
    {
        return roadCode;
    }

    public void setRoadCode(String roadCode)
    {
        this.roadCode = roadCode;
    }

    public String getBuildingName()
    {
        return buildingName;
    }

    public void setBuildingName(String buildingName)
    {
        this.buildingName = buildingName;
    }

    public String getSubBuildingName()
    {
        return subBuildingName;
    }

    public void setSubBuildingName(String subBuildingName)
    {
        this.subBuildingName = subBuildingName;
    }

    public String getRoadNameMainNo()
    {
        return roadNameMainNo;
    }

    public void setRoadNameMainNo(String roadNameMainNo)
    {
        this.roadNameMainNo = roadNameMainNo;
    }

    public String getRoadNameSubNo()
    {
        return roadNameSubNo;
    }

    public void setRoadNameSubNo(String roadNameSubNo)
    {
        this.roadNameSubNo = roadNameSubNo;
    }

    public String getFullAddress()
    {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress)
    {
        this.fullAddress = fullAddress;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(sidoName);
        parcel.writeString(sidoCode);
        parcel.writeString(sggName);
        parcel.writeString(sggCode);
        parcel.writeString(emdongName);
        parcel.writeString(emdongCode);
        parcel.writeString(mainNo);
        parcel.writeString(subNo);
        parcel.writeString(admDrCode);
        parcel.writeString(roadName);
        parcel.writeString(roadCode);
        parcel.writeString(buildingName);
        parcel.writeString(subBuildingName);
        parcel.writeString(roadNameMainNo);
        parcel.writeString(roadNameSubNo);
        parcel.writeString(fullAddress);
    }
}
