package io.github.euseanwoon2016.app;

import io.github.euseanwoon2016.textorm.Column;
import io.github.euseanwoon2016.textorm.Repository;

import java.time.LocalDate;

@Repository
public class Account extends Person {
    @Column
    private String username;

    @Column
    private String password;

    @Column
    private int vaccineCenterId;

    public Account(String name, int age, double balance, LocalDate birthDate, PersonRace race, String username, String password, int vaccineCenterId) {
        super(name, age, balance, birthDate, race);
        this.username = username;
        this.password = password;
        this.vaccineCenterId = vaccineCenterId;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
