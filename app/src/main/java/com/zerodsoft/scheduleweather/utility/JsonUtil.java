package com.zerodsoft.scheduleweather.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtil
{
    public static String getString(JsonObject jsonObject, String key)
    {
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }

    public static JsonArray getArray(JsonObject jsonObject, String key)
    {
        return jsonObject.has(key) ? jsonObject.getAsJsonArray(key) : null;
    }
}
