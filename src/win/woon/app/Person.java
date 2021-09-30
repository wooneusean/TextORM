package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Model;
import win.woon.textorm.Repository;

import java.time.LocalDate;

@Repository
public class Person extends Model<Person> {
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", balance=" + balance +
                ", birthDate=" + birthDate +
                '}';
    }
}
