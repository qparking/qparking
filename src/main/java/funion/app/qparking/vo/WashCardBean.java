package funion.app.qparking.vo;

/**
 * Created by Administrator on 2016/1/19.
 */
public class WashCardBean {
    private String id;//编号
    private String couponId;//优惠券ID
    private String couponsRecord;//优惠券设置
    private String merchantId;//商家id
    private String couponName;//优惠券名称
    private String couponAmount;//优惠券金额
    private String userId;//用户编号
    private String userName;//用户名
    private String orderId;//订单编号
    private String addTime;//记录时间
    private String couponNumber;//优惠券号
    private String status;//优惠状态 0未使用，1已使用
    private String usedTime;//消费时间
    private String ctype;//优惠类型: 0按价格优惠 1按时间优惠
    private String useType; //使用类型 0非刷号 1刷号
    private String servType;//服务类型 0洗车券 1停车券
    private String platformTicket;//平台券
    private String qrCode;//二维码
    private String merchantName;//商家名称
    private String end_date;

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponsRecord() {
        return couponsRecord;
    }

    public void setCouponsRecord(String couponsRecord) {
        this.couponsRecord = couponsRecord;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getCouponNumber() {
        return couponNumber;
    }

    public void setCouponNumber(String couponNumber) {
        this.couponNumber = couponNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(String usedTime) {
        this.usedTime = usedTime;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public String getServType() {
        return servType;
    }

    public void setServType(String servType) {
        this.servType = servType;
    }

    public String getPlatformTicket() {
        return platformTicket;
    }

    public void setPlatformTicket(String platformTicket) {
        this.platformTicket = platformTicket;
    }

    @Override
    public String toString() {
        return "WashCardBean{" +
                "id='" + id + '\'' +
                ", couponId='" + couponId + '\'' +
                ", couponsRecord='" + couponsRecord + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", couponName='" + couponName + '\'' +
                ", couponAmount='" + couponAmount + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", addTime='" + addTime + '\'' +
                ", couponNumber='" + couponNumber + '\'' +
                ", status='" + status + '\'' +
                ", usedTime='" + usedTime + '\'' +
                ", ctype='" + ctype + '\'' +
                ", useType='" + useType + '\'' +
                ", servType='" + servType + '\'' +
                ", platformTicket='" + platformTicket + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", end_date='" + end_date + '\'' +
                '}';
    }
}
