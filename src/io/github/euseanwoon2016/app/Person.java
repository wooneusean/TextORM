package io.github.euseanwoon2016.app;

import io.github.euseanwoon2016.textorm.Column;
import io.github.euseanwoon2016.textorm.Model;
import io.github.euseanwoon2016.textorm.Repository;

import java.time.LocalDate;

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

    @Column
    private PersonRace race;

    public Person(String name, int age, double balance, LocalDate birthDate, PersonRace race) {
        this.name = name;
        this.age = age;
        this.balance = balance;
        this.birthDate = birthDate;
        this.race = race;
    }

    public Person() {
    }

    public PersonRace getRace() {
        return race;
    }

    public void setRace(PersonRace race) {
        this.race = race;
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
