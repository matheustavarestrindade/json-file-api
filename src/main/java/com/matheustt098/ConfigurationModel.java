package com.matheustt098;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigurationModel {

    private File configurationFile;
    private String fileName;
    private String filePath;
    private JsonObject config;

    protected ConfigurationModel(String fileName) {
        this.fileName = fileName.toLowerCase();
        String jarPath = "";
        try {
            jarPath = ClassLoader.getSystemClassLoader().getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        this.filePath = jarPath;
        reloadConfig();
    }

    public void reloadConfig() {
        this.configurationFile = new File(filePath + "/" + this.fileName + ".json");
        if (!configurationFile.exists()) {
            try {
                if (!configurationFile.getParentFile().exists()) {
                    configurationFile.getParentFile().mkdir();
                }
                this.configurationFile.createNewFile();
                InputStream initialStream = getResource(fileName + ".json");
                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);
                OutputStream outStream = new FileOutputStream(this.configurationFile);
                outStream.write(buffer);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String jsonContent = stringBufferedReader(this.configurationFile.getAbsolutePath());
        System.out.println(jsonContent);
        this.config = new JsonParser().parse(jsonContent).getAsJsonObject();
    }

    protected JsonObject getConfig() {
        return this.config;
    }

    public void saveConfig() {
        try {
            FileWriter fw = new FileWriter(this.configurationFile, false);
            fw.write(config.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 
     * Private methods
     * 
     */

    private static String stringBufferedReader(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = ClassLoader.getSystemClassLoader().getResource(filename);
            if (url == null) {
                System.out.println("File is null");

                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

}
