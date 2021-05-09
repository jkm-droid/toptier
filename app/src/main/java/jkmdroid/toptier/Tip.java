package jkmdroid.toptier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tip{
    private int id;
    private String teamA, teamB;
    private double home, draw, away;
    private String correct, score;
    private long matchTime, createdAt;
    private int vipStatus;
    private String winLose;


    public void setAway(double away) {
        this.away = away;
    }

    public void setCorrect(String c) {
        correct = c;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setCreatedAt(String createdAt) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(createdAt);
        long millis = date.getTime();
        this.createdAt = millis;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }

    public void setHome(double home) {
        this.home = home;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatchTime(String matchTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(matchTime);
        long millis = date.getTime();
        this.matchTime = millis;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getCorrect() {
        return correct;
    }

    public String getScore() {
        return score;
    }

    public double getAway() {
        return away;
    }

    public double getDraw() {
        return draw;
    }

    public double getHome() {
        return home;
    }

    public int getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getMatchTime() {
        return matchTime;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }

    public int getVipStatus() {
        return vipStatus;
    }

    public String getWinLose() {
        return winLose;
    }

    public void setWinLose(String winLose) {
        this.winLose = winLose;
    }

}