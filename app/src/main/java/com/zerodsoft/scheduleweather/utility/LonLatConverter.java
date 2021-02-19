package com.zerodsoft.scheduleweather.utility;

public class LonLatConverter
{
    private static final double RE = 6371.00877;          /* 사용할 지구반경 [ km ]      */
    private static final double GRID = 5.0;        /* 격자간격        [ km ]      */
    private static final double SLAT1 = 30.0;       /* 표준위도        [degree]    */
    private static final double SLAT2 = 60.0;       /* 표준위도        [degree]    */
    private static final double OLON = 126.0;        /* 기준점의 경도   [degree]    */
    private static final double OLAT = 38.0;        /* 기준점의 위도   [degree]    */
    private static final double X0 = 42.0;          /* 기준점의 X좌표  [격자거리]  */
    private static final double Y0 = 135.0;          /* 기준점의 Y좌표  [격자거리]  */

    private LonLatConverter()
    {

    }

    public static LonLat lonLatToGridXY(double lon, double lat)
    {
        double degrad = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * degrad;
        double slat2 = SLAT2 * degrad;
        double olon = OLON * degrad;
        double olat = OLAT * degrad;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + (lat * degrad * 0.5));
        ra = re * sf / Math.pow(ra, sn);

        double theta = lon * degrad - olon;

        if (theta > Math.PI)
        {
            theta -= 2.0 * Math.PI;
        }
        if (theta < -Math.PI)
        {
            theta += 2.0 * Math.PI;
        }
        theta *= sn;

        LonLat lonLat = new LonLat();
        lonLat.setX((int) ((ra * Math.sin(theta)) + X0 + 1.5));
        lonLat.setY((int) ((ro - ra * Math.cos(theta)) + Y0 + 1.5));

        return lonLat;
    }

    public static LonLat convertGrid(double longitude, double latitude)
    {
        LonLat lonLat = new LonLat();

        final int LAT_DEGREE = (int) latitude;
        final int LON_DEGREE = (int) longitude;

        final double lat_minutes = (latitude - (double) LAT_DEGREE) * 60;
        final double lon_minutes = (longitude - (double) LON_DEGREE) * 60;

        final int LAT_MINUTES = (int) lat_minutes;
        final int LON_MINUTES = (int) lon_minutes;

        final double LAT_SECONDS = (lat_minutes - (double) LAT_MINUTES) * 60;
        final double LON_SECONDS = (lon_minutes - (double) LON_MINUTES) * 60;

        //도 변환
        lonLat.setLatitude_degree(LAT_DEGREE);
        lonLat.setLongitude_degree(LON_DEGREE);
        //분 변환
        lonLat.setLatitude_minutes(LAT_MINUTES);
        lonLat.setLongitude_minutes(LON_MINUTES);
        //초 변환
        lonLat.setLatitude_seconds(LAT_SECONDS);
        lonLat.setLongitude_seconds(LON_SECONDS);

        lonLat.setLatitude(latitude);
        lonLat.setLongitude(longitude);

        return lonLat;
    }
}
