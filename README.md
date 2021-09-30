# TextORM
Do you have a Java assignment where your lecturer prevents you from using a modern database to handle your data?

Are you FORCED to use crude .txt files as your data storage method?

TextORM is an Object–relational mapping framework that works with .txt files!

No longer would you have to wrestle with file opening and line splitting and re-writing whole files just to get your data saved and read.

Querying data is as easy as

```java
Vaccine sinovac = TextORM.getOne(Vaccine.class, dataMap -> Objects.equals(dataMap.get("vaccineName"), "Sinovac"));
```

And saving data is as easy as

```java
new Vaccine("Sinovac", 21, false).save();
```

All-in-one example
```java
Vaccine sinovac = new Vaccine("Sinovac", 21, false).save();
sinovac.setdaysBetweenDoses(14);
sinovac.save();

Vaccine finishedVaccines = TextORM.getAll(Vaccine.class, dataMap -> Boolean.parseBoolean(dataMap.get("isFinished")));
for (Vaccine vaccine : finishedVaccines) {
    vaccine.delete();
}
```

## Creating models

Here is an example model

```java
@Repository
public class Vaccine extends Model<Vaccine> {

    @Column
    private String vaccineName;

    @Column
    private int daysBetweenDoses;

    @Column
    private boolean isFinished = false;

    public Vaccine(String vaccineName, int daysBetweenDoses, boolean isFinished) {
        this.vaccineName = vaccineName;
        this.daysBetweenDoses = daysBetweenDoses;
        this.isFinished = isFinished;
    }

    // This is REQUIRED, it is used under the hood.
    // I am an incompetent programmer.
    public Vaccine() {
    }
}
```
