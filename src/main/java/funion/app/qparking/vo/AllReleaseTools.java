package funion.app.qparking.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 运泽 on 2016/1/5.
 */
public class AllReleaseTools implements Parcelable{
    private String user_id;//用户Id
    private String name;//停车位名字
    private String dtype;//排序 1价格 2距离
    private String longitude;//经度
    private String latitude;//纬度
    private String village;//小区名字
    private String villageid;//小区Id
    private String address;//地址
    private String charge_number;//出租价格
    private String add_time;//添加时间
    private String status;//状态
    private String ground;//位置，地上/地下
    private String duration_start;//开始时间
    private String duration_end;//结束时间
    private String avatar;//网址
    private String linkman;//联系人
    private String contactway;//联系电话
    private String parking_num;//车位编号

    public static final Parcelable.Creator<AllReleaseTools> CREATOR=new Creator<AllReleaseTools>() {
        @Override
        public AllReleaseTools createFromParcel(Parcel parcel) {
            AllReleaseTools allReleaseTools=new AllReleaseTools();
            allReleaseTools.setUser_id(parcel.readString());
            allReleaseTools.setName(parcel.readString());
            allReleaseTools.setDtype(parcel.readString());
            allReleaseTools.setLongitude(parcel.readString());
            allReleaseTools.setLatitude(parcel.readString());
            allReleaseTools.setVillage(parcel.readString());
            allReleaseTools.setVillageid(parcel.readString());
            allReleaseTools.setAddress(parcel.readString());
            allReleaseTools.setCharge_number(parcel.readString());
            allReleaseTools.setAdd_time(parcel.readString());
            allReleaseTools.setStatus(parcel.readString());
            allReleaseTools.setGround(parcel.readString());
            allReleaseTools.setDuration_start(parcel.readString());
            allReleaseTools.setDuration_end(parcel.readString());
            allReleaseTools.setAvatar(parcel.readString());
            allReleaseTools.setLinkman(parcel.readString());
            allReleaseTools.setContactway(parcel.readString());
            allReleaseTools.setParking_num(parcel.readString());
            return allReleaseTools;
        }

        @Override
        public AllReleaseTools[] newArray(int i) {
            return new AllReleaseTools[i];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCharge_number() {
        return charge_number;
    }

    public void setCharge_number(String charge_number) {
        this.charge_number = charge_number;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGround() {
        return ground;
    }

    public void setGround(String ground) {
        this.ground = ground;
    }

    public String getDuration_start() {
        return duration_start;
    }

    public void setDuration_start(String duration_start) {
        this.duration_start = duration_start;
    }

    public String getDuration_end() {
        return duration_end;
    }

    public void setDuration_end(String duration_end) {
        this.duration_end = duration_end;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getContactway() {
        return contactway;
    }

    public void setContactway(String contactway) {
        this.contactway = contactway;
    }

    public String getParking_num() {
        return parking_num;
    }

    public void setParking_num(String parking_num) {
        this.parking_num = parking_num;
    }

    @Override
    public String toString() {
        return "AllReleaseTools{" +
                "user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", dtype='" + dtype + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", village='" + village + '\'' +
                ", villageid='" + villageid + '\'' +
                ", address='" + address + '\'' +
                ", charge_number='" + charge_number + '\'' +
                ", add_time='" + add_time + '\'' +
                ", status='" + status + '\'' +
                ", ground='" + ground + '\'' +
                ", duration_start='" + duration_start + '\'' +
                ", duration_end='" + duration_end + '\'' +
                ", avatar='" + avatar + '\'' +
                ", linkman='" + linkman + '\'' +
                ", contactway='" + contactway + '\'' +
                ", parking_num='" + parking_num + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(name);
        parcel.writeString(dtype);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeString(village);
        parcel.writeString(villageid);
        parcel.writeString(address);
        parcel.writeString(charge_number);
        parcel.writeString(add_time);
        parcel.writeString(status);
        parcel.writeString(ground);
        parcel.writeString(duration_start);
        parcel.writeString(duration_end);
        parcel.writeString(avatar);
        parcel.writeString(linkman);
        parcel.writeString(contactway);
        parcel.writeString(parking_num);
    }
}
