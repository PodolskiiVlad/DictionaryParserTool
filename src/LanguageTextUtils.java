public class LanguageTextUtils {

    private static void removeSpaceBeforeSeparators(StringBuilder pair, String separator) {
        String[] separators = {" " + separator + " ", separator + " ", " " + separator, " / ", " /", "/ ", " // ", " //", "// ",};

        for (String symb : separators) {

            if (symb.equals(separators[0]) | symb.equals(separators[1]) | symb.equals(separators[2])) {
                while (pair.indexOf(symb) != -1) {
                    pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), separator);
                }
                continue;
            }

            while (pair.indexOf(" " + symb) != -1 | pair.indexOf(symb + " ") != -1) {
                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), symb.trim());
            }
        }
    }

    public static void lastClean(StringBuilder pair, String strPairSeparator) {
        for (String symb : PartsOfSpeech.LAST_CLEAN_SYBM) {
            if (symb.equals(strPairSeparator)) {
                continue;
            }

            while (pair.indexOf(symb) != -1) {
                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), " ");
            }
        }

        LanguageTextUtils.removeExcessSpaces(pair);
        LanguageTextUtils.removeSpaceBeforeSeparators(pair, strPairSeparator);

    }

    public static String selectWordFromDif(String wholeText, int index, char separator, char endOfString) {

        StringBuilder leftDef = new StringBuilder();
        StringBuilder rightDef = new StringBuilder();

        boolean searchInLeft = true;
        boolean searchInRight = true;

        int rightSearchIndex = index + 2;

        StringBuilder equal = new StringBuilder();

        for (; ; ) {
            String newString = getNextWords(wholeText, index, searchInLeft, rightSearchIndex, searchInRight, separator, endOfString);

            if (newString.equals("/")) {
                break;
            }

            String leftWord = newString.substring(0, newString.indexOf('/'));
            String rightWord = newString.substring(newString.indexOf('/') + 1, newString.length());

            if (wholeText.indexOf(leftWord + "/") != 0
                    | wholeText.charAt(wholeText.indexOf(leftWord + "/")) != ' '
                    | wholeText.charAt(wholeText.indexOf(leftWord + "/")) != separator) {
                searchInLeft = false;
            }

            if (wholeText.indexOf("/" + rightWord) + rightWord.length() != wholeText.length() - 1
                    | wholeText.charAt(wholeText.indexOf("/" + rightWord)) != ' '
                    | wholeText.charAt(wholeText.indexOf("/" + rightWord)) != separator) {
                searchInRight = false;
            }

            leftDef.append(leftWord).append(' ');
            rightDef.append(rightWord).append(' ');

            if (removeAllSpaces(leftDef.toString())
                    .equals(removeAllSpaces(rightDef.toString()))) {
                equal.delete(0, equal.length());
                equal.append(leftDef).append('/').append(rightDef);
            }

        }

        if (equal.length() > 0) {
            return equal.toString();
        } else {
            return getNextWords(wholeText, index, true, index + 2, true, separator, endOfString);
        }
    }

    private static String getNextWords(String wholeText, int leftIndex, boolean searchInLeft, int rightIndex, boolean searchInRight, char separator, char endOfString) {

        StringBuilder leftWordToAdd = new StringBuilder();
        StringBuilder rightWordToAdd = new StringBuilder();

        boolean writeToFirst = true;
        boolean writeToSecond = true;

        int j = rightIndex;
        int i = leftIndex;

        if (i <= 0) {
            searchInLeft = false;
        }

        if (j > wholeText.length() - 1) {
            searchInRight = false;
        }

        for (; ; ) {
            if (searchInLeft) {
                if (wholeText.charAt(i) == ' ') {
                    writeToFirst = false;
                    i--;
                }

                if (wholeText.charAt(i) == separator
                        | wholeText.charAt(i) == endOfString) {
                    writeToFirst = false;
                }

                if (writeToFirst) {
                    leftWordToAdd.append(wholeText.charAt(i));
                    i--;
                }

                if (i <= 0) {
                    searchInLeft = false;
                }
            } else {
                writeToFirst = false;
            }

            if (searchInRight) {
                if (wholeText.charAt(j) == ' ') {
                    writeToSecond = false;
                    j++;
                }

                if (wholeText.charAt(j) == separator
                        | wholeText.charAt(j) == endOfString) {
                    writeToSecond = false;
                }

                if (writeToSecond) {
                    rightWordToAdd.append(wholeText.charAt(j));
                    j++;
                }

                if (j >= wholeText.length() - 1) {
                    searchInRight = false;
                }
            } else {
                writeToSecond = false;
            }

            if (!writeToFirst & !writeToSecond) {
                break;
            }
        }

        leftWordToAdd.reverse();

        return leftWordToAdd.append('/').append(rightWordToAdd).toString();
    }

    public static boolean isSingleWord(int wordIndex, String wordToCheck, StringBuilder pair, char separator, char endOfString) {
        boolean freeBefore = false;
        boolean freeAfter = false;

        boolean leftFinded = false;
        boolean rightFinded = false;

        if (pair.charAt(wordIndex + wordToCheck.length()) == ' ') {
            freeAfter = true;
            rightFinded = true;
        }

        if (!rightFinded & pair.charAt(wordIndex + wordToCheck.length()) == '\t') {
            freeAfter = true;
            rightFinded = true;
        }

        if (!rightFinded & pair.charAt(wordIndex + wordToCheck.length()) == separator) {
            freeAfter = true;
            rightFinded = true;
        }

        if (!rightFinded & pair.charAt(wordIndex + wordToCheck.length()) == ',') {
            freeAfter = true;
            rightFinded = true;
        }

        if (!rightFinded & pair.charAt(wordIndex + wordToCheck.length()) == '.') {
            freeAfter = true;
            rightFinded = true;
        }

        if (!rightFinded & pair.charAt(wordIndex + wordToCheck.length()) == endOfString) {
            freeAfter = true;
        }

        if (wordIndex == 0){
            freeBefore = true;
            leftFinded = true;
        }

        if (!leftFinded & wordIndex > 0) {
            if (pair.charAt(wordIndex - 1) == ' ') {
                freeBefore = true;
                leftFinded = true;
            }
            if (!leftFinded & pair.charAt(wordIndex - 1) == '\t') {
                freeBefore = true;
                leftFinded = true;
            }
            if (!leftFinded & pair.charAt(wordIndex - 1) == ',') {
                freeBefore = true;
                leftFinded = true;
            }
            if (!leftFinded & pair.charAt(wordIndex - 1) == separator) {
                freeBefore = true;
            }
        }

        return freeBefore & freeAfter;
    }

    public static void cleanString(StringBuilder pair, String strSeparator) {

        removeExcessSpaces(pair);
        removeSpaceBeforeSeparators(pair, strSeparator);

        String deuText = pair.substring(0, pair.indexOf(strSeparator));
        String engText = pair.substring(pair.indexOf(strSeparator), pair.length());

        if (deuText.startsWith("-")){
            deuText = pair.substring(1, pair.indexOf(strSeparator));
        }

        if (engText.startsWith("-")){
            engText = pair.substring(1, pair.length());
        }

        deuText = deuText.trim() + strSeparator;
        engText = engText.trim() + '\n';

        pair.delete(0, pair.length());
        pair.append(deuText.toLowerCase()).append(engText.toLowerCase());
    }

    public static boolean isSingleLangWord(StringBuilder pair, String strPairSeparator) {
        int wordsCount = 0;

        String originString = pair.substring(0, pair.indexOf(strPairSeparator)).trim();

        for (char ch : originString.toCharArray()) {
            if (ch == ' ') {
                wordsCount++;
            }

            if (wordsCount > 0) {
                return false;
            }
        }

        return true;
    }

    private static void removeExcessSpaces(StringBuilder pair) {
        String result = removeExcessSpaces(pair.toString().toCharArray());

        pair.delete(0, pair.length());
        pair.append(result);
    }

    private static String removeExcessSpaces(char[] toClean) {

        char charBefore = 'z';
        char[] result = new char[toClean.length];
        int j = 0;

        for (int i = 0; i <= toClean.length - 1; i++) {
            if (i == 0 & toClean[i] == ' ') {
                continue;
            }

            result[j] = toClean[i];
            j++;

            if (charBefore == ' ' & toClean[i] == ' ' & charBefore == toClean[i]) {
                j--;
            }

            charBefore = toClean[i];
        }

        return new String(result);
    }

    private static String removeAllSpaces(String string) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') {
                continue;
            }
            builder.append(string.charAt(i));
        }

        return builder.toString();
    }

    public static String[] getLanguageAbbereviations(Language lang) {
        switch (lang) {
            case EN:
                return PartsOfSpeech.EN;
            case DE:
                return PartsOfSpeech.DE;
            case RU:
                return PartsOfSpeech.RU;
            case BG:
                return PartsOfSpeech.BG;
            case CS:
                return PartsOfSpeech.CS;
            case DA:
                return PartsOfSpeech.DA;
            case EO:
                return PartsOfSpeech.EO;
            case ES:
                return PartsOfSpeech.ES;
            case FI:
                return PartsOfSpeech.FI;
            case FR:
                return PartsOfSpeech.FR;
            case HU:
                return PartsOfSpeech.HU;
            case IS:
                return PartsOfSpeech.IS;
            case IT:
                return PartsOfSpeech.IT;
            case LA:
                return PartsOfSpeech.LA;
            case NL:
                return PartsOfSpeech.NL;
            case NO:
                return PartsOfSpeech.NO;
            case PL:
                return PartsOfSpeech.PL;
            case PT:
                return PartsOfSpeech.PT;
            case RO:
                return PartsOfSpeech.RO;
            case SK:
                return PartsOfSpeech.SK;
            case SV:
                return PartsOfSpeech.SV;
            case TR:
                return PartsOfSpeech.TR;
            case BS:
            case EL:
            case HR:
            case SQ:
            case SR:
            case UA:
            default:
                return null;
        }
    }

    enum Language {
        EN, DE, RU, UA, BG,
        BS, CS, DA, EL, EO,
        ES, FI, FR, HR, HU,
        IS, IT, LA, NL, NO,
        PL, PT, RO, SK, SQ,
        SR, SV, TR
    }
}
