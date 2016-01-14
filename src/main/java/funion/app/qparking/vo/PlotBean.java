package funion.app.qparking.vo;

/**
 * Created by 运泽 on 2016/1/8.
 */
public class PlotBean {
    private String name;
    private String uid;
    private String carport;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCarport() {
        return carport;
    }

    public void setCarport(String carport) {
        this.carport = carport;
    }

    @Override
    public String toString() {
        return "PlotBean{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", carport='" + carport + '\'' +
                '}';
    }
}
