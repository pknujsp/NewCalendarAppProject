package com.zerodsoft.tripweather;


import android.content.Context;
import android.content.res.Resources;

public class ConditionCode {
    private String condition;
    Context context;

    String getCondition(String code, Context contxt) {
        context = contxt;
        switch (code) {
            case "200":
                condition = context.getString(R.string.code_200);
                break;
            case "201":
                condition = context.getString(R.string.code_201);
                break;
            case "202":
                condition = context.getString(R.string.code_202);
                break;
            case "210":
                condition = context.getString(R.string.code_210);
                break;
            case "211":
                condition = context.getString(R.string.code_211);
                break;
            case "212":
                condition = context.getString(R.string.code_212);
                break;
            case "221":
                condition = context.getString(R.string.code_221);
                break;
            case "230":
                condition = context.getString(R.string.code_230);
                break;
            case "231":
                condition = context.getString(R.string.code_231);
                break;
            case "232":
                condition = context.getString(R.string.code_232);
                break;
            case "300":
                condition = context.getString(R.string.code_300);
                break;
            case "301":
                condition = context.getString(R.string.code_301);
                break;
            case "302":
                condition = context.getString(R.string.code_302);
                break;
            case "310":
                condition = context.getString(R.string.code_310);
                break;
            case "311":
                condition = context.getString(R.string.code_311);
                break;
            case "312":
                condition = context.getString(R.string.code_312);
                break;
            case "313":
                condition = context.getString(R.string.code_313);
                break;
            case "314":
                condition = context.getString(R.string.code_314);
                break;
            case "321":
                condition = context.getString(R.string.code_321);
                break;
            case "500":
                condition = context.getString(R.string.code_500);
                break;
            case "501":
                condition = context.getString(R.string.code_501);
                break;
            case "502":
                condition = context.getString(R.string.code_502);
                break;
            case "503":
                condition = context.getString(R.string.code_503);
                break;
            case "504":
                condition = context.getString(R.string.code_504);
                break;
            case "511":
                condition = context.getString(R.string.code_511);
                break;
            case "520":
                condition = context.getString(R.string.code_520);
                break;
            case "521":
                condition = context.getString(R.string.code_521);
                break;
            case "522":
                condition = context.getString(R.string.code_522);
                break;
            case "531":
                condition = context.getString(R.string.code_531);
                break;
            case "600":
                condition = context.getString(R.string.code_600);
                break;
            case "601":
                condition = context.getString(R.string.code_601);
                break;
            case "602":
                condition = context.getString(R.string.code_602);
                break;
            case "611":
                condition = context.getString(R.string.code_611);
                break;
            case "612":
                condition = context.getString(R.string.code_612);
                break;
            case "615":
                condition = context.getString(R.string.code_615);
                break;
            case "616":
                condition = context.getString(R.string.code_616);
                break;
            case "620":
                condition = context.getString(R.string.code_620);
                break;
            case "621":
                condition = context.getString(R.string.code_621);
                break;
            case "622":
                condition = context.getString(R.string.code_622);
                break;
            case "701":
                condition = context.getString(R.string.code_701);
                break;
            case "711":
                condition = context.getString(R.string.code_711);
                break;
            case "721":
                condition = context.getString(R.string.code_721);
                break;
            case "731":
                condition = context.getString(R.string.code_731);
                break;
            case "741":
                condition = context.getString(R.string.code_741);
                break;
            case "751":
                condition = context.getString(R.string.code_751);
                break;
            case "761":
                condition = context.getString(R.string.code_761);
                break;
            case "762":
                condition = context.getString(R.string.code_762);
                break;
            case "771":
                condition = context.getString(R.string.code_771);
                break;
            case "781":
                condition = context.getString(R.string.code_781);
                break;
            case "800":
                condition = context.getString(R.string.code_800);
                break;
            case "801":
                condition = context.getString(R.string.code_801);
                break;
            case "802":
                condition = context.getString(R.string.code_802);
                break;
            case "803":
                condition = context.getString(R.string.code_803);
                break;
            case "804":
                condition = context.getString(R.string.code_804);
                break;
        }
        return condition;
    }
}
