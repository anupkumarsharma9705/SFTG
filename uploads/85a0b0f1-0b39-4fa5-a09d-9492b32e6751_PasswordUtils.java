//package com.passowrdanalyzer.passowrd_analyzer.util;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//public class PasswordUtils {
//
//    private static final List<String> COMMON_PASSWORDS = Arrays.asList(
//            "123456", "password", "123456789", "12345", "12345678", "qwerty",
//            "abc123", "111111", "letmein", "monkey", "football", "iloveyou"
//    );
//
//    public static PasswordAnalysisResult analyze(String password) {
//        double entropy = calculateEntropy(password);
//        String strength = getStrength(entropy);
//        boolean breached = COMMON_PASSWORDS.contains(password);
//
//        PasswordAnalysisResult result = new PasswordAnalysisResult();
//        result.setEntropy(entropy);
//        result.setStrength(strength);
//        result.setBreached(breached);
//
//        return result;
//    }
//
//    private static double calculateEntropy(String password) {
//        int charsetSize = 0;
//        if (password.matches(".*[a-z].*")) charsetSize += 26;
//        if (password.matches(".*[A-Z].*")) charsetSize += 26;
//        if (password.matches(".*[0-9].*")) charsetSize += 10;
//        if (password.matches(".*[^a-zA-Z0-9].*")) charsetSize += 32;
//
//        return password.length() * (Math.log(charsetSize) / Math.log(2));
//    }
//
//    private static String getStrength(double entropy) {
//        if (entropy < 28) return "Very Weak";
//        if (entropy < 36) return "Weak";
//        if (entropy < 60) return "Reasonable";
//        if (entropy < 128) return "Strong";
//        return "Very Strong";
//    }
//
//    public static String suggestStrongPassword(String weakPassword) {
//        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
//        Random rand = new Random();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 12; i++) {
//            sb.append(chars.charAt(rand.nextInt(chars.length())));
//        }
//        return sb.toString();
//    }
//}


package com.passowrdanalyzer.passowrd_analyzer.util;

import java.util.*;

public class PasswordUtils {

    private static final List<String> COMMON_PASSWORDS = Arrays.asList(
            "123456", "password", "123456789", "12345678", "qwerty", "abc123", "111111", "letmein", "welcome", "admin"
    );

    private static final Map<Character, String[]> REPLACEMENTS = new HashMap<>();

    static {
        REPLACEMENTS.put('a', new String[]{"@", "4", "/-\\"});
        REPLACEMENTS.put('e', new String[]{"3", "€"});
        REPLACEMENTS.put('i', new String[]{"1", "!", "|"});
        REPLACEMENTS.put('o', new String[]{"0", "()", "°"});
        REPLACEMENTS.put('s', new String[]{"$", "5", "§"});
        REPLACEMENTS.put('t', new String[]{"7", "+"});
        REPLACEMENTS.put('l', new String[]{"1", "!"});
        REPLACEMENTS.put('b', new String[]{"8"});
        REPLACEMENTS.put('g', new String[]{"9", "&"});
    }

    public static double calculateEntropy(String password) {
        int charsetSize = 0;
        if (password.matches(".*[a-z].*")) charsetSize += 26;
        if (password.matches(".*[A-Z].*")) charsetSize += 26;
        if (password.matches(".*[0-9].*")) charsetSize += 10;
        if (password.matches(".*[^a-zA-Z0-9].*")) charsetSize += 32;

        return Math.round(password.length() * (Math.log(charsetSize) / Math.log(2)) * 100.0) / 100.0;
    }

    public static PasswordAnalysisResult analyze(String password) {
        double entropy = calculateEntropy(password);
        String strength = getStrength(entropy);
        boolean breached = COMMON_PASSWORDS.contains(password);

        PasswordAnalysisResult result = new PasswordAnalysisResult();
        result.setEntropy(entropy);
        result.setStrength(strength);
        result.setBreached(breached);

        return result;
    }

    private static String getStrength(double entropy) {
        if (entropy < 28) return "Very Weak";
        if (entropy < 36) return "Weak";
        if (entropy < 60) return "Reasonable";
        if (entropy < 128) return "Strong";
        return "Very Strong";
    }
    public static String suggestStrongPassword(String weakPassword) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String evaluateStrength(String password) {
        double entropy = calculateEntropy(password);
        if (entropy < 28) return "Very Weak";
        else if (entropy < 36) return "Weak";
        else if (entropy < 60) return "Moderate";
        else if (entropy < 128) return "Strong";
        else return "Very Strong";
    }

    public static boolean isCommonPassword(String password) {
        return COMMON_PASSWORDS.contains(password.toLowerCase());
    }

    public static String suggestPassword(String password) {
        StringBuilder suggestion = new StringBuilder();

        for (char c : password.toCharArray()) {
            if (REPLACEMENTS.containsKey(Character.toLowerCase(c))) {
                String[] repl = REPLACEMENTS.get(Character.toLowerCase(c));
                suggestion.append(repl[new Random().nextInt(repl.length)]);
            } else {
                suggestion.append(c);
            }
        }

        if (suggestion.toString().equals(password))
            suggestion.append("!");

        return suggestion.toString();
    }
}
