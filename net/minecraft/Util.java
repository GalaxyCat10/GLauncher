/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
    private static File workDir = null;

    public static File getWorkingDirectory() {
        if (workDir == null) {
            workDir = Util.getWorkingDirectory("minecraft");
        }
        return workDir;
    }

    public static File getWorkingDirectory(String applicationName) {
        File workingDirectory;
        String userHome = System.getProperty("user.home", ".");
        switch (Util.getPlatform()) {
            case linux: 
            case solaris: {
                workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
                break;
            }
            case windows: {
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + applicationName + '/');
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
                break;
            }
            case macos: {
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            }
            default: {
                workingDirectory = new File(userHome, String.valueOf(applicationName) + '/');
            }
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }

    private static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OS.windows;
        }
        if (osName.contains("mac")) {
            return OS.macos;
        }
        if (osName.contains("solaris")) {
            return OS.solaris;
        }
        if (osName.contains("sunos")) {
            return OS.solaris;
        }
        if (osName.contains("linux")) {
            return OS.linux;
        }
        if (osName.contains("unix")) {
            return OS.linux;
        }
        return OS.unknown;
    }

    public static String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            String line;
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String string = response.toString();
            return string;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum OS {
        linux,
        solaris,
        windows,
        macos,
        unknown;

    }
}

