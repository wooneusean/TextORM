package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Model;
import win.woon.textorm.Repository;

@Repository
public class VaccineCenter extends Model<VaccineCenter> {
    @Column
    public String name;

    @Column
    public Double longitude;

    @Column
    public Double latitude;

    public VaccineCenter(String name, Double longitude, Double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public VaccineCenter() {
    }
}
