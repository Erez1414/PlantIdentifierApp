package com.final_project.plantidentifier.data;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "plant")
public class PlantEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String type;
    private String name;
    private String loc;
    private String desc;
    private String water;
    private String sun;
    private String soil;
    private int alarm;
    private boolean notification;
    private Bitmap img;
    private double[] imgLocation;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;


    @Ignore
    public PlantEntry(String type, String name, String loc, String desc, String water, String sun, String soil, int alarm ,boolean notification, Date updatedAt, Bitmap img, double[] imgLocation) {
        this.type = type;
        this.name = name;
        this.loc = loc;
        this.desc = desc;
        this.water = water;
        this.sun = sun;
        this.soil = soil;
        this.alarm = alarm;
        this.notification = notification;
        this.img = img;
        this.imgLocation = imgLocation;
        this.updatedAt = updatedAt;
    }

    public PlantEntry(int id, String type, String name, String loc, String desc, String water, String sun, String soil, int alarm, boolean notification, Date updatedAt, Bitmap img, double[] imgLocation) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.loc = loc;
        this.desc = desc;
        this.water = water;
        this.sun = sun;
        this.soil = soil;
        this.alarm = alarm;
        this.notification = notification;
        this.img = img;
        this.imgLocation = imgLocation;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String t) {
        this.type = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double[] getImgLocation() {
        return imgLocation;
    }

    public void setImgLocation(double[] imgLocation) {
        this.imgLocation = imgLocation;
    }
}
