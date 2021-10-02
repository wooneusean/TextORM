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
public class Main {
    public static void main(String[] args) {
        Vaccine sinovac = new Vaccine("Sinovac", 21, 120.00, false);
        sinovac.setdaysBetweenDoses(14);
        sinovac.save();

        List<Vaccine> finishedVaccines = TextORM.getAll(Vaccine.class, dataMap -> Boolean.parseBoolean(dataMap.get("isFinished")));
        for (Vaccine vaccine : finishedVaccines) {
            vaccine.delete();
        }
    }
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

    @Column
    private int vaccineCenterId;

    public Account(String name, int age, double balance, LocalDate birthDate, String username, String password) {
        super(name, age, balance, birthDate);
        this.username = username;
        this.password = password;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account() {
    }
    
    /* getters and setters... */
}
```

## Lazy Loading for Related Models

TextORM also supports lazy loading of related models. See below.

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

    @HasOne(foreignKey = "vaccineId")
    private Vaccine vaccine;

    @HasMany(targetKey = "vaccineCenterId")
    private Account[] assignedAccounts;

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
public class Main {
    public static void main(String[] args) {
        VaccineCenter movenpick = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Movenpick"));
        if (movenpick != null) {
            movenpick.include(Vaccine.class);
            System.out.printf("The vaccine at %s is %s and costs RM %,.2f. Is finished: %b", movenpick.getName(), movenpick.getVaccine().getVaccineName(), movenpick.getVaccine().getCost(), movenpick.getVaccine().isFinished());
            movenpick.getVaccine().setCost(Math.round(100.0 * (500.0 * Math.random())) / 100.0);

            // Saving the parent model also saves the included models.
            movenpick.save();
        }
    }
}
```

## Eager Loading for Related Models

To use eager loading in TextORM, simply list down the models you wish to include when using `getOne()` or `getAll()`. See below.

in `Main.java`
```java
public class Main {
    public static void main(String[] args) {
        VaccineCenter movenpick = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Movenpick"), Vaccine.class /* <- Here */);
        if (movenpick != null) {
            System.out.printf("The vaccine at %s is %s and costs RM %,.2f. Is finished: %b", movenpick.getName(), movenpick.getVaccine().getVaccineName(), movenpick.getVaccine().getCost(), movenpick.getVaccine().isFinished());
            movenpick.getVaccine().setCost(Math.round(100.0 * (500.0 * Math.random())) / 100.0);

            // Saving the parent model also saves the included models.
            movenpick.save();
        }
    }
}
```

## One-To-Many Relationship

To use one-to-many relationship in your models, A field of type Array must be annotated with `@HasMany(targetKey = "key")`
where `"key"` is the name of the foreign key in the included model which points to the including model. See below.

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

    @HasOne(foreignKey = "vaccineId")
    private Vaccine vaccine;

    // Keep in mind, this field MUST BE of type T[]
    // and not List<T> or ArrayList<T> or any other list.
    @HasMany(targetKey = "vaccineCenterId") /* <- Here */
    private Account[] assignedAccounts;

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

in `Account.java`

```java
@Repository
public class Account extends Person {
    @Column
    private String username;

    @Column
    private String password;

    // targetKey = "vaccineCenterId"
    // is referring to this field here.
    @Column
    private int vaccineCenterId;

    public Account(String name, int age, double balance, LocalDate birthDate, String username, String password) {
        super(name, age, balance, birthDate);
        this.username = username;
        this.password = password;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account() {
    }
    
    /* getters and setters... */
}
```

Then, including it is as usual, through eager or lazy loading. Both works. See below.

in `Main.java`

```java
public class Main {
    public static void main(String[] args) {
        VaccineCenter jalil = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Bukit Jalil Stadium"), Vaccine.class, Account.class);
        if (jalil != null) {
            System.out.printf("The vaccine at %s is %s and costs RM %,.2f. Is finished: %b%n", jalil.getName(), jalil.getVaccine().getVaccineName(), jalil.getVaccine().getCost(), jalil.getVaccine().isFinished());
            for (Account account : jalil.getAssignedAccounts()) {
                System.out.println(account.getName());
            }
            
            // Saving the parent model also saves all included models.
            jalil.save();
        }
    }
}
```