package com.ziker.train.Utils.Info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CustomShuttleInfo implements Parcelable {

    /**
     * id : 1
     * name : 1路
     * map : /images/bus/bus_001.png
     * mileage : 20
     * ticket : 8
     * sites : ["巨龙大道","宋家岗","航空总部","天河机场","建设大道双墩","建设大道新合村","解放大道水厂","解放大道太平洋","解放大道仁寿路","解放大道宝丰路","解放大道同济医院","解放大道航空路","解放大道中山公园","友谊路中山大道口","中山大道地铁六渡桥站","中山大道地铁江汉路站","保华街黄石路","中山大道大智路","胜利街一元路","胜利街三阳路","胜利街六合路","胜利街张自忠路","芦沟桥路","汉口火车站","后襄河北路"]
     * time : [{"site":"巨龙大道","starttime":"06:23:00","endtime":"21:19:00"},{"site":"后襄河北路","starttime":"06:00:00","endtime":"23:12:00"}]
     */

    private int id;
    private String name;
    private String map;
    private String mileage;
    private int ticket;
    private List<String> sites;
    private List<TimeBean> time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public List<TimeBean> getTime() {
        return time;
    }

    public void setTime(List<TimeBean> time) {
        this.time = time;
    }

    public static final Parcelable.Creator<CustomShuttleInfo> CREATOR = new Parcelable.Creator<CustomShuttleInfo>() {

        @Override
        public CustomShuttleInfo createFromParcel(Parcel in) {
            CustomShuttleInfo face = new CustomShuttleInfo();
            face.id = in.readInt();
            face.name = in.readString();
            face.map = in.readString();
            face.mileage = in.readString();
            face.ticket = in.readInt();
            List<String> sites  = new ArrayList<>();
            in.readStringList(sites);
            face.sites = sites;
            List<TimeBean> parts = new ArrayList<>();
            in.readTypedList(parts, TimeBean.CREATOR);
            face.time = parts;
            return face;
        }

        @Override
        public CustomShuttleInfo[] newArray(int size) {
            return new CustomShuttleInfo[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getName());
        dest.writeString(getMap());
        dest.writeString(getMileage());
        dest.writeInt(getTicket());
        dest.writeStringList(getSites());
        dest.writeTypedList(getTime());
    }


    public static class TimeBean implements Parcelable{
        private String site;
        private String starttime;
        private String endtime;

        protected TimeBean(Parcel in) {
            site = in.readString();
            starttime = in.readString();
            endtime = in.readString();
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public static final Creator<TimeBean> CREATOR = new Creator<TimeBean>() {
            @Override
            public TimeBean createFromParcel(Parcel in) {
                return new TimeBean(in);
            }

            @Override
            public TimeBean[] newArray(int size) {
                return new TimeBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(getStarttime());
                dest.writeString(getEndtime());
                dest.writeString(getSite());
        }
    }


}
