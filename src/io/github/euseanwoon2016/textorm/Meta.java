package io.github.euseanwoon2016.textorm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Meta {
    public String tableName;
    public int autoIncrement = 1;

    public Meta(String saveString) {
        fromSaveString(saveString);
    }

    public Meta(String tableName, int autoIncrement) {
        this.tableName = tableName;
        this.autoIncrement = autoIncrement;
    }

    public String toSaveString() {
        Map<String, String> map = Map.of("tableName", tableName, "autoIncrement", String.valueOf(autoIncrement));
        return SaveString.fromHashMap(map);
    }

    public void fromSaveString(String saveString) {
        String[] metas = saveString.split("\\|");

        for (String meta : metas) {
            String[] metaKeyValue = meta.split(":");

            if (Objects.equals(metaKeyValue[0], "tableName")) {
                tableName = metaKeyValue[1];
            } else if (Objects.equals(metaKeyValue[0], "autoIncrement")) {
                autoIncrement = Integer.parseInt(metaKeyValue[1]);
            }
        }
    }

    public void save() {
        try {
            Path storageLocation = TextORM.getMetaLocation();
            TextORM.createFileIfEmpty(storageLocation);

            BufferedReader reader = new BufferedReader(new FileReader(storageLocation.toAbsolutePath().toString()));
            StringBuilder outputBuffer = new StringBuilder();
            String line;

            boolean recordExists = false;
            while ((line = reader.readLine()) != null) {
                // Update existing
                HashMap<String, String> lineMap = SaveString.toHashMap(line);
                if (Objects.equals(lineMap.get("tableName"), tableName)) {
                    outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
                    recordExists = true;
                } else {
                    outputBuffer.append(line).append(System.lineSeparator());
                }
            }
            reader.close();

            if (!recordExists) {
                outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(storageLocation.toAbsolutePath().toString()));
            writer.write(outputBuffer.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
