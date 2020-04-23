package com.zerodsoft.tripweather.ScheduleData;

import java.util.ArrayList;

public class TravelSchedule {
    private String travelName;
    private String travelStartDate;
    private String travelEndDate;
    private ArrayList<String> travelDestinations;

    public String getTravelName() {
        return travelName;
    }

    public TravelSchedule setTravelName(String travelName) {
        this.travelName = travelName;
        return this;
    }

    public String getTravelStartDate() {
        return travelStartDate;
    }

    public TravelSchedule setTravelStartDate(String travelStartDate) {
        this.travelStartDate = travelStartDate;
        return this;
    }

    public String getTravelEndDate() {
        return travelEndDate;
    }

    public TravelSchedule setTravelEndDate(String travelEndDate) {
        this.travelEndDate = travelEndDate;
        return this;
    }

    public ArrayList<String> getTravelDestinations() {
        return travelDestinations;
    }

    public TravelSchedule setTravelDestinations(ArrayList<String> travelDestinations) {
        this.travelDestinations = travelDestinations;
        return this;
    }
}
