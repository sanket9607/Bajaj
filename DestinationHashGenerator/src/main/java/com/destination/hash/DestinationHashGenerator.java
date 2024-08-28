package com.destination.hash;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to JSON file>");
            return;
        }
        
        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];
        
        try (FileReader reader = new FileReader(jsonFilePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            String destinationValue = findDestination(jsonElement);
            
            if (destinationValue != null) {
                String randomString = generateRandomString(8);
                String concatenatedString = prnNumber + destinationValue + randomString;
                String md5Hash = generateMD5Hash(concatenatedString);
                
                System.out.println(md5Hash + ";" + randomString);
            } else {
                System.out.println("Key 'destination' not found in the JSON file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String findDestination(JsonElement element) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if ("destination".equals(entry.getKey())) {
                    return entry.getValue().getAsString();
                }
                String result = findDestination(entry.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                String result = findDestination(arrayElement);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
