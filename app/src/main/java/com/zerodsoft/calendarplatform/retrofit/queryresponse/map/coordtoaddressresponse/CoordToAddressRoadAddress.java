package com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordToAddressRoadAddress implements Parcelable
{
    @SerializedName("address_name")
    @Expose
    private String addressName;

    @SerializedName("region_1depth_name")
    @Expose
    private String region1DepthName;

    @SerializedName("region_2depth_name")
    @Expose
    private String region2DepthName;

    @SerializedName("region_3depth_name")
    @Expose
    private String region3DepthName;

    @SerializedName("road_name")
    @Expose
    private String roadName;

    @SerializedName("underground_yn")
    @Expose
    private String undergroundYn;

    @SerializedName("main_building_no")
    @Expose
    private String mainBuildingNo;

    @SerializedName("sub_building_no")
    @Expose
    private String subBuildingNo;

    @SerializedName("building_name")
    @Expose
    private String buildingName;

    @SerializedName("zone_no")
    @Expose
    private String zoneNo;

    protected CoordToAddressRoadAddress(Parcel in)
    {
        addressName = in.readString();
        region1DepthName = in.readString();
        region2DepthName = in.readString();
        region3DepthName = in.readString();
        roadName = in.readString();
        undergroundYn = in.readString();
        mainBuildingNo = in.readString();
        subBuildingNo = in.readString();
        buildingName = in.readString();
        zoneNo = in.readString();
    }

    public static final Creator<CoordToAddressRoadAddress> CREATOR = new Creator<CoordToAddressRoadAddress>()
    {
        @Override
        public CoordToAddressRoadAddress createFromParcel(Parcel in)
        {
            return new CoordToAddressRoadAddress(in);
        }

        @Override
        public CoordToAddressRoadAddress[] newArray(int size)
        {
            return new CoordToAddressRoadAddress[size];
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
        parcel.writeString(addressName);
        parcel.writeString(region1DepthName);
        parcel.writeString(region2DepthName);
        parcel.writeString(region3DepthName);
        parcel.writeString(roadName);
        parcel.writeString(undergroundYn);
        parcel.writeString(mainBuildingNo);
        parcel.writeString(subBuildingNo);
        parcel.writeString(buildingName);
        parcel.writeString(zoneNo);
    }

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getRegion1DepthName()
    {
        return region1DepthName;
    }

    public void setRegion1DepthName(String region1DepthName)
    {
        this.region1DepthName = region1DepthName;
    }

    public String getRegion2DepthName()
    {
        return region2DepthName;
    }

    public void setRegion2DepthName(String region2DepthName)
    {
        this.region2DepthName = region2DepthName;
    }

    public String getRegion3DepthName()
    {
        return region3DepthName;
    }

    public void setRegion3DepthName(String region3DepthName)
    {
        this.region3DepthName = region3DepthName;
    }

    public String getRoadName()
    {
        return roadName;
    }

    public void setRoadName(String roadName)
    {
        this.roadName = roadName;
    }

    public String getUndergroundYn()
    {
        return undergroundYn;
    }

    public void setUndergroundYn(String undergroundYn)
    {
        this.undergroundYn = undergroundYn;
    }

    public String getMainBuildingNo()
    {
        return mainBuildingNo;
    }

    public void setMainBuildingNo(String mainBuildingNo)
    {
        this.mainBuildingNo = mainBuildingNo;
    }

    public String getSubBuildingNo()
    {
        return subBuildingNo;
    }

    public void setSubBuildingNo(String subBuildingNo)
    {
        this.subBuildingNo = subBuildingNo;
    }

    public String getBuildingName()
    {
        return buildingName;
    }

    public void setBuildingName(String buildingName)
    {
        this.buildingName = buildingName;
    }

    public String getZoneNo()
    {
        return zoneNo;
    }

    public void setZoneNo(String zoneNo)
    {
        this.zoneNo = zoneNo;
    }
}
