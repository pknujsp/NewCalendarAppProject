package com.zerodsoft.scheduleweather.retrofit.paremeters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class LocalApiPlaceParameter implements Parcelable, Cloneable
{
    private String query;
    private String categoryGroupCode;
    private String x;
    private String y;
    private String radius;
    private String page;
    private String size;
    private String sort;
    private String rect;

    public static final String DEFAULT_RADIUS = "5000";
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_SIZE = "15";
    public static final String SORT_ACCURACY = "accuracy";
    public static final String SORT_DISTANCE = "distance";


    public static final Creator<LocalApiPlaceParameter> CREATOR = new Creator<LocalApiPlaceParameter>()
    {
        @Override
        public LocalApiPlaceParameter createFromParcel(Parcel in)
        {
            return new LocalApiPlaceParameter(in);
        }

        @Override
        public LocalApiPlaceParameter[] newArray(int size)
        {
            return new LocalApiPlaceParameter[size];
        }
    };

    public String getQuery()
    {
        return query;
    }

    public LocalApiPlaceParameter setQuery(String query)
    {
        this.query = query;
        return this;
    }

    public String getCategoryGroupCode()
    {
        return categoryGroupCode;
    }

    public LocalApiPlaceParameter setCategoryGroupCode(String categoryGroupCode)
    {
        this.categoryGroupCode = categoryGroupCode;
        return this;
    }

    public String getX()
    {
        return x;
    }

    public LocalApiPlaceParameter setX(double x)
    {
        this.x = Double.toString(x);
        return this;
    }

    public String getY()
    {
        return y;
    }

    public LocalApiPlaceParameter setY(double y)
    {
        this.y = Double.toString(y);
        return this;
    }

    public String getRadius()
    {
        return radius;
    }

    public LocalApiPlaceParameter setRadius(String radius)
    {
        this.radius = radius;
        return this;
    }

    public String getPage()
    {
        return page;
    }

    public LocalApiPlaceParameter setPage(String page)
    {
        this.page = page;
        return this;
    }

    public String getSize()
    {
        return size;
    }

    public LocalApiPlaceParameter setSize(String size)
    {
        this.size = size;
        return this;
    }

    public String getSort()
    {
        return sort;
    }

    public LocalApiPlaceParameter setSort(String sort)
    {
        this.sort = sort;
        return this;
    }

    public String getRect()
    {
        return rect;
    }

    public LocalApiPlaceParameter setRect(String rect)
    {
        this.rect = rect;
        return this;
    }

    public Map<String, String> getParameterMap()
    {
        Map<String, String> map = new HashMap<>();

        if (query != null)
        {
            map.put("query", query);
        }
        if (categoryGroupCode != null)
        {
            map.put("category_group_code", categoryGroupCode);
        }
        if (x != null)
        {
            map.put("x", x);
        }
        if (y != null)
        {
            map.put("y", y);
        }
        if (radius != null)
        {
            map.put("radius", radius);
        }
        if (page != null)
        {
            map.put("page", page);
        }
        if (size != null)
        {
            map.put("size", size);
        }
        if (sort != null)
        {
            map.put("sort", sort);
        }
        if (rect != null)
        {
            map.put("rect", rect);
        }
        return map;
    }

    public void clear()
    {
        query = null;
        categoryGroupCode = null;
        x = null;
        y = null;
        radius = null;
        page = null;
        size = null;
        sort = null;
        rect = null;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(query);
        parcel.writeString(categoryGroupCode);
        parcel.writeString(x);
        parcel.writeString(y);
        parcel.writeString(radius);
        parcel.writeString(page);
        parcel.writeString(size);
        parcel.writeString(sort);
        parcel.writeString(rect);
    }

    public LocalApiPlaceParameter()
    {

    }

    public LocalApiPlaceParameter(Parcel in)
    {
        query = in.readString();
        categoryGroupCode = in.readString();
        x = in.readString();
        y = in.readString();
        radius = in.readString();
        page = in.readString();
        size = in.readString();
        sort = in.readString();
        rect = in.readString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
