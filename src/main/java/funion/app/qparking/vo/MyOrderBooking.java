package funion.app.qparking.vo;

/**
 * 我的订单-预约
 * Created by yunze on 2015/12/19.
 */
public class MyOrderBooking {
    private String cost;//金额
    private String reserve_time;//预约时间
    private String status;//预约状态  0:未付 1:已预定 2:已结 4:超时 5:取消'
    private String plateNum;//车牌
    private String parkName;//停车场名

    public String getCost() {
        return this.cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getReserve_time() {
        return this.reserve_time;
    }

    public void setReserve_time(String reserve_time) {
        this.reserve_time = reserve_time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlateNum() {
        return this.plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getParkName() {
        return this.parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
}
