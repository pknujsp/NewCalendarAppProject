package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment
{
    @Expose
    @SerializedName("allComntcnt")
    private String allComntcnt;

    @Expose
    @SerializedName("currentPage")
    private String currentPage;

    @Expose
    @SerializedName("daumComntcnt")
    private String daumComntcnt;

    @Expose
    @SerializedName("kamapComntcnt")
    private String kamapComntcnt;

    @Expose
    @SerializedName("kaplaceComntcnt")
    private String kaplaceComntcnt;

    @Expose
    @SerializedName("scorecnt")
    private String scorecnt;

    @Expose
    @SerializedName("scoresum")
    private String scoresum;

    @Expose
    @SerializedName("pageList")
    private List<String> pageList;

    @Expose
    @SerializedName("list")
    private List<CommentItem> commentItemList;

    private String scoreAvg;

    public Comment()
    {
    }

    public String getAllComntcnt()
    {
        return allComntcnt;
    }

    public void setAllComntcnt(String allComntcnt)
    {
        this.allComntcnt = allComntcnt;
    }

    public String getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(String currentPage)
    {
        this.currentPage = currentPage;
    }

    public String getDaumComntcnt()
    {
        return daumComntcnt;
    }

    public void setDaumComntcnt(String daumComntcnt)
    {
        this.daumComntcnt = daumComntcnt;
    }

    public String getKamapComntcnt()
    {
        return kamapComntcnt;
    }

    public void setKamapComntcnt(String kamapComntcnt)
    {
        this.kamapComntcnt = kamapComntcnt;
    }

    public String getKaplaceComntcnt()
    {
        return kaplaceComntcnt;
    }

    public void setKaplaceComntcnt(String kaplaceComntcnt)
    {
        this.kaplaceComntcnt = kaplaceComntcnt;
    }

    public String getScorecnt()
    {
        return scorecnt;
    }

    public void setScorecnt(String scorecnt)
    {
        this.scorecnt = scorecnt;
    }

    public String getScoresum()
    {
        return scoresum;
    }

    public void setScoresum(String scoresum)
    {
        this.scoresum = scoresum;
    }

    public List<String> getPageList()
    {
        return pageList;
    }

    public void setPageList(List<String> pageList)
    {
        this.pageList = pageList;
    }

    public List<CommentItem> getCommentDataList()
    {
        return commentItemList;
    }

    public void setCommentDataList(List<CommentItem> commentItemList)
    {
        this.commentItemList = commentItemList;
    }

    public String getScoreAvg()
    {
        return scoreAvg;
    }

    public void setScoreAvg()
    {
        if (scorecnt != null && scoresum != null)
        {
            double avg = Double.parseDouble(scoresum) / Double.parseDouble(scorecnt);
            scoreAvg = String.format("%.1f", avg);
        }
    }
}
