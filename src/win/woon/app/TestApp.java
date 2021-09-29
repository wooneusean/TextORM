package win.woon.app;

import win.woon.textorm.TextORM;

import java.nio.file.Files;
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
                System.out.printf("[%d] %s, %d day(s) between doses. Finished: %b%n", vaccine.id, vaccine.vaccineName, vaccine.daysBetweenDoses, vaccine.isFinished);
            }
        }

        Vaccine sinovac = TextORM.getOne(Vaccine.class, dataMap -> Objects.equals(dataMap.get("vaccineName"), "Sinovac"));

        if (sinovac != null) {
            System.out.printf("[%d] %s, %d day(s) between doses. Finished: %b%n", sinovac.id, sinovac.vaccineName, sinovac.daysBetweenDoses, sinovac.isFinished);
            sinovac.daysBetweenDoses = (int) (Math.random() * 100);
            sinovac.save();
        }

        VaccineCenter center = TextORM.getOne(VaccineCenter.class, dataMap -> Objects.equals(dataMap.get("name"), "Movenpick"));
        if (center != null) {
            System.out.println(center.name);
        }
    }

    static void seedModels() {
        seedVaccines();
        seedCenters();
    }

    static void seedVaccines() {
        if (!Files.exists(TextORM.getRepositoryStorageLocation(Vaccine.class))) {
            new Vaccine("Sinovac", 21, 120.00, false).save();
            new Vaccine("Moderna", 14, 200.00, true).save();
            new Vaccine("Pfizer", 14, 160.00, false).save();
            new Vaccine("Sinopharm", 14, 240.00, false).save();
            new Vaccine("Johnson&Johnson", 21, 140.00, false).save();
        }
    }

    static void seedCenters() {
        if (!Files.exists(TextORM.getRepositoryStorageLocation(VaccineCenter.class))) {
            new VaccineCenter("Movenpick", 23.00, 304.00).save();
            new VaccineCenter("Bukit Jalil Stadium", 44.00, 201.00).save();
        }
    }
}
