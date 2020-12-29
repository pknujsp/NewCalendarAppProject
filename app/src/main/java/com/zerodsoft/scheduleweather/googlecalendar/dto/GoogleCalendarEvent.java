package com.zerodsoft.scheduleweather.googlecalendar.dto;

public class GoogleCalendarEvent
{
    // 제목
    private String title;
    // 시작 날짜
    private long dtStart;
    // 종료 날짜
    private long dtEnd;
    // 접근 수준 (참석자만, 공개, 기본)
    private int accessLevel;
    // 하루종일 일정인지 여부
    private boolean allDay;
    // 일정 상태 : 바쁨, 자유, 가변적
    private int availability;
    // 이벤트가 저장될 캘린더의 ID
    private int calendarId;
    // 참석자 초대 가능여부
    private boolean canInviteOthers;
    // 설명(내용)
    private String description;
    // 이벤트 색상
    private int displayColor;
    // 반복
    private String duration;
    // A secondary color for the individual event. This should only be updated by the sync adapter for a given account.
    private int eventColor;
    private int eventColorKey;
    // 이벤트 종료 시간의 시간대
    private String eventEndTimeZone;
    // 위치
    private String eventLocation;
    // 이벤트의 시간대
    private String eventTimeZone;
    // 반복 예외
    private String exDate;
    // 반복 예외 규칙
    private String exRule;
}
