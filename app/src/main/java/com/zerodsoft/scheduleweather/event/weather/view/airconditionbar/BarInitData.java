package com.zerodsoft.scheduleweather.event.weather.view.airconditionbar;

public class BarInitData
{
    //색상, 기준, 상태명(보통,좋음,나쁨)
    private int color;
    private double referenceValueBegin;
    private double referenceValueEnd;
    private String statusName;

    public BarInitData(int color, double referenceValueBegin,double referenceValueEnd, String statusName)
    {
        this.color = color;
        this.referenceValueBegin = referenceValueBegin;
        this.referenceValueEnd = referenceValueEnd;
        this.statusName = statusName;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public void setReferenceValueBegin(double referenceValueBegin)
    {
        this.referenceValueBegin = referenceValueBegin;
    }

    public void setReferenceValueEnd(double referenceValueEnd)
    {
        this.referenceValueEnd = referenceValueEnd;
    }

    public void setStatusName(String statusName)
    {
        this.statusName = statusName;
    }

    public int getColor()
    {
        return color;
    }

    public double getReferenceValueBegin()
    {
        return referenceValueBegin;
    }

    public double getReferenceValueEnd()
    {
        return referenceValueEnd;
    }

    public String getStatusName()
    {
        return statusName;
    }
}
