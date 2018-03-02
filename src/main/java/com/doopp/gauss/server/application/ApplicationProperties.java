package com.doopp.gauss.server.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    ApplicationProperties() {
        String applicationPropertiesConfig = System.getProperty("applicationPropertiesConfig");
        try {
            FileReader fileReader = new FileReader(applicationPropertiesConfig);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            this.load(bufferedReader);
        }
        catch(IOException e) {

        }
    }

    public String s(String key) {
        return this.getProperty(key);
    }

    public int i(String key) {
        return Integer.valueOf(this.getProperty(key));
    }

    public boolean b(String key) {
        return Boolean.valueOf(this.getProperty(key));
    }
}
