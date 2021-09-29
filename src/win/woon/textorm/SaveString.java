package win.woon.textorm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveString {
    public static HashMap<String, String> toHashMap(String line) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] fieldPairs = line.split("\\|");

        for (String fieldPair : fieldPairs) {
            String[] pair = fieldPair.split(":");
            hashMap.put(pair[0], pair[1]);
        }
        return hashMap;
    }

    public static String fromHashMap(Map<String, String> hashMap) {
        ArrayList<String> saveString = new ArrayList<>();
        for (var entry : hashMap.entrySet()) {
            saveString.add(entry.getKey() + ":" + entry.getValue());
        }

        return String.join("|", saveString);
    }
}
