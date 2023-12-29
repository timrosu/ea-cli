package tools;

public class Auth {
    private static String filePath = "conf/credentials.json";

    public static boolean credentialsWritten() {
        String jsonData = Reader.readFromFile(filePath);
        if (jsonData != null && !jsonData.isEmpty()) {
            JsonParser jsonParser = JsonParser.getInstance();
            String storedUsername = jsonParser.getValue(jsonData, "username");
            String storedPassword = jsonParser.getValue(jsonData, "password");
            return storedUsername != null && !storedUsername.isEmpty() && storedPassword != null && !storedPassword.isEmpty();
        }
        return false;
    }

    public static String getUsername() {
        String jsonData = Reader.readFromFile(filePath);
        if (jsonData != null && !jsonData.isEmpty()) {
            JsonParser jsonParser = JsonParser.getInstance();
            return jsonParser.getValue(jsonData, "username");
        }
        return null;
    }

    public static String getPassword() {
        String jsonData = Reader.readFromFile(filePath);
        if (jsonData != null && !jsonData.isEmpty()) {
            JsonParser jsonParser = JsonParser.getInstance();
            return jsonParser.getValue(jsonData, "password");
        }
        return null;
    }

    public static void setCredentials(String username, String password) {
        Writer.writeCredentials(filePath, username, password);
    }
    public static void deleteCredentials() {
        Writer.deleteCredentials(filePath);
    }
}
