package com.base.db;

import java.io.InputStream;
import java.util.Properties;

// need to update this file
public class DBConfig {
    private static Properties properties = new Properties();

    static {
        try (InputStream in = DBConfig.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if(in == null) {
                System.out.println("File not found : db.properties");
            } else {
                properties.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
