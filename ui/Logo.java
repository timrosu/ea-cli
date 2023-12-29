package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Logo {
    private static final String text = "conf/ea-cli_ascii.logo";

    public static void getLogo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(text))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

