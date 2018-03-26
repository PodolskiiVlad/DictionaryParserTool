import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictParser {

    private static final String EMPTY_STRING = "";
    private static final int PAIR_NUMBER = 1000000;
    private static  int currentPair = 0;

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

            if (!pair.toString().endsWith(String.valueOf(endOfString))){
                pair.append(endOfString);
            }

            if (parse(pair)){

                boolean isSingleLangWord = !checkForSingleWords || LanguageTextUtils.isSingleLangWord(pair, strPairSeparator);

                String originWord = pair.substring(0, pair.indexOf(strPairSeparator));
                boolean isMapContainsKey = !pairMap.containsKey(originWord);

                if (isSingleLangWord & isMapContainsKey) {
                    finalText.append(pair);
                }

            }

            pair.delete(0, pair.length());
        });

        FileReader.writeToFile(finalText, pathTo);
    }

    private boolean parse(StringBuilder pair) {
        System.out.println("Parsing");

        if (pair.toString().contains("(Abstehender) Salzschwingel {m}\treflexed saltmarsh-grass / saltmarsh grass [Puccinellia distans, syn.: P. capillaris, P. limosa, P sevamgensis, Atropis distans, Glyceria distans, Poa distans]\tnoun\n")){
            System.out.println("Notice");
        }

        checkForExcessStrings(pair);
        checkForBrackets(pair);
        removeExcessVariants(pair);

        String strPair = pair.toString().toLowerCase().trim();

        if (strPair.length() == 0 ){
            return false;
        }

        LanguageTextUtils.cleanString(pair, strPairSeparator);
        lastClean(pair);

        System.out.println("String = " + pair);
        currentPair++;
        System.out.println("Current pair = " + currentPair);

        return true;
    }

    private void lastClean(StringBuilder pair){
        for (String symb: PartsOfSpeech.LAST_CLEAN_SYBM) {
            if (symb.equals(strPairSeparator)){
                continue;
            }

           while (pair.indexOf(symb) != -1){
                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), " ");
           }
        }

        LanguageTextUtils.removeExcessSpaces(pair);
        LanguageTextUtils.removeSpaceBeforeSeparator(pair, strPairSeparator);

    }

    private void removeExcessVariants(StringBuilder pair) {
        while (pair.indexOf("/") != -1) {
            String wordsToChoose = LanguageTextUtils.selectWordsFromDefinitions(pair.toString(), pair.indexOf("/") - 2, chPairSeparator, endOfString);
            int separatorIndex = wordsToChoose.indexOf("/");

            String firstWord = wordsToChoose.substring(0, separatorIndex);
            firstWord = firstWord.trim().toLowerCase();

            String secondWord = wordsToChoose.substring(separatorIndex + 1, wordsToChoose.length());
            secondWord = secondWord.trim().toLowerCase();

            int lastDelIndex = secondWord.length();

            for (int i = 0; i <= 3; i++){
                if (lastDelIndex != pair.indexOf(strPairSeparator)){
                    lastDelIndex++;
                }
            }

            if (firstWord.length() > secondWord.length()){
                pair.delete(pair.indexOf("/") - firstWord.length() - 1, pair.indexOf("/") + 1);
            } else {
                pair.delete(pair.indexOf("/"), pair.indexOf("/") + secondWord.length() + 3);
            }
        }
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

            int startBracketIndex;
            int endBracketIndex;

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

    private void checkForExcessStrings(StringBuilder pair) {
        String[] wordClasses = customWordClasses == null ? PartsOfSpeech.WORD_CLASSES : customWordClasses;
        String[] firstAbbrs = firstLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(originLang) : firstLangAbbrs;
        String[] secAbbrs = secLangAbbrs == null ? LanguageTextUtils.getLanguageAbbereviations(translationLang) : secLangAbbrs;

        for (int i = 0; i < 3; i++) {
            for (String wordClass : wordClasses) {
                int match = pair.lastIndexOf(wordClass);

                if (match == -1 | match < pair.indexOf(strPairSeparator)){
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

                    if (match == -1){
                        continue;
                    }

                    char charBeforeMatchIndex = ' ';
                    char charAfterMatch = ' ';

                    if (match != 0){
                       charBeforeMatchIndex = pair.charAt(match - 1);
                    }

                    if (match + abbr.length() + 1 <= pair.length()){
                        charAfterMatch = pair.charAt(match + abbr.length());
                    }

                    if (charBeforeMatchIndex == ' '
                            | match == 0
                            & (charAfterMatch == ' ' | charAfterMatch == this.chPairSeparator)) {
                        pair.replace(match, match + abbr.length(), EMPTY_STRING);
                    } else {
                        newVal += abbr.length();
                    }

                    fromIndex = newVal;
                }
            }
        }

        if (secAbbrs != null) {
            for (String abbr : secAbbrs) {
                fromIndex = pair.indexOf(strPairSeparator);

                while (pair.indexOf(abbr, fromIndex) != -1) {

                    newVal = pair.indexOf(abbr, fromIndex);
                    int match = pair.indexOf(abbr, fromIndex);

                    if (match == -1){
                        continue;
                    }

                    char charBeforeMatchIndex = pair.charAt(match - 1);
                    char charAfterMatch = pair.charAt(match + abbr.length());

                    if ((charBeforeMatchIndex == this.chPairSeparator | charBeforeMatchIndex ==  ' ')
                            & (charAfterMatch == ' ' | charAfterMatch == endOfString | charAfterMatch == chPairSeparator)) {
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

                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), " ");
            }
        }

        for (String letter: PartsOfSpeech.ALPHABET_SYMBOLS) {
            if (pair.indexOf(letter) != -1) {

                if (letter.endsWith(".") | letter.endsWith("-") & letter.startsWith("\t") | letter.endsWith("\t")) {
                    pair.replace(pair.indexOf(letter), pair.indexOf(letter) + letter.length(), "\t");
                    continue;
                }

                char charAfterLetter = pair.charAt(pair.indexOf(letter)+letter.length());

                if (pair.indexOf(letter) == 0 & (charAfterLetter == ' ' | charAfterLetter != '\t')) {
                    pair.replace(pair.indexOf(letter), pair.indexOf(letter) + letter.length(), "");
                }
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
