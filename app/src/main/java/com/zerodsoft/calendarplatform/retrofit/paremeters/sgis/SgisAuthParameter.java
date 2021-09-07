package com.zerodsoft.calendarplatform.retrofit.paremeters.sgis;

import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class SgisAuthParameter
{
    private String consumerKey = HttpCommunicationClient.SGIS_CONSUMER_KEY;
    private String consumerSecret = HttpCommunicationClient.SGIS_CONSUMER_SECRET;

    public SgisAuthParameter()
    {
    }


    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("consumer_key", consumerKey);
        map.put("consumer_secret", consumerSecret);

        return map;
    }
}
