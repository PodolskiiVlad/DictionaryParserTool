import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictParser {

    private static final String EMPTY_STRING = "";
    private static final int PAIR_NUMBER = 1000000;
    private static int currentPair = 0;
    private static int currentWritedPair = 0;
    private static int count = 0;

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

    private LanguageTextUtils.Language originLang;
    private LanguageTextUtils.Language translationLang;

    private boolean checkForSingleWords = true;

    DictParser(String pathFrom,
               String pathTo,
               char chPairSeparator,
               char endOfString,
               LanguageTextUtils.Language originLang,
               LanguageTextUtils.Language translationLang) {

        this.pathFrom = pathFrom;
        this.pathTo = pathTo;
        this.chPairSeparator = chPairSeparator;
        this.endOfString = endOfString;
        this.originLang = originLang;
        this.translationLang = translationLang;
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

            if (!pair.toString().endsWith(String.valueOf(endOfString))) {
                pair.append(endOfString);
            }

            if (parse(pair)) {

                boolean isSingleLangWord = !checkForSingleWords || LanguageTextUtils.isSingleLangWord(pair, strPairSeparator);

                String originWord = pair.substring(0, pair.indexOf(strPairSeparator));
                boolean isMapContainsKey = pairMap.containsKey(originWord);

                if (!isMapContainsKey & pair.length() < 40 & validate(pair) & isSingleLangWord) {
                    finalText.append(pair);
                    pairMap.put(originWord, "s");
                    currentWritedPair++;
                    count++;
                }
            }
            pair.delete(0, pair.length());
        });

        FileReader.writeToFile(finalText, pathTo);
    }

    private boolean validate(StringBuilder pair) {
        String firstPart = pair.substring(0, pair.indexOf(strPairSeparator)).trim();
        String secondPart = pair.substring(pair.indexOf(strPairSeparator), pair.length()).trim();

        firstPart = firstPart.replace(strPairSeparator, EMPTY_STRING);
        firstPart = firstPart.replace(new String(new char[]{endOfString}), EMPTY_STRING);
        firstPart = firstPart.replace(" ", EMPTY_STRING);

        secondPart = secondPart.replace(strPairSeparator, EMPTY_STRING);
        secondPart = secondPart.replace(new String(new char[]{endOfString}), EMPTY_STRING);
        secondPart = secondPart.replace(" ", EMPTY_STRING);

        return firstPart.length() != 0 && secondPart.length() != 0;
    }

    private boolean parse(StringBuilder pair) {
        System.out.println("Parsing");

        LanguageTextUtils.cleanString(pair, strPairSeparator);
        checkForExcessStrings(pair);
        checkForBrackets(pair);
        removeExcessVariants(pair);

        String strPair = pair.toString().toLowerCase().trim();

        if (strPair.length() == 0) {
            return false;
        }

        LanguageTextUtils.cleanString(pair, strPairSeparator);
        LanguageTextUtils.lastClean(pair, strPairSeparator);
        checkWordsToRemove(pair);
        LanguageTextUtils.cleanString(pair, strPairSeparator);
        return true;
    }

    private void removeExcessVariants(StringBuilder pair) {
        while (pair.indexOf("/") != -1) {
            String wordsToChoose = LanguageTextUtils.selectWordFromDif(pair.toString(), pair.indexOf("/") - 1, chPairSeparator, endOfString);
            int separatorIndex = wordsToChoose.indexOf("/");

            String firstWord = wordsToChoose.substring(0, separatorIndex);
            firstWord = firstWord.trim().toLowerCase();

            String secondWord = wordsToChoose.substring(separatorIndex + 1, wordsToChoose.length());
            secondWord = secondWord.trim().toLowerCase();

            int delFrom;
            int delTo;

            if (firstWord.length() > secondWord.length()) {
                delFrom = pair.indexOf("/") - firstWord.length();
                delTo = pair.indexOf("/") + 1;
            } else {
                delFrom = pair.indexOf("/");
                delTo = pair.indexOf("/") + secondWord.length() + 1;
            }

            pair.delete(delFrom, delTo);
        }
        LanguageTextUtils.cleanString(pair, strPairSeparator);
    }

    private void checkForBrackets(StringBuilder pair) {
        for (int i = 0; i < PartsOfSpeech.BRACKETS.length - 1; i = i + 2) {
            String startBracket = PartsOfSpeech.BRACKETS[i];
            String endBracket = PartsOfSpeech.BRACKETS[i + 1];

            if (startBracket.equals(strPairSeparator)
                    | endBracket.equals(strPairSeparator)) {
                continue;
            }

            int fromIndex = 0;

            int startBracketIndex;
            int endBracketIndex;

            while (pair.indexOf(startBracket, fromIndex) != -1) {
                startBracketIndex = pair.indexOf(startBracket, fromIndex);

                if (pair.indexOf(endBracket, fromIndex) != -1) {
                    endBracketIndex = pair.indexOf(endBracket, fromIndex);

                    if (pair.substring(startBracketIndex + 1, endBracketIndex).contains(startBracket)) {
                        fromIndex = startBracketIndex + 1;
                        continue;
                    }

                    pair.delete(startBracketIndex, endBracketIndex + 1);

                    fromIndex = 0;
                }
            }
        }
        LanguageTextUtils.cleanString(pair, strPairSeparator);
    }

    private void checkForExcessStrings(StringBuilder pair) {
        String[] wordClasses = customWordClasses == null ? PartsOfSpeech.WORD_CLASSES : customWordClasses;
        String[] firstAbbrs = firstLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(originLang) : firstLangAbbrs;
        String[] secAbbrs = secLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(translationLang) : secLangAbbrs;

        for (int i = 0; i < 3; i++) {
            for (String wordClass : wordClasses) {
                int match = pair.lastIndexOf(wordClass);

                if (match == -1 | match < pair.indexOf(strPairSeparator)) {
                    continue;
                }

                int matchEndIndex = match + wordClass.length();
                int charBeforeMatch = pair.charAt(match - 1);
                char charAtMatchIndex = pair.charAt(matchEndIndex);
                int indexOfSeparator = pair.indexOf(strPairSeparator);

                if (match > indexOfSeparator
                        & (charBeforeMatch == ' ' | charBeforeMatch == '\t')
                        & (charAtMatchIndex == ' ' | charAtMatchIndex == endOfString)) {
                    pair.replace(match, match + wordClass.length(), EMPTY_STRING);
                }
            }
        }

        int newVal;
        int fromIndex = 0;

        if (firstAbbrs != null) {
            for (String abbr : firstAbbrs) {
                while (pair.indexOf(abbr, fromIndex) != -1 & pair.indexOf(abbr, fromIndex) < pair.indexOf(strPairSeparator)) {
                    newVal = pair.indexOf(abbr, fromIndex);
                    int match = pair.indexOf(abbr, fromIndex);

                    if (match == -1) {
                        continue;
                    }

                    if (LanguageTextUtils.isSingleLangWord(pair, strPairSeparator)){
                        fromIndex++;
                        continue;
                    }

                    if (LanguageTextUtils.isSingleWord(newVal, abbr, pair, chPairSeparator, endOfString)) {
                        pair.replace(match, match + abbr.length(), EMPTY_STRING);
                    } else {
                        newVal += abbr.length();
                    }

                    fromIndex = newVal;
                }
                fromIndex = 0;
            }
        }

        if (secAbbrs != null) {
            for (String abbr : secAbbrs) {
                fromIndex = pair.indexOf(strPairSeparator);

                while (pair.indexOf(abbr, fromIndex) != -1) {

                    newVal = pair.indexOf(abbr, fromIndex);
                    int match = pair.indexOf(abbr, fromIndex);

                    if (match == -1) {
                        continue;
                    }

                    if (LanguageTextUtils.isSingleWord(newVal, abbr, pair, chPairSeparator, endOfString)) {
                        pair.replace(match, match + abbr.length(), EMPTY_STRING);
                    } else {
                        newVal += abbr.length();
                    }

                    fromIndex = newVal;
                }
            }
        }

        for (String symb : PartsOfSpeech.STANDART_SYMBOLS) {
            fromIndex = 0;
            while (pair.indexOf(symb, fromIndex) != -1) {
                int matchIndex = pair.indexOf(symb, fromIndex);

                if (symb.equals(strPairSeparator)) {
                    fromIndex = pair.indexOf(symb, fromIndex) + 1;
                    continue;
                }

                if ((symb.equals("A") | symb.equals("To") | symb.equals("to") | symb.equals("a"))
                        & !LanguageTextUtils.isSingleWord(matchIndex, symb, pair, chPairSeparator, endOfString)) {
                    fromIndex = pair.indexOf(symb, fromIndex) + 1;
                    continue;
                }

                pair.replace(matchIndex, matchIndex + symb.length(), " ");
            }
        }

        checkWordsToRemove(pair);

        LanguageTextUtils.cleanString(pair, strPairSeparator);
    }

    private void checkWordsToRemove(StringBuilder pair){
        for (String letter : PartsOfSpeech.SINGLE_SYMBOLS) {
            int fromIndex = 0;
            while (pair.indexOf(letter, fromIndex) != -1) {

                int match = pair.indexOf(letter, fromIndex);

                if (LanguageTextUtils.isSingleWord(match, letter, pair, chPairSeparator, endOfString)) {
                    pair.replace(match, match + letter.length(), " ");
                    continue;
                }

                fromIndex = match + 1;
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

    public void setCheckForSingleWords(boolean needToCheck) {
        checkForSingleWords = needToCheck;
    }
}
