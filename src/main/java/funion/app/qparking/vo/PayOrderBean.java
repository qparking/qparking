package funion.app.qparking.vo;

/**
 * Created by 运泽 on 2016/1/11.
 */
public class PayOrderBean {
    private String id;
    private String park_order;//订单编号
    private String reserveId;
    private String plateNum;
    private String duration;
    private String price;
    private String yingPay;
    private String pay;
    private String youhui;
    private String name;
    private String rule_name;
    private String rule_content;
    private String couponId;
    private String inTime;
    private String outTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPark_order() {
        return park_order;
    }

    public void setPark_order(String park_order) {
        this.park_order = park_order;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getYouhui() {
        return youhui;
    }

    public void setYouhui(String youhui) {
        this.youhui = youhui;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getYingPay() {
        return yingPay;
    }

    public void setYingPay(String yingPay) {
        this.yingPay = yingPay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public String getRule_content() {
        return rule_content;
    }

    public void setRule_content(String rule_content) {
        this.rule_content = rule_content;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getOutTime() {
        return outTime;
    }

    @Override
    public String toString() {
        return "PayOrderBean{" +
                "id='" + id + '\'' +
                ", park_order='" + park_order + '\'' +
                ", reserveId='" + reserveId + '\'' +
                ", plateNum='" + plateNum + '\'' +
                ", duration='" + duration + '\'' +
                ", price='" + price + '\'' +
                ", yingPay='" + yingPay + '\'' +
                ", pay='" + pay + '\'' +
                ", youhui='" + youhui + '\'' +
                ", name='" + name + '\'' +
                ", rule_name='" + rule_name + '\'' +
                ", rule_content='" + rule_content + '\'' +
                ", couponId='" + couponId + '\'' +
                ", inTime='" + inTime + '\'' +
                ", outTime='" + outTime + '\'' +
                '}';
    }
}
