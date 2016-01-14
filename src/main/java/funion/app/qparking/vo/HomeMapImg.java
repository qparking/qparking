package funion.app.qparking.vo;

import java.io.Serializable;

/**
 * 地图图片
 * Created by yunze on 2016/1/9.
 */
public class HomeMapImg implements Serializable {
    private ActionItem actionItem;
    private MapItem mapItem;
    private BarItem barItem;

    public void setActionItem(ActionItem actionItem) {
        this.actionItem = actionItem;
    }

    public void setBarItem(BarItem barItem) {
        this.barItem = barItem;
    }

    public void setMapItem(MapItem mapItem) {
        this.mapItem = mapItem;
    }

    public ActionItem getActionItem() {
        return actionItem;
    }

    public BarItem getBarItem() {
        return barItem;
    }

    public MapItem getMapItem() {
        return mapItem;
    }

    class ActionItem implements Serializable {
        private String nav;
        private String subscribe;

        public void setNav(String nav) {
            this.nav = nav;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }

        public String getNav() {
            return nav;
        }

        public String getSubscribe() {
            return subscribe;
        }
    }

    class MapItem implements Serializable {
        private String commonmarking;
        private String selmarking;
        private String currentmarking;

        public void setCommonmarking(String commonmarking) {
            this.commonmarking = commonmarking;
        }

        public void setCurrentmarking(String currentmarking) {
            this.currentmarking = currentmarking;
        }

        public void setSelmarking(String selmarking) {
            this.selmarking = selmarking;
        }

        public String getCommonmarking() {
            return commonmarking;
        }

        public String getCurrentmarking() {
            return currentmarking;
        }

        public String getSelmarking() {
            return selmarking;
        }
    }

    class BarItem implements Serializable {
        private String leftBarImg;
        private String rightBarImg;

        public void setLeftBarImg(String leftBarImg) {
            this.leftBarImg = leftBarImg;
        }

        public void setRightBarImg(String rightBarImg) {
            this.rightBarImg = rightBarImg;
        }

        public String getLeftBarImg() {
            return leftBarImg;
        }

        public String getRightBarImg() {
            return rightBarImg;
        }
    }
}
