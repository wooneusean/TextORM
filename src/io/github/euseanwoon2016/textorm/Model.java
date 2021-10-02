package io.github.euseanwoon2016.textorm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
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

        saveIncludedModels();
    }

    private void saveIncludedModels() {
        // Save one-to-one models
        List<Field> hasOneAnnotatedFields = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getAnnotation(HasOne.class) != null);
        for (Field field : hasOneAnnotatedFields) {
            if (!Model.class.isAssignableFrom(field.getType())) {
                System.err.println("The Foreign Key annotated object '" + field.getName() + "' on class '" + this.getClass().getSimpleName() + "' does not extend the Model class.%n");
                return;
            }

            try {
                field.setAccessible(true);
                Model model = (Model) field.get(this);
                if (model == null) continue;

                model.save();
            } catch (Exception ignored) {
            }
        }

        // Save one-to-many models
        List<Field> hasManyAnnotatedFields = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getAnnotation(HasMany.class) != null);
        for (Field field : hasManyAnnotatedFields) {
            field.setAccessible(true);
            try {
                Model[] modelList = (Model[]) field.get(this);
                if (modelList == null) return;

                for (Model model : modelList) {
                    model.save();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
        List<Field> modelFields = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getType() == toInclude || field.getType().getComponentType() == toInclude);

        if (modelFields.size() > 1) {
            System.err.printf("There are multiple fields with the type '%s' when trying to include it into %s%n", toInclude.getSimpleName(), this.getClass().getSimpleName());
            return;
        }

        if (modelFields.size() == 0) {
            System.err.printf("There are no fields with type '%s' on model %s.%n", toInclude.getSimpleName(), this.getClass().getSimpleName());
            return;
        }

        Field toIncludeFiend = modelFields.get(0);
        Annotation[] annotations = toIncludeFiend.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(HasOne.class)) {
                includeOne(toInclude);
            } else if (annotation.annotationType().equals(HasMany.class)) {
                includeMany(toInclude);
            } else {
                System.err.printf("The field with type '%s' isn't annotated to point out its a model.%n", toInclude);
            }
        }
    }

    private <T extends Model> void includeOne(Class<T> toInclude) {
        List<Field> includeHasOne = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getType() == toInclude && field.getAnnotation(HasOne.class) != null);

        if (includeHasOne.size() <= 0) {
            System.err.println("This model has no relations with '" + toInclude.getSimpleName() + "'!\n");
            return;
        }

        if (includeHasOne.size() > 1) {
            System.err.println("This model has more than one one-to-one relations with '" + toInclude.getSimpleName() + "'!\n");
            return;
        }

        for (Field field : includeHasOne) {
            HasOne hasOneAnnotation = field.getAnnotation(HasOne.class);
            String otherKeyFieldName = hasOneAnnotation.foreignKey();

            try {
                Field otherKeyField = this.getClass().getDeclaredField(otherKeyFieldName);

                otherKeyField.setAccessible(true);
                int otherKeyValue = (int) otherKeyField.get(this);

                T includedObject = TextORM.getOne(toInclude, dataMap -> Integer.parseInt(dataMap.get("id")) == otherKeyValue);

                if (includedObject == null) {
                    System.err.printf("Error when trying to include '%s' to '%s'. There are no '%s' models that has id == %d.%n", toInclude.getSimpleName(), this.getClass().getSimpleName(), toInclude.getSimpleName(), otherKeyValue);
                    return;
                }

                field.set(this, includedObject);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private <T extends Model> void includeMany(Class<T> toInclude) {
        List<Field> includeHasMany = TextORM.getAllFieldsWhere(this.getClass(), field -> field.getType().getComponentType() == toInclude && field.getAnnotation(HasMany.class) != null);

        if (includeHasMany.size() <= 0) {
            System.err.println("This model has no relations with '" + toInclude.getSimpleName() + "'!\n");
            return;
        }

        if (includeHasMany.size() > 1) {
            System.err.println("This model has more than one one-to-many relations with '" + toInclude.getSimpleName() + "'!\n");
            return;
        }

        Field hasManyField = includeHasMany.get(0);
        hasManyField.setAccessible(true);

        HasMany hasManyAnnotation = hasManyField.getAnnotation(HasMany.class);

        List<T> toIncludeObjects = TextORM.getAll(toInclude, dataMap -> Objects.equals(dataMap.get(hasManyAnnotation.targetKey()), String.valueOf(this.getId())));

        if (toIncludeObjects != null) {
            try {
                hasManyField.set(this, toIncludeObjects.toArray((T[]) Array.newInstance(toInclude, toIncludeObjects.size())));
            } catch (Exception e) {
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
