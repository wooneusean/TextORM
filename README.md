# TextORM

Do you have a Java assignment where your lecturer prevents you from using a modern database to handle your data?

Are you FORCED to use crude .txt files as your data storage method?

TextORM is an Object–relational mapping framework that works with .txt files!

No longer would you have to wrestle with file opening and line splitting and re-writing whole files just to get your
data saved and read.

Querying data is as easy as

```java
Vaccine sinovac = TextORM.getOne(Vaccine.class, dataMap -> Objects.equals(dataMap.get("vaccineName"), "Sinovac"));
```

And saving data is as easy as

```java
new Vaccine("Sinovac", 21, 120.00, false).save();
```

All-in-one example

```java
Vaccine sinovac = new Vaccine("Sinovac", 21, 120.00, false);
sinovac.setdaysBetweenDoses(14);
sinovac.save();

List<Vaccine> finishedVaccines = TextORM.getAll(Vaccine.class, dataMap -> Boolean.parseBoolean(dataMap.get("isFinished")));
for(Vaccine vaccine : finishedVaccines) {
    vaccine.delete();
}
```

## Creating Models

Here is an example model

```java
@Repository
public class Vaccine extends Model {

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
    
    /* getters and setters... */
}
```

## Model Inheritance

Inheritance works as you'd expect. View example below.

in `Person.java`

```java

@Repository
public class Person extends Model {
    @Column
    private String name;

    @Column
    private int age;

    @Column
    private double balance;

    @Column
    private LocalDate birthDate;

    public Person(String name, int age, double balance, LocalDate birthDate) {
        this.name = name;
        this.age = age;
        this.balance = balance;
        this.birthDate = birthDate;
    }

    public Person() {
    }

    /* setters and getters... */
}
```

in `Account.java`

```java

@Repository
public class Account extends Person {
    @Column
    private String username;

    @Column
    private String password;

    public Account(String name, int age, double balance, LocalDate birthDate, String username, String password) {
        super(name, age, balance, birthDate);
        this.username = username;
        this.password = password;
    }

    public Account() {
    }

    /* getters and setters... */
}
```

## Eager Loading for Related Models

TextORM also supports eager loading of related models. See below.

in `VaccineCenter.java`

```java

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

    /* getters and setters... */
}
```

in `Main.java`
```java
VaccineCenter movenpick = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Movenpick"));
if (movenpick != null) {
    movenpick.include(Vaccine.class);
    System.out.printf("The vaccine at %s is %s and costs RM %,.2f. Is finished: %b", movenpick.getName(), movenpick.getVaccine().getVaccineName(), movenpick.getVaccine().getCost(), movenpick.getVaccine().isFinished());
    movenpick.getVaccine().setCost(Math.round(100.0 * (500.0 * Math.random())) / 100.0);
    
    // Saving the parent model also saves the included models.
    movenpick.save();
}
```

Still planning to add one-to-many relationships.