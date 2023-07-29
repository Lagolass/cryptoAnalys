package com.example.cryptoanalys;

import java.util.*;

public class AnalyseDecryption {
    private static AnalyseDecryption instance;
    private ArrayList<String> popularWordsUA = new ArrayList<>(Arrays.asList(
            "привіт", "дякую", "надобраніч", "мова", "україна", "київ", "любов", "радість", "сонце",
            "мовчання", "життя", "родина", "друзі", "весілля", "їжа", "музика", "книга", "подорож", "автомобіль",
            "робота", "свято", "гроші", "кохання", "весна", "літо", "осінь", "зима", "квіти", "місто", "село", "школа",
            "університет", "релігія", "здоров'я", "спорт", "культура", "історія", "мистецтво", "наука", "природа",
            "тварини", "загадка", "віра", "надія", "справедливість", "мир", "війна", "свобода", "діти", "мовний",
            "сніг", "дощ", "вода", "палац", "ліс", "сад", "сміх", "сльози", "танець", "смак", "вечір", "ранок",
            "шоколад", "сніданок", "обід", "вечеря", "медицина", "коханець", "ворог", "подруга", "читання",
            "відпочинок", "здоровий", "шанс", "смуток", "здивування", "священик", "вчитель", "студент", "комп'ютер",
            "телефон", "фото", "кошеня", "цуценя", "вітер", "тепло", "тиск", "пам'ять", "думки", "хмари", "пісок",
            "секрет", "прихована", "зрада", "сон", "пісня", "стріла", "літак", "корабель", "метро", "кохана",
            "подарунок", "мода", "правда", "довіра", "сила", "воля"
    ));

    private ArrayList<String> popularWordsEN = new ArrayList<>(Arrays.asList(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "I", "it", "for", "not", "on", "with", "he",
            "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
            "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about",
            "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know",
            "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then",
            "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two", "how", "our",
            "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most",
            "us"
    ));

    private ArrayList<Character> punctuationMarks = new ArrayList<>(Arrays.asList(',', ':', ';', '.', '!', '?'));

    private ArrayList<String> currentLangList = popularWordsEN;

    private EncryptionCesar encryptionCesar = EncryptionCesar.getInstance();

    private HashMap<Integer, Integer> keyStatistic = new HashMap<>();
    private int maxTrust = 0;

    public void analyse(String data, int key) {
        StringTokenizer tokenizer = new StringTokenizer(data, " ");
        int countTrust = 0;
        if(tokenizer.countTokens() > 0) {
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().toLowerCase().trim();
                String word2 = (word.length() > 1) ? word.substring(0, word.length() - 1) : "";
                char punctuation = (word.length() > 1) ? word.charAt(word.length() - 1) : '0';
                if(punctuationMarks.contains(punctuation)) {
                    countTrust++;
                }

                if(currentLangList.contains(word)) {
                    countTrust++;
                }

                if(currentLangList.contains(word2)) {
                    countTrust++;
                }
            }
        }

        if(countTrust > maxTrust) {
            maxTrust = countTrust;
        }

        keyStatistic.put(countTrust, key);
    }

    public boolean keyWasFind() {
        return maxTrust > 0;
    }

    public int getKey() {
        return keyStatistic.get(maxTrust);
    }

    public HashMap getKeyStatistic() {
        return keyStatistic;
    }

    public void switchLangList(String locale) {
        this.currentLangList = (Objects.equals(locale, "en")) ? popularWordsEN : popularWordsUA;
    }


    public static AnalyseDecryption getInstance() {
        if (instance == null) {
            instance = new AnalyseDecryption();
        }
        return instance;
    }
}
