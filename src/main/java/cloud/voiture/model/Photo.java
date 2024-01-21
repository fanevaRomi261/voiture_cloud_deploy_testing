package cloud.voiture.model;

public class Photo {

    private String data;
    private String type;

    
    public Photo() {
    }

    public Photo(String data, String type) {
        this.data = data;
        this.type = type;
    }
    
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
}
