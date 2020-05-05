package com.zerodsoft.tripweather;

public class ProcessingType
{
    private int action;
    private int processingType;

    public int getAction()
    {
        return action;
    }

    public ProcessingType setAction(int action)
    {
        this.action = action;
        return this;
    }

    public int getProcessingType()
    {
        return processingType;
    }

    public ProcessingType setProcessingType(int processingType)
    {
        this.processingType = processingType;
        return this;
    }
}
