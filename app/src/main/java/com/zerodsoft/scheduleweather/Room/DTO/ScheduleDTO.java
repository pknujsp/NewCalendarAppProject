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
    private int startDate;

    @ColumnInfo(name = "end_date")
    private int endDate;

    @ColumnInfo(name = "noti_time")
    private int notiTime;

    @ColumnInfo(name = "place_id_to_be_visited")
    private int placeId;

    @ColumnInfo(name = "address_id_to_be_visited")
    private int addressId;

    @ColumnInfo(name = "inserted_date")
    private int insertedDate;

    @ColumnInfo(name = "updated_date")
    private int updatedDate;

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
        startDate = in.readInt();
        endDate = in.readInt();
        notiTime = in.readInt();
        placeId = in.readInt();
        addressId = in.readInt();
        insertedDate = in.readInt();
        updatedDate = in.readInt();
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

    public int getStartDate()
    {
        return startDate;
    }

    public void setStartDate(int startDate)
    {
        this.startDate = startDate;
    }

    public int getEndDate()
    {
        return endDate;
    }

    public void setEndDate(int endDate)
    {
        this.endDate = endDate;
    }

    public int getNotiTime()
    {
        return notiTime;
    }

    public void setNotiTime(int notiTime)
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

    public int getInsertedDate()
    {
        return insertedDate;
    }

    public void setInsertedDate(int insertedDate)
    {
        this.insertedDate = insertedDate;
    }

    public int getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(int updatedDate)
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
        parcel.writeInt(startDate);
        parcel.writeInt(endDate);
        parcel.writeInt(notiTime);
        parcel.writeInt(placeId);
        parcel.writeInt(addressId);
        parcel.writeInt(insertedDate);
        parcel.writeInt(updatedDate);
    }
}
