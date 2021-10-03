package io.github.euseanwoon2016.textorm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TextORM {
    private static Path storagePath = Paths.get("");
    private static Path metaStoragePath = storagePath;

    public static void setStoragePath(String storagePath) {
        TextORM.storagePath = Paths.get(storagePath);
    }

    public static void setMetaStoragePath(String metaStoragePath) {
        TextORM.metaStoragePath = Paths.get(metaStoragePath);
    }

    public static <T extends Model> List<T> getAll(Class<T> model, Function<HashMap<String, String>, Boolean> filter, Class<? extends Model>... includes) {
        ArrayList<T> models = new ArrayList<>();

        List<String> lines = readModelRepository(model);

        if (lines == null) return null;

        T modelInstance;

        for (String line : lines) {
            HashMap<String, String> modelData = SaveString.toHashMap(line);
            if (filter.apply(modelData)) {
                try {
                    modelInstance = model.getConstructor().newInstance();

                    for (var entry : modelData.entrySet()) {
                        updateInstanceFields(modelInstance, entry.getKey(), entry.getValue());
                    }

                    for (Class<? extends Model> include : includes) {
                        modelInstance.include(include);
                    }

                    models.add(modelInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return models;
    }

    public static <T extends Model> T getOne(Class<T> model, Function<HashMap<String, String>, Boolean> filter, Class<? extends Model>... includes) {
        List<String> lines = readModelRepository(model);

        if (lines == null) return null;

        T modelInstance = null;

        for (String line : lines) {
            HashMap<String, String> modelData = SaveString.toHashMap(line);
            if (filter.apply(modelData)) {
                try {
                    modelInstance = model.getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                for (var entry : modelData.entrySet()) {
                    updateInstanceFields(modelInstance, entry.getKey(), entry.getValue());
                }
            }
        }

        if (modelInstance == null) return null;

        for (Class<? extends Model> include : includes) {
            modelInstance.include(include);
        }

        return modelInstance;
    }

    private static <T> boolean updateInstanceFields(T modelInstance, String key, String value) {
        Field currentField = findFieldInSuperclasses(modelInstance.getClass(), key);

        if (currentField == null) return false;

        currentField.setAccessible(true);

        Object newValue = value;
        if (Integer.class.isAssignableFrom(currentField.getType()) || int.class.isAssignableFrom(currentField.getType())) {
            newValue = Integer.parseInt(value);
        } else if (Boolean.class.isAssignableFrom(currentField.getType()) || boolean.class.isAssignableFrom(currentField.getType())) {
            newValue = Boolean.parseBoolean(value);
        } else if (Double.class.isAssignableFrom(currentField.getType()) || double.class.isAssignableFrom(currentField.getType())) {
            newValue = Double.parseDouble(value);
        } else if (LocalDate.class.isAssignableFrom(currentField.getType())) {
            newValue = LocalDate.parse(value);
        } else if (Enum.class.isAssignableFrom(currentField.getType())) {
            newValue = Enum.valueOf((Class<Enum>) currentField.getType(), value);
        }

        try {
            currentField.set(modelInstance, newValue);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> Path getRepositoryStorageLocation(Class<T> model) {
        return Paths.get(storagePath.toString(), getTableName(model) + ".txt");
    }

    protected static <T> Path getMetaLocation() {
        return Paths.get(metaStoragePath.toString(), "TextORM.meta");
    }

    protected static <T> String getTableName(Class<T> model) {
        Repository repository = model.getAnnotation(Repository.class);
        if (repository == null) throw new IllegalArgumentException("Provided class is not a repository!");

        String tableName = model.getSimpleName();
        if (!Objects.equals(repository.repositoryName(), "")) {
            tableName = repository.repositoryName();
        }
        return tableName;
    }

    private static <T> HashMap<String, String> toHashMap(Class<T> model) {
        Field[] fields = model.getDeclaredFields();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Column.class) != null) {
                try {
                    hashMap.put(field.getName(), field.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(hashMap);
        return hashMap;
    }

    protected static String getColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation == null) return null;

        return field.getName();
    }

    protected static <T> List<String> readModelRepository(Class<T> model) {
        try {
            Path storageLocation = getRepositoryStorageLocation(model);
            if (!Files.exists(storageLocation)) return null;
            return Files.readAllLines(storageLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static <T> Meta readModelMeta(Class<T> model) {
        Path storageLocation = getMetaLocation();
        String tableName = getTableName(model);

        try {
            if (!Files.exists(storageLocation)) return null;
            List<String> lines = Files.readAllLines(storageLocation);

            for (String line : lines) {
                var meta = new Meta(line);
                if (Objects.equals(meta.tableName, tableName)) {
                    return meta;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    protected static void createFileIfEmpty(Path storageLocation) throws IOException {
        File file = storageLocation.toFile();
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
    }

    public static <T> Field findFieldInSuperclasses(Class<T> clazz, String fieldName) {
        Class<?> current = clazz;
        Field foundField = null;
        do {
            try {
                foundField = current.getDeclaredField(fieldName);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return foundField;
    }

    public static <T> List<Field> getAllColumnFields(Class<T> clazz) {
        Class<?> current = clazz;
        ArrayList<Field> columnFields = new ArrayList<>();
        do {
            try {
                Field[] fields = current.getDeclaredFields();
                for (Field field : fields) {
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    if (columnAnnotation != null) {
                        columnFields.add(field);
                    }
                }
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);

        return columnFields;
    }

    public static <T> List<Field> getAllFieldsWhere(Class<T> clazz, Function<Field, Boolean> filter) {
        Class<?> current = clazz;
        ArrayList<Field> foundFields = new ArrayList<>();
        do {
            Field[] fields = current.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (filter.apply(field)) {
                    foundFields.add(field);
                }
            }
        } while ((current = current.getSuperclass()) != null);
        return foundFields;
    }
}
