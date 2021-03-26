package com.zerodsoft.scheduleweather.event.weather.repository;

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

public abstract class AirConditionDownloader
{
    public abstract void onResponse(DataWrapper<? extends AirConditionRoot> result);

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
                AirConditionDownloader.this.onResponse(new DataWrapper<MsrstnAcctoRltmMesureDnstyBody>(response.body().getResponse().getBody()));
            }

            @Override
            public void onFailure(Call<MsrstnAcctoRltmMesureDnstyRoot> call, Throwable t)
            {
                AirConditionDownloader.this.onResponse(new DataWrapper<MsrstnAcctoRltmMesureDnstyBody>(new Exception(t)));
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
        call.enqueue(new RetrofitCallback<CtprvnRltmMesureDnstyRoot>()
        {
            @Override
            protected void handleResponse(CtprvnRltmMesureDnstyRoot data)
            {
                AirConditionDownloader.this.onResponse(new DataWrapper<CtprvnRltmMesureDnstyBody>(data.getResponse().getBody()));
            }

            @Override
            protected void handleError(Response<CtprvnRltmMesureDnstyRoot> response)
            {
                AirConditionDownloader.this.onResponse(new DataWrapper<CtprvnRltmMesureDnstyBody>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                AirConditionDownloader.this.onResponse(new DataWrapper<CtprvnRltmMesureDnstyBody>(e));
            }
        });

    }
}
