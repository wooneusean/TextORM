package win.woon.app;

import win.woon.textorm.Column;
import win.woon.textorm.Repository;

@Repository
public class Account extends Person {
    @Column
    private String username;

    @Column
    private String password;

    public Account(String name, int age, double balance, String username, String password) {
        super(name, age, balance);
        this.username = username;
        this.password = password;
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
