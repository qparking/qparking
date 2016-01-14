package funion.app.qparking.vo;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 运泽 on 2015/12/30.
 */
public class SelectPlotId {
    private String name;
    private String uid;
    private String add;
    private String distance;
    private String freenum;

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFreenum() {
        return freenum;
    }

    public void setFreenum(String freenum) {
        this.freenum = freenum;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "SelectPlotId{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", add='" + add + '\'' +
                ", distance='" + distance + '\'' +
                ", freenum='" + freenum + '\'' +
                '}';
    }
}
