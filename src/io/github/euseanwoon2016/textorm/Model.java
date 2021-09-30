package io.github.euseanwoon2016.textorm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Model<T> {
    @Column()
    private int id = -1;

    public int getId() {
        return id;
    }

    public void save() {
        try {
            Path storageLocation = TextORM.getRepositoryStorageLocation(this.getClass());
            TextORM.createFileIfEmpty(storageLocation);

            BufferedReader reader = new BufferedReader(new FileReader(storageLocation.toAbsolutePath().toString()));
            StringBuilder outputBuffer = new StringBuilder();
            String line;

            boolean recordExists = false;
            while ((line = reader.readLine()) != null) {
                // Update existing
                HashMap<String, String> lineMap = SaveString.toHashMap(line);
                if (Objects.equals(lineMap.get("id"), String.valueOf(this.id))) {
                    outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
                    recordExists = true;
                } else {
                    outputBuffer.append(line).append(System.lineSeparator());
                }
            }
            reader.close();

            if (!recordExists) {
                Meta meta = TextORM.readModelMeta(this.getClass());
                if (meta == null) {
                    meta = new Meta(TextORM.getTableName(this.getClass()), 1);
                }
                this.id = meta.autoIncrement++;
                outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
                meta.save();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(storageLocation.toAbsolutePath().toString()));
            writer.write(outputBuffer.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean delete() {
        try {
            Path storageLocation = TextORM.getRepositoryStorageLocation(this.getClass());
            if (!Files.exists(storageLocation)) return false;

            BufferedReader reader = new BufferedReader(new FileReader(storageLocation.toAbsolutePath().toString()));
            StringBuilder outputBuffer = new StringBuilder();
            String line;

            boolean recordExists = false;
            while ((line = reader.readLine()) != null) {
                // Update existing
                HashMap<String, String> lineMap = SaveString.toHashMap(line);
                if (!Objects.equals(lineMap.get("id"), String.valueOf(this.id))) {
                    outputBuffer.append(line).append(System.lineSeparator());
                }
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(storageLocation.toAbsolutePath().toString()));
            writer.write(outputBuffer.toString());
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String toSaveString() {
        ArrayList<String> fieldData = new ArrayList<>();
        this.toHashMap().forEach((s, s2) -> fieldData.add(s + ":" + s2));
        return String.join("|", fieldData);
    }

    private HashMap<String, String> toHashMap() {
        HashMap<String, String> hashMap = new HashMap<>();

        // Superclass walking
        Field pkField = TextORM.findFieldInSuperclasses(this.getClass(), "id");

        if (pkField != null) {
            try {
                pkField.setAccessible(true);
                hashMap.put(pkField.getName(), pkField.get(this).toString());
            } catch (Exception ignored) {
            }
        }

        List<Field> fields = TextORM.getAllColumnFields(this.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                hashMap.put(field.getName(), field.get(this).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }
}
