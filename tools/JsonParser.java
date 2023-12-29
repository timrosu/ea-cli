package tools;

import com.google.gson.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class JsonParser {
    private static JsonParser instance;
    private Gson gson;

    private JsonParser() {
        gson = new Gson();
    }

    public static JsonParser getInstance() {
        if (instance == null) {
            instance = new JsonParser();
        }
        return instance;
    }

    public String getValue(String jsonData, String path) {
        JsonElement jsonElement = gson.fromJson(jsonData, JsonElement.class);
        JsonElement result = traverseJson(jsonElement, path);
        if (result != null && result.isJsonPrimitive()) {
            return result.getAsString();
        }
        return null;
    }

    public void setValue(String filePath, String path, String value) {
        try {
            String jsonData = Files.readString(Path.of(filePath));
            JsonElement jsonElement = gson.fromJson(jsonData, JsonElement.class);
            traverseAndSetJson(jsonElement, path, value);
            String updatedJsonData = gson.toJson(jsonElement);
            Files.writeString(Path.of(filePath), updatedJsonData, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonElement traverseJson(JsonElement jsonElement, String path) {
        if (jsonElement == null) {
            return null;
        }
    
        String[] segments = path.split("/");
        for (String segment : segments) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonElement = jsonObject.get(segment);
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                int index = Integer.parseInt(segment);
                if (index >= 0 && index < jsonArray.size()) {
                    jsonElement = jsonArray.get(index);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return jsonElement;
    }

    private void traverseAndSetJson(JsonElement jsonElement, String path, String value) {
        String[] segments = path.split("/");
        int lastIndex = segments.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            String segment = segments[i];
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonElement = jsonObject.get(segment);
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                int index = Integer.parseInt(segment);
                if (index >= 0 && index < jsonArray.size()) {
                    jsonElement = jsonArray.get(index);
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String lastSegment = segments[lastIndex];
            jsonObject.addProperty(lastSegment, value);
        }
    }
        
    public int getArrayLength(String jsonData, String path) {
        JsonElement jsonElement = gson.fromJson(jsonData, JsonElement.class);
        JsonElement result = traverseJson(jsonElement, path);
        if (result != null && result.isJsonArray()) {
            JsonArray jsonArray = result.getAsJsonArray();
            return jsonArray.size();
        }
        return 0;
    }

}
