package funion.app.qparking.vo;

/**
 * Created by 运泽 on 2016/1/9.
 */
public class PlateNumBean {
    private String platenum;
    private String isDefault;//0为非默认，1为默认
    private String plateType;

    public String getPlatenum() {
        return platenum;
    }

    public void setPlatenum(String platenum) {
        this.platenum = platenum;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getPlateType() {
        return plateType;
    }

    public void setPlateType(String plateType) {
        this.plateType = plateType;
    }

    @Override
    public String toString() {
        return "PlateNumBean{" +
                "platenum='" + platenum + '\'' +
                ", isDefault='" + isDefault + '\'' +
                ", plateType='" + plateType + '\'' +
                '}';
    }
}
