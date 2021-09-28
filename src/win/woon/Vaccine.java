package win.woon;

@Repository
public class Vaccine extends Model<Vaccine> {

    @Column
    public String vaccineName;

    @Column
    public int daysBetweenDoses;

    @Column
    public boolean isFinished = false;

    public Vaccine(int id, String vaccineName, int daysBetweenDoses, boolean isFinished) {
        this.id = id;
        this.vaccineName = vaccineName;
        this.daysBetweenDoses = daysBetweenDoses;
        this.isFinished = isFinished;
    }

    public Vaccine() {
    }
}
