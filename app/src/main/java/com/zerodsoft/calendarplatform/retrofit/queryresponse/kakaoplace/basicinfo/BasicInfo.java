package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.utility.JsonUtil;

public class BasicInfo
{
    @SerializedName("cid")
    @Expose
    private String cId;

    @SerializedName("placenamefull")
    @Expose
    private String placeNameFull;

    @SerializedName("mainphotourl")
    @Expose
    private String mainPhotoUrl;

    @SerializedName("phonenum")
    @Expose
    private String phoneNum;

    @SerializedName("homepage")
    @Expose
    private String homePage;

    @SerializedName("catename")
    @Expose
    private String cateName;

    @SerializedName("cate1name")
    @Expose
    private String cate1Name;

    @SerializedName("address")
    @Expose
    private Address address;

    @SerializedName("feedback")
    @Expose
    private Feedback feedback;

    @SerializedName("openHour")
    @Expose
    private OpenHour openHour;

    @SerializedName("source")
    @Expose
    private Source source;

    public BasicInfo()
    {
    }

    public static BasicInfo init(JsonObject jsonObject)
    {
        BasicInfo basicInfo = new BasicInfo();

        basicInfo.setcId(JsonUtil.getString(jsonObject, "cid"));
        basicInfo.setPlaceNameFull(JsonUtil.getString(jsonObject, "placenamefull"));
        basicInfo.setMainPhotoUrl(JsonUtil.getString(jsonObject, "mainphotourl"));
        basicInfo.setPhoneNum(JsonUtil.getString(jsonObject, "phonenum"));
        basicInfo.setCateName(JsonUtil.getString(jsonObject, "catename"));
        basicInfo.setCate1Name(JsonUtil.getString(jsonObject, "cate1name"));
        basicInfo.setHomePage(JsonUtil.getString(jsonObject, "homepage"));


        //address
        Address address = new Address();
        basicInfo.setAddress(address);

        address.setAddrBunho(JsonUtil.getString(jsonObject, "addrbunho"));
        address.setAddrDetail(JsonUtil.getString(jsonObject, "addrdetail"));

        NewAddress newAddress = new NewAddress();
        newAddress.setNewAddrFull(JsonUtil.getString(jsonObject.getAsJsonObject("address").getAsJsonObject("newaddr"), "newaddrfull"));
        newAddress.setBsizonno(JsonUtil.getString(jsonObject.getAsJsonObject("address").getAsJsonObject("newaddr"), "bsizonno"));

        address.setNewAddress(newAddress);

        Region region = new Region();
        region.setName3(JsonUtil.getString(jsonObject.getAsJsonObject("address").getAsJsonObject("region"), "name3"));
        region.setFullName(JsonUtil.getString(jsonObject.getAsJsonObject("address").getAsJsonObject("region"), "fullname"));
        region.setNewAddrFullName(JsonUtil.getString(jsonObject.getAsJsonObject("address").getAsJsonObject("region"), "newaddrfullname"));

        address.setRegion(region);

        //feedback
        Feedback feedback = new Feedback();
        basicInfo.setFeedback(feedback);

        feedback.setAllPhotoCnt(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "allphotocnt"));
        feedback.setBlogRvwcnt(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "blogrvwcnt"));
        feedback.setKasCnt(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "kascnt"));
        feedback.setComntCnt(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "comntcnt"));
        feedback.setScoreSum(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "scoresum"));
        feedback.setScoreCnt(JsonUtil.getString(jsonObject.getAsJsonObject("feedback"), "scorecnt"));

        //openHour
        OpenHour openHour = new OpenHour();
        basicInfo.setOpenHour(openHour);

        return basicInfo;
    }

    public String getcId()
    {
        return cId;
    }

    public void setcId(String cId)
    {
        this.cId = cId;
    }

    public String getPlaceNameFull()
    {
        return placeNameFull;
    }

    public void setPlaceNameFull(String placeNameFull)
    {
        this.placeNameFull = placeNameFull;
    }

    public String getMainPhotoUrl()
    {
        return mainPhotoUrl;
    }

    public void setMainPhotoUrl(String mainPhotoUrl)
    {
        this.mainPhotoUrl = mainPhotoUrl;
    }

    public String getPhoneNum()
    {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum)
    {
        this.phoneNum = phoneNum;
    }

    public String getHomePage()
    {
        return homePage;
    }

    public void setHomePage(String homePage)
    {
        this.homePage = homePage;
    }

    public String getCateName()
    {
        return cateName;
    }

    public void setCateName(String cateName)
    {
        this.cateName = cateName;
    }

    public String getCate1Name()
    {
        return cate1Name;
    }

    public void setCate1Name(String cate1Name)
    {
        this.cate1Name = cate1Name;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public Feedback getFeedback()
    {
        return feedback;
    }

    public void setFeedback(Feedback feedback)
    {
        this.feedback = feedback;
    }

    public OpenHour getOpenHour()
    {
        return openHour;
    }

    public void setOpenHour(OpenHour openHour)
    {
        this.openHour = openHour;
    }

    public Source getSource()
    {
        return source;
    }

    public void setSource(Source source)
    {
        this.source = source;
    }
}
