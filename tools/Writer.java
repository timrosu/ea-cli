package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Writer {
   public static void writeCredentials(String filePath, String username, String password) {
   JsonObject jsonObject = new JsonObject();
   jsonObject.addProperty("username", username);
   jsonObject.addProperty("password", password);

   String updatedJsonData = jsonObject.toString();
   writeToFile(filePath, updatedJsonData);
   }


   public static void deleteCredentials(String filePath) {
      String jsonData = Reader.readFromFile(filePath);
      if (jsonData != null && !jsonData.isEmpty()) {
          JsonElement jsonElement = JsonParser.parseString(jsonData);

         if (jsonElement.isJsonObject()) {
              JsonObject jsonObject = jsonElement.getAsJsonObject();

              jsonObject.remove("username");
              jsonObject.remove("password");

              String updatedJsonData = jsonObject.toString();
              writeToFile(filePath, updatedJsonData);
         }
      }
   }


   private static void writeToFile(String filePath, String jsonData) {
       try {
           Files.write(Paths.get(filePath), jsonData.getBytes());
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

}
