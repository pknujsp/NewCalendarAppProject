package com.zerodsoft.scheduleweather.kakaomap.building.model;

import java.util.List;

public class BuildingFloorData
{
    private List<CompanyData> companyDataList;
    private FacilityData facilityData;

    public BuildingFloorData(List<CompanyData> companyDataList, FacilityData facilityData)
    {
        this.companyDataList = companyDataList;
        this.facilityData = facilityData;
    }

    public List<CompanyData> getCompanyDataList()
    {
        return companyDataList;
    }

    public void setCompanyDataList(List<CompanyData> companyDataList)
    {
        this.companyDataList = companyDataList;
    }

    public FacilityData getFacilityData()
    {
        return facilityData;
    }

    public void setFacilityData(FacilityData facilityData)
    {
        this.facilityData = facilityData;
    }
}
