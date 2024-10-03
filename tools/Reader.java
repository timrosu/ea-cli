package tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Reader {
    public static String readFromFile(String filePath) {
        String jsonData = "";
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
               Files.createFile(path);
            }
            byte[] bytes = Files.readAllBytes(path);
            jsonData = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}