package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

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
    @TypeConverters({TypeConverter.class})
    private Date startDate;

    @ColumnInfo(name = "end_date")
    @TypeConverters({TypeConverter.class})
    private Date endDate;

    @ColumnInfo(name = "noti_main_type")
    private int notiMainType;

    @ColumnInfo(name = "noti_time")
    @TypeConverters({TypeConverter.class})
    private Date notiTime;

    @ColumnInfo(name = "noti_time_day")
    private int notiDay;

    @ColumnInfo(name = "noti_time_hour")
    private int notiHour;

    @ColumnInfo(name = "noti_time_minute")
    private int notiMinute;

    @ColumnInfo(name = "place_to_be_visited")
    private int place;

    @ColumnInfo(name = "address_to_be_visited")
    private int address;

    @ColumnInfo(name = "inserted_date")
    @TypeConverters({TypeConverter.class})
    private Date insertedDate;

    @ColumnInfo(name = "updated_date")
    @TypeConverters({TypeConverter.class})
    private Date updatedDate;

    @Ignore
    public static final int NOT_LOCATION = 0;

    @Ignore
    public static final int SELECTED_LOCATION = 1;

    @Ignore
    public static final int GOOGLE_CATEGORY = 0;

    @Ignore
    public static final int LOCAL_CATEGORY = 1;


    @Ignore
    public static final int MAIN_DAY = 2;

    @Ignore
    public static final int MAIN_HOUR = 3;

    @Ignore
    public static final int MAIN_MINUTE = 4;

    @Ignore
    public static final int NOT_NOTI = 5;

    public ScheduleDTO()
    {
    }

    protected ScheduleDTO(Parcel in)
    {
        id = in.readInt();
        category = in.readInt();
        subject = in.readString();
        content = in.readString();
        startDate = (Date) in.readSerializable();
        endDate = (Date) in.readSerializable();
        notiMainType = in.readInt();
        notiTime = (Date) in.readSerializable();
        notiDay = in.readInt();
        notiHour = in.readInt();
        notiMinute = in.readInt();
        place = in.readInt();
        address = in.readInt();
        insertedDate = (Date) in.readSerializable();
        updatedDate = (Date) in.readSerializable();
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


    public int getPlace()
    {
        return place;
    }

    public void setPlace(int place)
    {
        this.place = place;
    }

    public int getAddress()
    {
        return address;
    }

    public void setAddress(int address)
    {
        this.address = address;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getNotiTime()
    {
        return notiTime;
    }

    public void setNotiTime(Date notiTime)
    {
        this.notiTime = notiTime;
    }

    public Date getInsertedDate()
    {
        return insertedDate;
    }

    public void setInsertedDate(Date insertedDate)
    {
        this.insertedDate = insertedDate;
    }

    public Date getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    public int getNotiMainType()
    {
        return notiMainType;
    }

    public void setNotiMainType(int notiMainType)
    {
        this.notiMainType = notiMainType;
    }

    public int getNotiDay()
    {
        return notiDay;
    }

    public void setNotiDay(int notiDay)
    {
        this.notiDay = notiDay;
    }

    public int getNotiHour()
    {
        return notiHour;
    }

    public void setNotiHour(int notiHour)
    {
        this.notiHour = notiHour;
    }

    public int getNotiMinute()
    {
        return notiMinute;
    }

    public void setNotiMinute(int notiMinute)
    {
        this.notiMinute = notiMinute;
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
        parcel.writeSerializable(startDate);
        parcel.writeSerializable(endDate);
        parcel.writeInt(notiMainType);
        parcel.writeSerializable(notiTime);
        parcel.writeInt(notiDay);
        parcel.writeInt(notiHour);
        parcel.writeInt(notiMinute);
        parcel.writeInt(place);
        parcel.writeInt(address);
        parcel.writeSerializable(insertedDate);
        parcel.writeSerializable(updatedDate);
    }

    public boolean isEmpty()
    {
        if (subject == null)
        {
            return true;
        } else
        {
            return false;
        }
    }
}
