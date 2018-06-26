package wmlove.istation.network.model;

public class GoodsImage {

    private String type;
    private String path;


    @Override
    public String toString() {
        return "GoodsImage [type=" + type + ", path=" + path + "]";
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }



}
