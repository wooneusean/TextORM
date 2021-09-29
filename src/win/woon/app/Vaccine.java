package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Model;
import win.woon.textorm.Repository;

@Repository
public class Vaccine extends Model<Vaccine> {

    @Column
    public String vaccineName;

    @Column
    public int daysBetweenDoses;

    @Column
    public double cost;

    @Column
    public boolean isFinished = false;

    public Vaccine(String vaccineName, int daysBetweenDoses, double cost, boolean isFinished) {
        this.cost = cost;
        this.vaccineName = vaccineName;
        this.daysBetweenDoses = daysBetweenDoses;
        this.isFinished = isFinished;
    }

    public Vaccine() {
    }
}
