package com.example.cryptoanalys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EncryptionCesar {
    private static EncryptionCesar instance;
    public static final ArrayList<Character> ALPHABET_ENGLISH = new ArrayList<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', '\'', '"', ',', ':', ';', '.', '!', '?'));
    public static final ArrayList<Character> ALPHABET_UKRAINIAN = new ArrayList<>(Arrays.asList('а', 'б', 'в', 'г', 'ґ', 'д', 'е', 'є', 'ж', 'з', 'и', 'і', 'ї',
            'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ь', 'ю', 'я', '`',
            ' ', '\'', '"', ',', ':', ';', '.', '!', '?'));
    private ArrayList<Character> currentAlpha = ALPHABET_ENGLISH;
    protected int key = 3;

    public int getKey() {
        return this.key;
    }
    public void setKey(int key) {
        this.key = key;
    }

    public void switchAlphabet(String alpha) {
        this.currentAlpha = (Objects.equals(alpha, "en")) ? ALPHABET_ENGLISH : ALPHABET_UKRAINIAN;
    }

    public String encrypt(String data) {
        StringBuilder newStr = new StringBuilder("");
        for (char c : data.toCharArray()) {
            int index = currentAlpha.indexOf(Character.toLowerCase(c));
            if(index != -1) {
                int newIndex = (index + key) % currentAlpha.size();
                if(newIndex < 0) {
                    newIndex = currentAlpha.size() + newIndex;
                }
                newStr.append(currentAlpha.get(newIndex));
            } else {
                newStr.append(c);
            }
        }

        newStr.append("\n");

        return newStr.toString();
    }

    public static EncryptionCesar getInstance() {
        if (instance == null) {
            instance = new EncryptionCesar();
        }
        return instance;
    }
}
