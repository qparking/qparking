package funion.app.qparking;

/**
 * Created by 运泽 on 2015/12/30.
 * TODO数据库查询辅助类
 */
public class SelectInfo {
    private static  Integer pro_code,city_code,dis_code;
    private static String dis;

    public Integer getPro_code() {
        return pro_code;
    }

    public void setPro_code(Integer pro_code) {
        this.pro_code = pro_code;
    }

    public Integer getCity_code() {
        return city_code;
    }

    public void setCity_code(Integer city_code) {
        this.city_code = city_code;
    }

    public Integer getDis_code() {
        return dis_code;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public void setDis_code(Integer dis_code) {
        this.dis_code = dis_code;
    }

    @Override
    public String toString() {
        return "SelectInfo{" +
                "pro_code=" + pro_code +
                ", city_code=" + city_code +
                ", dis_code=" + dis_code +
                ", dis='" + dis + '\'' +
                '}';
    }
}
