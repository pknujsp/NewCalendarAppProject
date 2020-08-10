package com.zerodsoft.scheduleweather.Room.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TB_SCHEDULE")
public class ScheduleDTO implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private int id;

    @ColumnInfo(name = "category")
    private int category;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "start_date")
    private float startDate;

    @ColumnInfo(name = "end_date")
    private float endDate;

    @ColumnInfo(name = "noti_time")
    private float notiTime;

    @ColumnInfo(name = "place_id_to_be_visited")
    private int placeId;

    @ColumnInfo(name = "address_id_to_be_visited")
    private int addressId;

    @ColumnInfo(name = "inserted_date")
    private float insertedDate;

    @ColumnInfo(name = "updated_date")
    private float updatedDate;

    @Ignore
    public static final int GOOGLE_CATEGORY = 0;

    @Ignore
    public static final int LOCAL_CATEGORY = 1;

    public ScheduleDTO()
    {
    }

    protected ScheduleDTO(Parcel in)
    {
        id = in.readInt();
        category = in.readInt();
        subject = in.readString();
        content = in.readString();
        startDate = in.readFloat();
        endDate = in.readFloat();
        notiTime = in.readFloat();
        placeId = in.readInt();
        addressId = in.readInt();
        insertedDate = in.readFloat();
        updatedDate = in.readFloat();
    }

    public static final Creator<ScheduleDTO> CREATOR = new Creator<ScheduleDTO>()
    {
        @Override
        public ScheduleDTO createFromParcel(Parcel in)
        {
            return new ScheduleDTO(in);
        }

        @Override
        public ScheduleDTO[] newArray(int size)
        {
            return new ScheduleDTO[size];
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

    public int getCategory()
    {
        return category;
    }

    public void setCategory(int category)
    {
        this.category = category;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public float getStartDate()
    {
        return startDate;
    }

    public void setStartDate(float startDate)
    {
        this.startDate = startDate;
    }

    public float getEndDate()
    {
        return endDate;
    }

    public void setEndDate(float endDate)
    {
        this.endDate = endDate;
    }

    public float getNotiTime()
    {
        return notiTime;
    }

    public void setNotiTime(float notiTime)
    {
        this.notiTime = notiTime;
    }

    public int getPlaceId()
    {
        return placeId;
    }

    public void setPlaceId(int placeId)
    {
        this.placeId = placeId;
    }

    public int getAddressId()
    {
        return addressId;
    }

    public void setAddressId(int addressId)
    {
        this.addressId = addressId;
    }

    public float getInsertedDate()
    {
        return insertedDate;
    }

    public void setInsertedDate(float insertedDate)
    {
        this.insertedDate = insertedDate;
    }

    public float getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(float updatedDate)
    {
        this.updatedDate = updatedDate;
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
        parcel.writeInt(category);
        parcel.writeString(subject);
        parcel.writeString(content);
        parcel.writeFloat(startDate);
        parcel.writeFloat(endDate);
        parcel.writeFloat(notiTime);
        parcel.writeInt(placeId);
        parcel.writeInt(addressId);
        parcel.writeFloat(insertedDate);
        parcel.writeFloat(updatedDate);
    }
}
