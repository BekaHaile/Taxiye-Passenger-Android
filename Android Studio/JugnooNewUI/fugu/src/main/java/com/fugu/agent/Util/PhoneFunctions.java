package com.fugu.agent.Util;

public final class PhoneFunctions {
    private static PhoneFunctions instance;

    public PhoneFunctions() {
    }

    public static PhoneFunctions getInstance() {
        if (instance == null) {
            instance = new PhoneFunctions();
        }

        return instance;
    }

    public String getCountry(String[] argStringArray, String argText) {
        String country = "";

        if (argText.length() >= 4) {
            for (int i = 0; i < argStringArray.length; i++) {
                String[] g = argStringArray[i].split(",");
                if (g[0].equals(argText.substring(0, 4))) {
                    country = g[0];
                    break;
                }
                if (g[0].equals(argText.substring(0, 3))) {
                    country = g[0];
                    break;
                }
                if (g[0].equals(argText.substring(0, 2))) {
                    country = g[0];
                    break;
                }
            }
        }

        return country;
    }
}