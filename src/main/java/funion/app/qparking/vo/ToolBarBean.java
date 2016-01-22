package funion.app.qparking.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/20.
 */
public class ToolBarBean implements Parcelable{
    private String itemName;
    private String title;
    private String fileName;
    private String imgUrl;
    private String color;

    public static final Parcelable.Creator<ToolBarBean> CREATOR=new Parcelable.Creator<ToolBarBean>(){
        @Override
        public ToolBarBean createFromParcel(Parcel parcel) {
            ToolBarBean toolBarBean=new ToolBarBean();
            toolBarBean.setItemName(parcel.readString());
            toolBarBean.setTitle(parcel.readString());
            toolBarBean.setFileName(parcel.readString());
            toolBarBean.setImgUrl(parcel.readString());
            toolBarBean.setColor(parcel.readString());
            return toolBarBean;
        }
        @Override
        public ToolBarBean[] newArray(int i) {
            return new ToolBarBean[i];
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ToolBarBean{" +
                "itemName='" + itemName + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", color='" + color + '\'' +
                ", fileName='" + fileName + '\'' +
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
        parcel.writeString(imgUrl);
        parcel.writeString(color);
    }
}
