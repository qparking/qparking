package funion.app.qparking.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LeftMenuIconBean implements Parcelable{
    private String itemName;
    private String title;
    private String fileName;
    private String imgurl;
    private String color;

    public static final Parcelable.Creator<LeftMenuIconBean> CREATOR=new Parcelable.Creator<LeftMenuIconBean>() {
        @Override
        public LeftMenuIconBean createFromParcel(Parcel parcel) {
            LeftMenuIconBean leftMenuIconBean=new LeftMenuIconBean();
            leftMenuIconBean.setItemName(parcel.readString());
            leftMenuIconBean.setTitle(parcel.readString());
            leftMenuIconBean.setFileName(parcel.readString());
            leftMenuIconBean.setImgurl(parcel.readString());
            leftMenuIconBean.setColor(parcel.readString());
            return leftMenuIconBean;
        }

        @Override
        public LeftMenuIconBean[] newArray(int i) {
            return new LeftMenuIconBean[i];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "LeftMenuIconBean{" +
                "itemName='" + itemName + '\'' +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemName);
        parcel.writeString(title);
        parcel.writeString(fileName);
        parcel.writeString(imgurl);
        parcel.writeString(color);

    }
}
