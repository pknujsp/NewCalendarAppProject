package com.zerodsoft.calendarplatform.navermap.building.model;

public class CompanyData
{
    private String companyName;
    private String companyTheme;

    public CompanyData(String companyName, String companyTheme)
    {
        this.companyName = companyName;
        this.companyTheme = companyTheme;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyTheme()
    {
        return companyTheme;
    }

    public void setCompanyTheme(String companyTheme)
    {
        this.companyTheme = companyTheme;
    }
}
