package com.zerodsoft.scheduleweather.event.weather.repository;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.CtprvnRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AirConditionDownloader extends JsonDownloader<AirConditionRoot>
{
    /*
    관측소 별 실시간 측정정보 조회
     */
    public void getMsrstnAcctoRltmMesureDnsty(MsrstnAcctoRltmMesureDnstyParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

        Call<MsrstnAcctoRltmMesureDnstyRoot> call = querys.getMsrstnAcctoRltmMesureDnsty(parameter.getMap());
        call.enqueue(new Callback<MsrstnAcctoRltmMesureDnstyRoot>()
        {
            @Override
            public void onResponse(Call<MsrstnAcctoRltmMesureDnstyRoot> call, Response<MsrstnAcctoRltmMesureDnstyRoot> response)
            {
                processResult(response);
            }

            @Override
            public void onFailure(Call<MsrstnAcctoRltmMesureDnstyRoot> call, Throwable t)
            {
                processResult(t);
            }
        });

    }

    /*
    시도 별 실시간 측정정보 조회
     */
    public void getCtprvnRltmMesureDnsty(CtprvnRltmMesureDnstyParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

        Call<CtprvnRltmMesureDnstyRoot> call = querys.getCtprvnRltmMesureDnsty(parameter.getMap());
        call.enqueue(new Callback<CtprvnRltmMesureDnstyRoot>()
        {
            @Override
            public void onResponse(Call<CtprvnRltmMesureDnstyRoot> call, Response<CtprvnRltmMesureDnstyRoot> response)
            {
                processResult(response);
            }

            @Override
            public void onFailure(Call<CtprvnRltmMesureDnstyRoot> call, Throwable t)
            {
                processResult(t);
            }
        });

    }
}
