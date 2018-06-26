package wmlove.istation.entity;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.utils.Common;


public class StockEntity {

    // 涨幅榜
    public List<StockInfo> increase_list;

    public static class StockInfo {

        private int itemType;


        public String id;
        public String imgURL;
        public String name;
        public String price;
        public String salesNum;
        public String desc;

        public StockInfo(int itemType) {
            this.itemType = itemType;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public boolean check;

        public StockInfo(GoodsModel good) {
            this.imgURL = Common.API_IMAGE_DOMAIN_URL + good.getImages().get(0).getPath();
            this.desc = good.getDesc_phrase();
            this.id = good.getId();
            this.name = good.getName();
            this.price = good.getPrice();
            this.salesNum = good.getSales_num();
        }
    }

}
