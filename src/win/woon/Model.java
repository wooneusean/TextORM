package win.woon;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static win.woon.TextORM.fromSaveString;
import static win.woon.TextORM.getRepositoryStorageLocation;

public class Model<T> {
    @Column
    public int id;

    public void save() {
        try {
            Path storageLocation = getRepositoryStorageLocation(this.getClass());
            createRepositoryIfEmpty(storageLocation);

            BufferedReader reader = new BufferedReader(new FileReader(storageLocation.toAbsolutePath().toString()));
            StringBuilder outputBuffer = new StringBuilder();
            String line;

            boolean recordExists = false;
            while ((line = reader.readLine()) != null) {
                // Update existing
                HashMap<String, String> lineMap = fromSaveString(line);
                if (Objects.equals(lineMap.get("id"), String.valueOf(this.id))) {
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

    private void createRepositoryIfEmpty(Path storageLocation) throws IOException {
        if (!Files.exists(storageLocation))
            Files.createFile(storageLocation);
    }

    private String toSaveString() {
        ArrayList<String> fieldData = new ArrayList<>();
        this.toHashMap().forEach((s, s2) -> {
            fieldData.add(s + ":" + s2);
        });
        return String.join("|", fieldData);
    }

    private HashMap<String, String> toHashMap() {
        Field[] fields = this.getClass().getFields();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Field field : fields) {
            if (field.getAnnotation(Column.class) != null) {
                try {
                    hashMap.put(field.getName(), field.get(this).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return hashMap;
    }
}
