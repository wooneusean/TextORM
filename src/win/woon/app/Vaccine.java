package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Model;
import win.woon.textorm.Repository;

@Repository
public class Vaccine extends Model<Vaccine> {

    @Column
    private String vaccineName;

    @Column
    private int daysBetweenDoses;

    @Column
    private double cost;

    @Column
    private boolean isFinished = false;

    public Vaccine(String vaccineName, int daysBetweenDoses, double cost, boolean isFinished) {
        this.cost = cost;
        this.vaccineName = vaccineName;
        this.daysBetweenDoses = daysBetweenDoses;
        this.isFinished = isFinished;
    }

    public Vaccine() {
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public int getDaysBetweenDoses() {
        return daysBetweenDoses;
    }

    public void setDaysBetweenDoses(int daysBetweenDoses) {
        this.daysBetweenDoses = daysBetweenDoses;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
