package com.zerodsoft.scheduleweather.Room.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "TB_SCHEDULE_CATEGORY")
public class ScheduleCategoryDTO implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    public static final int GOOGLE_SCHEDULE = 0;

    @Ignore
    public static final int LOCAL_SCHEDULE = 1;

    public ScheduleCategoryDTO()
    {
    }

    protected ScheduleCategoryDTO(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<ScheduleCategoryDTO> CREATOR = new Creator<ScheduleCategoryDTO>()
    {
        @Override
        public ScheduleCategoryDTO createFromParcel(Parcel in)
        {
            return new ScheduleCategoryDTO(in);
        }

        @Override
        public ScheduleCategoryDTO[] newArray(int size)
        {
            return new ScheduleCategoryDTO[size];
        }
    };

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(id);
        parcel.writeString(name);
    }
}
