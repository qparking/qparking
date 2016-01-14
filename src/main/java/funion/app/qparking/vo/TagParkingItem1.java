package funion.app.qparking.vo;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

public class TagParkingItem1 implements Serializable {
    // 停车场id
    public String m_strPid;
    // 停车场名
    public String m_strName;
    // 停车场位置（经纬坐标）
    public LatLng m_llParking;
    // 停车场地址信息
    public String m_strAddress;
    private String phone;
    private String parking_img;

    public void setParking_img(String parking_img) {
        this.parking_img = parking_img;
    }

    public String getParking_img() {
        return parking_img;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LatLng getM_llParking() {
        return m_llParking;
    }

    public void setM_llParking(LatLng m_llParking) {
        this.m_llParking = m_llParking;
    }

    public String getM_strPid() {
        return m_strPid;
    }

    public void setM_strPid(String m_strPid) {
        this.m_strPid = m_strPid;
    }

    public String getM_strName() {
        return m_strName;
    }

    public void setM_strName(String m_strName) {
        this.m_strName = m_strName;
    }

    public String getM_strAddress() {
        return m_strAddress;
    }

    public void setM_strAddress(String m_strAddress) {
        this.m_strAddress = m_strAddress;
    }

    public int getM_iFreeNum() {
        return m_iFreeNum;
    }

    public void setM_iFreeNum(int m_iFreeNum) {
        this.m_iFreeNum = m_iFreeNum;
    }

    public int getM_iChargeNum() {
        return m_iChargeNum;
    }

    public void setM_iChargeNum(int m_iChargeNum) {
        this.m_iChargeNum = m_iChargeNum;
    }

    public int getM_iPraiseNum() {
        return m_iPraiseNum;
    }

    public void setM_iPraiseNum(int m_iPraiseNum) {
        this.m_iPraiseNum = m_iPraiseNum;
    }

    public int getM_iDespiseNum() {
        return m_iDespiseNum;
    }

    public void setM_iDespiseNum(int m_iDespiseNum) {
        this.m_iDespiseNum = m_iDespiseNum;
    }

    public String getM_strShareName() {
        return m_strShareName;
    }

    public void setM_strShareName(String m_strShareName) {
        this.m_strShareName = m_strShareName;
    }

    public int getM_iLocationType() {
        return m_iLocationType;
    }

    public void setM_iLocationType(int m_iLocationType) {
        this.m_iLocationType = m_iLocationType;
    }

    public int getM_iDistance() {
        return m_iDistance;
    }

    public void setM_iDistance(int m_iDistance) {
        this.m_iDistance = m_iDistance;
    }

    // 免费车位数
    public int m_iFreeNum;
    // 收费车位数
    public int m_iChargeNum;
    // 停车场好评数
    public int m_iPraiseNum;
    // 停车场差评数
    public int m_iDespiseNum;
    // 停车场分享者名
    public String m_strShareName;
    // 停车场位置类型0代表地上，1代表地下，2代表室内
    public int m_iLocationType;
    // 我与停车场的距离
    public int m_iDistance;

}
