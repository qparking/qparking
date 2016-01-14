package funion.app.qparking.vo;

import java.util.Date;

/**
 * Created by 运泽 on 2015/12/31.
 */
public class MyReleaseTools {
    private String name;//车位名字
    private String addTime;//添加时间
    private String carprot_num;//车位编号
    private String place;//1地上 0地下
    private String plot_name;
    private String status;//发布车位状态 0:下架;1上架;2待审核;3审核未通过;4禁用

    public String getPlot_name() {
        return plot_name;
    }

    public void setPlot_name(String plot_name) {
        this.plot_name = plot_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getCarprot_num() {
        return carprot_num;
    }

    public void setCarprot_num(String carprot_num) {
        this.carprot_num = carprot_num;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MyReleaseTools{" +
                "name='" + name + '\'' +
                ", addTime='" + addTime + '\'' +
                ", carprot_num='" + carprot_num + '\'' +
                ", place='" + place + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
