import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictParser {

    private static final String EMPTY_STRING = "";
    private static final int PAIR_NUMBER = 1000000;

    private String pathTo;
    private String pathFrom;

    private String strPairSeparator;
    private char chPairSeparator;
    private char endOfString;

    private StringBuilder finalText;
    private Map<String, String> pairMap;

    private String[] customWordClasses;
    private String[] firstLangAbbrs;
    private String[] secLangAbbrs;

    private LanguageTextUtils.Language firstLang;
    private LanguageTextUtils.Language secLang;

    private boolean checkForSingleWords = true;

    DictParser(String pathFrom,
               String pathTo,
               char chPairSeparator,
               char endOfString,
               LanguageTextUtils.Language firstLang,
               LanguageTextUtils.Language secLang) {

        this.pathFrom = pathFrom;
        this.pathTo = pathTo;
        this.chPairSeparator = chPairSeparator;
        this.endOfString = endOfString;
        this.firstLang = firstLang;
        this.secLang = secLang;
        strPairSeparator = new String(new char[]{chPairSeparator});
        finalText = new StringBuilder(PAIR_NUMBER * 10);
        pairMap = new HashMap<>(PAIR_NUMBER);
    }

    public void processFile() {
        StringBuilder pair = new StringBuilder();
        List<String> text = FileReader.readFile(pathFrom);

        if (text == null) {
            return;
        }

        text.forEach(n -> {
            pair.append(n);
            parse(pair);

            if (pair.length() < 40 & isSingleWord(pair) & !pairMap.containsKey(pair.substring(0, pair.charAt(chPairSeparator)))) {
                finalText.append(pair);
            }

            pair.delete(0, pair.length());
        });

        FileReader.writeToFile(finalText, pathTo);
    }

    private void parse(StringBuilder pair) {
        System.out.println("Parsing");

        checkExcessStrings(pair);
        removeExcessVariants(pair);
        checkForBrackets(pair);
    }

    private void removeExcessVariants(StringBuilder pair) {
        while (pair.indexOf("/") != -1) {
            String firstWord = LanguageTextUtils.selectWholeWord(pair.toString(), pair.indexOf("/") - 2);
            firstWord = firstWord.trim();
            firstWord = firstWord.toLowerCase();

            String secondWord = LanguageTextUtils.selectWholeWord(pair.toString(), pair.indexOf("/") + 2);
            secondWord = secondWord.trim();
            secondWord = secondWord.toLowerCase();

            int wordLength = LanguageTextUtils.selectWholeWord(pair.toString(), pair.indexOf("/") - 2).length();

            int firstIndex = pair.indexOf("/") - wordLength;
            int secIndex = pair.indexOf("/") + 2;

            if (firstWord.equals(secondWord)){
                pair.delete(firstIndex, secIndex);
            }
        }
    }

    private boolean isSingleWord(StringBuilder pair){
        if (!checkForSingleWords){
            return true;
        }

        return true;
    }

    private void checkForBrackets(StringBuilder pair){
        for (int i = 0; i < PartsOfSpeech.BRACKETS.length - 1; i = i + 2) {
            String startBracket = PartsOfSpeech.BRACKETS[i];
            String endBracket = PartsOfSpeech.BRACKETS[i + 1];

            if (startBracket.equals(strPairSeparator)
                    | endBracket.equals(strPairSeparator)){
                continue;
            }

            int fromIndex = 0;

            int startBracketIndex = 0;
            int endBracketIndex = 0;

            while (pair.indexOf(startBracket, fromIndex) != -1){
                startBracketIndex = pair.indexOf(startBracket, fromIndex);

                if (pair.indexOf(endBracket, fromIndex) != -1){
                    endBracketIndex = pair.indexOf(endBracket, fromIndex);

                    if (pair.substring(startBracketIndex + 1, endBracketIndex).contains(startBracket)){
                        fromIndex = startBracketIndex + 1;
                        continue;
                    }

                    pair.delete(startBracketIndex, endBracketIndex + 1);

                    fromIndex = 0;
                }
            }
        }
    }

    private void checkExcessStrings(StringBuilder pair) {
        String[] wordClasses = customWordClasses == null ? PartsOfSpeech.WORD_CLASSES : customWordClasses;
        String[] firstAbbrs = firstLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(firstLang) : firstLangAbbrs;
        String[] secAbbrs = secLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(secLang) : secLangAbbrs;

        for (int i = 0; i < 3; i++) {
            for (String wordClass : wordClasses) {
                int match = pair.lastIndexOf(wordClass);

                if (match > pair.indexOf(strPairSeparator)
                        & match != -1
                        & pair.charAt(match - 1) == (' ')
                        & pair.charAt(match + wordClass.length()) == (' ' | endOfString)) {
                    pair.replace(match, match + wordClass.length(), EMPTY_STRING);
                }
            }
        }

        int newVal;
        int fromIndex = 0;

        if (firstAbbrs != null) {
            for (String abbr : firstAbbrs) {
                while (pair.indexOf(abbr, fromIndex) < pair.indexOf(strPairSeparator) | pair.indexOf(abbr, fromIndex) != -1) {

                    newVal = pair.indexOf(abbr, fromIndex);
                    int match = pair.indexOf(abbr, fromIndex);

                    if (pair.charAt(match - 1) == ' ' | match == 0
                            & pair.charAt(match + abbr.length()) == (' ' | this.chPairSeparator)) {
                        pair.replace(match, match + abbr.length(), EMPTY_STRING);
                    } else {
                        newVal += abbr.length();
                    }

                    fromIndex = newVal;
                }
            }
        }

        fromIndex = pair.indexOf(strPairSeparator);

        if (secAbbrs != null) {
            for (String abbr : secAbbrs) {
                while (pair.indexOf(abbr, fromIndex) != -1) {

                    newVal = pair.indexOf(abbr, fromIndex);
                    int match = pair.indexOf(abbr, fromIndex);

                    if (pair.charAt(match - 1) == (this.chPairSeparator | ' ')
                            & pair.charAt(match + abbr.length()) == (' ' | endOfString)) {
                        pair.replace(match, match + abbr.length(), EMPTY_STRING);
                    } else {
                        newVal += abbr.length();
                    }

                    fromIndex = newVal;
                }
            }
        }

        for (String symb : PartsOfSpeech.STANDART_SYMBOLS) {
            while (pair.indexOf(symb) != -1) {

                if (symb.equals(strPairSeparator)) {
                    continue;
                }

                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + 1, " ");
            }
        }
    }

    public void setCustomWordClasses(String[] wordClasses) {
        customWordClasses = wordClasses;
    }

    public void setFirstLanguageAbbreviations(String[] languageAbbreviations) {
        firstLangAbbrs = languageAbbreviations;
    }

    public void setSecondLanguageAbbreviations(String[] languageAbbreviations) {
        secLangAbbrs = languageAbbreviations;
    }

    public void setCheckForSingleWords(boolean needToCheck){
        checkForSingleWords = needToCheck;
    }
}
