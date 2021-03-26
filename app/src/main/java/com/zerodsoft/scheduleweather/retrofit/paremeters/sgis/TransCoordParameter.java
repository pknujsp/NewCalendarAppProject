package com.zerodsoft.scheduleweather.retrofit.paremeters.sgis;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class TransCoordParameter implements Parcelable
{
    public static final String BESSEL = "EPSG:4004";
    public static final String WGS84 = "EPSG:4326";
    public static final String WEST_ORIGIN = "EPSG:5185";
    public static final String JUNGBU_ORIGIN = "EPSG:5181";
    public static final String EAST_ORIGIN = "EPSG:5187";
    public static final String DONGHAE_UllEUNG_ORIGIN = "EPSG:5188";
    public static final String UTM_K = "EPSG:5179";
    public static final String GOOGLE_MERCATOR = "EPSG:900913";

    private String accessToken;
    private String src;
    private String dst;
    private String posX;
    private String posY;

    public TransCoordParameter()
    {
    }

    protected TransCoordParameter(Parcel in)
    {
        accessToken = in.readString();
        src = in.readString();
        dst = in.readString();
        posX = in.readString();
        posY = in.readString();
    }

    public static final Creator<TransCoordParameter> CREATOR = new Creator<TransCoordParameter>()
    {
        @Override
        public TransCoordParameter createFromParcel(Parcel in)
        {
            return new TransCoordParameter(in);
        }

        @Override
        public TransCoordParameter[] newArray(int size)
        {
            return new TransCoordParameter[size];
        }
    };

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    public String getDst()
    {
        return dst;
    }

    public void setDst(String dst)
    {
        this.dst = dst;
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
        parcel.writeString(accessToken);
        parcel.writeString(src);
        parcel.writeString(dst);
        parcel.writeString(posX);
        parcel.writeString(posY);
    }

    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("src", src);
        map.put("dst", dst);
        map.put("posX", posX);
        map.put("posY", posY);

        return map;
    }
}
