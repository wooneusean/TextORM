package io.github.euseanwoon2016.app;

import io.github.euseanwoon2016.textorm.Column;
import io.github.euseanwoon2016.textorm.ForeignKey;
import io.github.euseanwoon2016.textorm.Model;
import io.github.euseanwoon2016.textorm.Repository;

@Repository
public class VaccineCenter extends Model {
    @Column
    private String name;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private int vaccineId;

    @ForeignKey(foreignKey = "vaccineId")
    private Vaccine vaccine;

    public VaccineCenter(String name, Double longitude, Double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public VaccineCenter() {
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
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
