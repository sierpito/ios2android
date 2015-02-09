package lv.sierpito.ios2android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class IOS2Android {
    // todo this must be set to root with [lang].properties files
    private static final String PATH = "[YOUR_PATH]";
    // iOS file name extension
    private static final String IOS_EXTENSION = ".strings";
    // Android file name extension
    private static final String ANDROID_EXTENSION = ".xml";
    // prefix for all Android resource strings
    private static final String ANDROID_PREFIX = "ios_";
    // here are available [lang].properties files
    private static final String[] NAMES = {
            "en", "ru", "de", "fr", "lv"
    };

    public static void main(String[] args) throws IOException {
        for (String name : NAMES) {
            convertFile(name);
        }
    }

    private static void convertFile(String filename) throws IOException {
        String from = PATH + filename + IOS_EXTENSION;
        String to = PATH + filename + ANDROID_EXTENSION;
        Scanner fileScanner = new Scanner(new FileInputStream(from));
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(to)), "UTF8"));
        writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.contains("=")) {
                String[] parts = line.split("=");

                String name = processName(parts[0]);
                String value = processValue(parts);

                String nextLine = "\t<string name=\"" + ANDROID_PREFIX + name + "\">" + value + "</string>";
                System.out.println(nextLine);
                writer.append(nextLine + "\n");
            } else if (line.startsWith("//")) {
                String nextLine = line.replaceAll("//", "");
                nextLine = "\t<!-- " + nextLine + " -->";
                System.out.println(nextLine);
                writer.append(nextLine + "\n");
            } else {
                System.out.println();
                writer.append("\n");
            }
        }
        fileScanner.close();
        writer.append("</resources>");
        writer.close();
    }

    private static String processName(String name) {
        name = name.trim()
                .replace(" ", "_")
                .replace("\\n", "_")
                .replace("-", "_")
                .replace("\"", "")
                .replace(";", "")
                .replace("'", "")
                .replace("/", "")
                .replace("(", "")
                .replace(")", "")
                .replace("?", "_Question")
                .replaceAll("[^A-Za-z0-9 _]", "");
        StringBuilder resultName = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                char prev = i > 0 ? name.charAt(i - 1) : '\0';
                if (prev != '_' && !Character.isUpperCase(prev) && prev != '\0') {
                    resultName.append('_');
                }
                resultName.append(Character.toLowerCase(c));
            } else {
                resultName.append(Character.toLowerCase(c));
            }
        }
        return resultName.toString();
    }

    private static String processValue(String[] parts) {
        String value = parts[1];
        if (parts.length>2) {
            for (int i=2; i<parts.length; i++) {
                value += "=" + parts[i];
            }
        }
        value = value.trim();
        value = value.substring(1, value.length()-2);
        value = value.replace("\\\"", "\"");
        value = value.replace("&", "&amp;");
        StringBuilder resultValue = new StringBuilder();
        if (value.contains("%@")) {
            int formatCnt = 0;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                char next = value.length() > i + 1 ? value.charAt(i + 1) : '\0';
                if (c == '%' && next == '@') {
                    formatCnt++;
                    resultValue.append('%');
                    resultValue.append(formatCnt);
                    resultValue.append("$s");
                    i++;
                } else {
                    resultValue.append(value.charAt(i));
                }
            }
        }   else{
            resultValue.append(value);
        }
        return resultValue.toString();
    }
}