package funion.app.qparking.vo;

/**
 * 我的订单-充值
 * Created by yunze on 2015/12/19.
 */
public class MyOrderRecharge {

    private String amount;
    private String addtime;
    private String status;
    private String id;
    private String channel;
    private String orderSn;

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddtime() {
        return this.addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrderSn() {
        return this.orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

}
