package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Model;
import win.woon.textorm.Repository;

@Repository
public class VaccineCenter extends Model<VaccineCenter> {
    @Column
    private String name;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    public VaccineCenter(String name, Double longitude, Double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public VaccineCenter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
