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

public class Model {
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

        List<Field> foreignKeyAnnotatedFields = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getAnnotation(ForeignKey.class) != null);
        for (Field field : foreignKeyAnnotatedFields) {
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

            if (foreignKey == null) continue;

            if (!Model.class.isAssignableFrom(field.getType())) {
                System.err.println("The Foreign Key annotated object '" + field.getName() + "' on class '" + this.getClass().getSimpleName() + "' does not extend the Model class.");
                return;
            }

            try {
                field.setAccessible(true);
                Model model = (Model) field.get(this);
                model.save();
            } catch (Exception ignored) {
            }
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

    public <T extends Model> void include(Class<T> toInclude) {
        List<Field> toIncludeFields = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getType() == toInclude);

        if (toIncludeFields.size() <= 0) {
            System.err.println("This model has no relations with '" + toInclude.getSimpleName() + "'.");
            return;
        }

        for (Field field : toIncludeFields) {
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

            if (foreignKey == null) continue;

            // By right, this should never get triggered,
            // but I'm leaving this here. Because why not.
            if (!Model.class.isAssignableFrom(field.getType())) {
                System.err.println("The Foreign Key annotated object does not extend the Model class.");
                return;
            }

            String otherKeyFieldName = foreignKey.foreignKey();

            try {
                Field otherKeyField = this.getClass().getDeclaredField(otherKeyFieldName);

                otherKeyField.setAccessible(true);
                int otherKeyValue = (int) otherKeyField.get(this);

                T includedObject = TextORM.getOne(toInclude, dataMap -> Integer.parseInt(dataMap.get("id")) == otherKeyValue);

                if (includedObject == null) {
                    System.err.println("The foreign key associated with this object does not belong to any records.");
                    return;
                }

                field.set(this, includedObject);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
