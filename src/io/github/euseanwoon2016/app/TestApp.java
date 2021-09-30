package io.github.euseanwoon2016.app;

import io.github.euseanwoon2016.textorm.TextORM;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class TestApp {
    public static void main(String[] args) {
        TextORM.setStoragePath("storage");
        TextORM.setMetaStoragePath("storage");

        seedModels();

        List<Vaccine> vaccines = TextORM.getAll(Vaccine.class, dataMap -> Integer.parseInt(dataMap.get("daysBetweenDoses")) <= 14);

        if (vaccines != null) {
            for (Vaccine vaccine : vaccines) {
                System.out.printf("[%d] %s, %d day(s) between doses. Finished: %b%n", vaccine.getId(), vaccine.getVaccineName(), vaccine.getDaysBetweenDoses(), vaccine.isFinished());
            }
        }

        Vaccine foundVaccine = TextORM.getOne(Vaccine.class, dataMap -> Objects.equals(dataMap.get("vaccineName"), "Chapalang Vaccine"));

        if (foundVaccine != null) {
            System.out.printf("[%d] %s, %d day(s) between doses. Finished: %b%n", foundVaccine.getId(), foundVaccine.getVaccineName(), foundVaccine.getDaysBetweenDoses(), foundVaccine.isFinished());
            System.out.println("Deleting " + foundVaccine.getVaccineName());
            foundVaccine.delete();
        }


        VaccineCenter center = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Movenpick"));
        if (center != null) {
            System.out.println(center.getName());
        }

        Account account1 = TextORM.getOne(Account.class, dataMap -> Double.parseDouble(dataMap.get("balance")) <= 3000.00);
        if (account1 != null) {
            System.out.println(account1);
            System.out.println("Age of " + account1.getName() + " is " + ChronoUnit.YEARS.between(account1.getBirthDate(), LocalDate.now()));
        }
    }

    static void seedModels() {
        seedVaccines();
        seedCenters();
        seedAccount();
    }

    static void seedVaccines() {
        if (!Files.exists(TextORM.getRepositoryStorageLocation(Vaccine.class))) {
            new Vaccine("Sinovac", 21, 120.00, false).save();
            new Vaccine("Moderna", 14, 200.00, true).save();
            new Vaccine("Pfizer", 14, 160.00, true).save();
            new Vaccine("Sinopharm", 14, 240.00, false).save();
            new Vaccine("Johnson & Johnson", 21, 140.00, true).save();
            new Vaccine("Chapalang Vaccine", 2, 2.00, true).save();
        }
    }

    static void seedCenters() {
        if (!Files.exists(TextORM.getRepositoryStorageLocation(VaccineCenter.class))) {
            new VaccineCenter("Movenpick", 23.00, 304.00).save();
            new VaccineCenter("Bukit Jalil Stadium", 44.00, 201.00).save();
        }
    }

    static void seedAccount() {
        if (!Files.exists(TextORM.getRepositoryStorageLocation(Account.class))) {
            new Account("John Doe", 24, 4000.00, LocalDate.of(2001, 3, 6), "j.doe", "123").save();
            new Account("Jane Smith", 24, 2300.00, LocalDate.of(2000, 6, 26), "j.smith", "123").save();
            new Account("Julian Summers", 24, 5000.00, LocalDate.of(2001, 12, 2), "summers.j", "123").save();
        }
    }
}