package wmlove.istation.network.model;

import java.util.List;

public class Category {

    /**
     * category1 : 美妆
     * category2 : [{"categ2":"面部护理","images":[{"path":"image/thumb/683575538_0.png"},{"path":"image/thumb/686676215_0.png"}]},{"categ2":"彩妆美瞳","images":[{"path":"image/thumb/173216319_0.png"},{"path":"image/thumb/686693104_0.png"}]},{"categ2":"面膜","images":[{"path":"image/thumb/687150001_0.png"},{"path":"image/thumb/697162428_0.png"}]}]
     */

    private String category1;
    private List<Category2Bean> category2;

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public List<Category2Bean> getCategory2() {
        return category2;
    }

    public void setCategory2(List<Category2Bean> category2) {
        this.category2 = category2;
    }

    public static class Category2Bean {
        /**
         * categ2 : 面部护理
         * images : [{"path":"image/thumb/683575538_0.png"},{"path":"image/thumb/686676215_0.png"}]
         */

        private String categ2;
        private List<ImagesBean> images;

        public String getCateg2() {
            return categ2;
        }

        public void setCateg2(String categ2) {
            this.categ2 = categ2;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public static class ImagesBean {
            /**
             * path : image/thumb/683575538_0.png
             */

            private String path;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }
        }
    }
}
