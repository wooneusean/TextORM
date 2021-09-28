package win.woon.textorm;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TextORM {
    private static final Path storagePath = Paths.get("");

    public static <T> ArrayList<T> getAll(Class<T> model, Function<HashMap<String, String>, Boolean> filter) {
        ArrayList<T> models = new ArrayList<>();

        try {
            Path storageLocation = getRepositoryStorageLocation(model);
            if (!Files.exists(storageLocation)) return null;
            List<String> lines = Files.readAllLines(storageLocation);

            for (String line : lines) {
                HashMap<String, String> modelData = fromSaveString(line);
                if (filter.apply(modelData)) {

                    T modelInstance = model.getConstructor().newInstance();
                    modelData.forEach((s, s2) -> {
                        try {
                            updateInstanceFields(modelInstance, s, s2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    models.add(modelInstance);
                }
            }
            return models;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getOne(Class<T> model, Function<HashMap<String, String>, Boolean> filter) {
        try {
            Path storageLocation = getRepositoryStorageLocation(model);
            if (!Files.exists(storageLocation)) return null;
            List<String> lines = Files.readAllLines(storageLocation);

            for (String line : lines) {
                HashMap<String, String> modelData = fromSaveString(line);
                if (filter.apply(modelData)) {

                    T modelInstance = model.getConstructor().newInstance();
                    modelData.forEach((s, s2) -> {
                        try {
                            updateInstanceFields(modelInstance, s, s2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    return modelInstance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void updateInstanceFields(T modelInstance, String s, String s2) throws NoSuchFieldException, IllegalAccessException {
        Field currentField = modelInstance.getClass().getField(s);
        Object value = s2;
        if (Integer.class.isAssignableFrom(currentField.getType()) || int.class.isAssignableFrom(currentField.getType())) {
            value = Integer.parseInt(s2);
        } else if (Boolean.class.isAssignableFrom(currentField.getType()) || boolean.class.isAssignableFrom(currentField.getType())) {
            value = Boolean.parseBoolean(s2);
        }
        currentField.set(modelInstance, value);
    }

    protected static <T> Path getRepositoryStorageLocation(Class<T> model) {
        return Paths.get(storagePath.toString(), getTableName(model) + ".txt");
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
        Field[] fields = model.getFields();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Field field : fields) {
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
        Column column = field.getAnnotation(Column.class);
        if (column == null) return null;

        return field.getName();
    }

    protected static HashMap<String, String> fromSaveString(String saveString) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] fieldPairs = saveString.split("\\|");

        for (String fieldPair : fieldPairs) {
            String[] pair = fieldPair.split(":");
            hashMap.put(pair[0], pair[1]);
        }
        return hashMap;
    }
}
