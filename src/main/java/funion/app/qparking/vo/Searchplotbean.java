package funion.app.qparking.vo;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 运泽 on 2016/1/7.
 */
public class Searchplotbean {
    private String uid;
    private String address;
    private LatLng latLng;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "Searchplotbean{" +
                "uid='" + uid + '\'' +
                ", address='" + address + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
