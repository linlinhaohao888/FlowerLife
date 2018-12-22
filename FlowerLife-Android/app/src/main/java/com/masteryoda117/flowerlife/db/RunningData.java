package com.masteryoda117.flowerlife.db;

import org.litepal.crud.DataSupport;

public class RunningData extends DataSupport {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private int id;
    private double distance;
    private int time;
}
