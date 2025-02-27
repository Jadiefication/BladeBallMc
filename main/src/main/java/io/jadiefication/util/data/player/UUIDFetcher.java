package io.jadiefication.util.data.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {
    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    public static UUID getUUID(String username) {
        try {
            URL url = new URL(String.format(PROFILE_URL, username));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();

                if (line != null) {
                    // Parse JSON response to get UUID
                    String id = line.split("\"id\":\"")[1].split("\"")[0];
                    return UUID.fromString(
                            id.substring(0, 8) + "-" +
                                    id.substring(8, 12) + "-" +
                                    id.substring(12, 16) + "-" +
                                    id.substring(16, 20) + "-" +
                                    id.substring(20)
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

