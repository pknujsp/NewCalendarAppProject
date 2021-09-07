package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedback
{
    @SerializedName("allphotocnt")
    @Expose
    private String allPhotoCnt;

    @SerializedName("blogrvwcnt")
    @Expose
    private String blogRvwcnt;

    @SerializedName("kascnt")
    @Expose
    private String kasCnt;

    @SerializedName("comntcnt")
    @Expose
    private String comntCnt;

    @SerializedName("scorecnt")
    @Expose
    private String scoreCnt;

    @SerializedName("scoresum")
    @Expose
    private String scoreSum;

    private String scoreAvg;

    public Feedback()
    {
    }

    public String getAllPhotoCnt()
    {
        return allPhotoCnt;
    }

    public void setAllPhotoCnt(String allPhotoCnt)
    {
        this.allPhotoCnt = allPhotoCnt;
    }

    public String getBlogRvwcnt()
    {
        return blogRvwcnt;
    }

    public void setBlogRvwcnt(String blogRvwcnt)
    {
        this.blogRvwcnt = blogRvwcnt;
    }

    public String getKasCnt()
    {
        return kasCnt;
    }

    public void setKasCnt(String kasCnt)
    {
        this.kasCnt = kasCnt;
    }

    public String getComntCnt()
    {
        return comntCnt;
    }

    public void setComntCnt(String comntCnt)
    {
        this.comntCnt = comntCnt;
    }

    public String getScoreCnt()
    {
        return scoreCnt;
    }

    public void setScoreCnt(String scoreCnt)
    {
        this.scoreCnt = scoreCnt;
    }

    public String getScoreSum()
    {
        return scoreSum;
    }

    public void setScoreSum(String scoreSum)
    {
        this.scoreSum = scoreSum;
    }

    public String getScoreAvg()
    {
        return scoreAvg;
    }

    public void setScoreAvg()
    {
        if (scoreCnt != null && scoreSum != null)
        {
            double sum = Double.parseDouble(scoreSum);
            double cnt = Double.parseDouble(scoreCnt);
            if (sum > 0 && cnt > 0)
            {
                scoreAvg = String.format("%.1f", sum / cnt);
            } else
            {
                scoreAvg = "별점없음";
            }
        } else
        {
            scoreAvg = "별점없음";
        }
    }
}
