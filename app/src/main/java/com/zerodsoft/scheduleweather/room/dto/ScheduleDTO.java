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

    @ColumnInfo(name = "date_type")
    private int dateType;

    @ColumnInfo(name = "start_date")
    @TypeConverters({TypeConverter.class})
    private Date startDate;

    @ColumnInfo(name = "end_date")
    @TypeConverters({TypeConverter.class})
    private Date endDate;

    @ColumnInfo(name = "noti_time")
    @TypeConverters({TypeConverter.class})
    private Date notiTime;

    @ColumnInfo(name = "noti_time_day")
    private int notiDay;

    @ColumnInfo(name = "noti_time_hour")
    private int notiHour;

    @ColumnInfo(name = "noti_time_minute")
    private int notiMinute;

    @ColumnInfo(name = "inserted_date")
    @TypeConverters({TypeConverter.class})
    private Date insertedDate;

    @ColumnInfo(name = "updated_date")
    @TypeConverters({TypeConverter.class})
    private Date updatedDate;


    @Ignore
    public static final int DATE_NOT_ALLDAY = 0;

    @Ignore
    public static final int DATE_ALLDAY = 1;


    @Ignore
    public static final int GOOGLE_CATEGORY = 0;

    @Ignore
    public static final int LOCAL_CATEGORY = 1;

    @Ignore
    public static final int ALL_CATEGORY = 2;


    @Ignore
    public static final int NOT_SELECTED = -1;


    @Ignore
    public boolean updatedLocation;

    @Ignore
    public boolean deletedLocation;

    @Ignore
    public boolean addedLocation;

    @Ignore
    private boolean isDrawed;

    public ScheduleDTO()
    {
        category = NOT_SELECTED;
        dateType = DATE_NOT_ALLDAY;

        subject = "";
        content = "";
    }

    protected ScheduleDTO(Parcel in)
    {
        id = in.readInt();
        category = in.readInt();
        subject = in.readString();
        content = in.readString();
        dateType = in.readInt();
        startDate = (Date) in.readSerializable();
        endDate = (Date) in.readSerializable();
        notiTime = (Date) in.readSerializable();
        notiDay = in.readInt();
        notiHour = in.readInt();
        notiMinute = in.readInt();
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


    public void setDateType(int dateType)
    {
        this.dateType = dateType;
    }

    public int getDateType()
    {
        return dateType;
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
        parcel.writeInt(dateType);
        parcel.writeSerializable(startDate);
        parcel.writeSerializable(endDate);
        parcel.writeSerializable(notiTime);
        parcel.writeInt(notiDay);
        parcel.writeInt(notiHour);
        parcel.writeInt(notiMinute);
        parcel.writeSerializable(insertedDate);
        parcel.writeSerializable(updatedDate);
    }

    public boolean isEmpty()
    {
        if (subject.isEmpty() && startDate == null)
        {
            return true;
        } else
        {
            return false;
        }
    }
}
