package funion.app.qparking.vo;

import java.io.Serializable;

/**
 * 左边菜单图片
 * Created by yunze on 2016/1/9.
 */
public class LeftMenuImg implements Serializable {
    private static final long serialVersionUID = 2090084806L;
    private String color;
    private String title;
    private String img;
    private String itemname;

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getItemname() {
        return this.itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }
}
