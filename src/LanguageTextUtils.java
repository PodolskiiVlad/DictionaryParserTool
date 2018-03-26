public class LanguageTextUtils {

    public static String selectWordsFromDefinitions(String wholeText, int index, char separator, char endOfString) {
        StringBuilder result = new StringBuilder();

        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();

        int leftIndex = index;
        int rightIndex = index + 4;
        do {
            if (leftIndex < 0 | rightIndex >= wholeText.length()) {
                break;
            }

            getWholeWord(wholeText, leftIndex, rightIndex, left, right, separator, endOfString);
            rightIndex = right.length();
            leftIndex = -left.length();
            left.insert(0, ' ');

        }
        while (!removeAllSpaces(left.toString().toLowerCase().trim()).equals(removeAllSpaces(right.toString().toLowerCase().trim())));

        return result.append(left).append('/').append(right).toString().trim();
    }

    public static void removeSpaceBeforeSeparator(StringBuilder pair, String separator) {
        String[] separators = {" " + separator, separator + " "};

        for (String symb : separators) {
            if (pair.indexOf(symb) != -1) {
                pair.replace(pair.indexOf(symb), pair.indexOf(symb) + symb.length(), separator);
            }
        }
    }

    private static void getWholeWord(String wholeText, int leftIndex, int rightIndex, StringBuilder left, StringBuilder right, char separator, char endOfString) {
        StringBuilder first = new StringBuilder();
        StringBuilder sec = new StringBuilder();

        boolean writeToFirst = true;
        boolean writeToSecond = true;

        int j = rightIndex;
        int i = leftIndex;

        for (;i >= 0 & j <= wholeText.length(); ) {

            if (wholeText.charAt(i) == separator
                    | wholeText.charAt(i) == endOfString) {
                writeToFirst = false;
            }

            if (wholeText.charAt(j) == separator
                    | wholeText.charAt(j) == endOfString) {
                writeToSecond = false;
            }


            for (;;) {

                if (wholeText.charAt(i) == ' '){
                    writeToFirst = false;
                }

                if (writeToFirst) {
                    first.append(wholeText.charAt(i));
                    i--;
                }

                if (wholeText.charAt(j) == ' '){
                    writeToSecond = false;
                }

                if (writeToSecond) {
                    sec.append(wholeText.charAt(j));
                    j++;
                }

                if (!writeToFirst & !writeToSecond) {
                    break;
                }
            }

            if (first.reverse().toString().toLowerCase().trim()
                    .equals(sec.toString().toLowerCase().trim())) {
                break;
            }

            if (!writeToFirst & ! writeToSecond){
                break;
            }

        }

        first.reverse();

        String temp = left.toString();
        left.delete(0, left.length());
        left.append(first).append(temp);

        String secTemp = right.toString();
        right.delete(0, right.length());
        right.append(secTemp).append(sec);
    }

    public static boolean isPartOfWord(int wordIndex, String wordToCheck, StringBuilder pair) {
        return pair.charAt(wordIndex - 1) == ' '
                & (pair.charAt(wordIndex + wordToCheck.length()) - 1) == ' ';
    }

    public static void cleanString(StringBuilder pair, String strSeparator) {

        String deuText = pair.substring(0, pair.indexOf(strSeparator));
        String engText = pair.substring(pair.indexOf(strSeparator), pair.length());

        deuText = clean(deuText.toCharArray());
        engText = clean(engText.toCharArray());

        deuText = deuText.trim() + strSeparator;
        engText = engText.trim() + '\n';

        pair.delete(0, pair.length());
        pair.append(deuText.toLowerCase()).append(engText.toLowerCase());
    }

    private static String clean(char[] string) {

        char charBefore = 'z';
        char[] result = new char[string.length];
        int j = 0;

        for (int i = 0; i <= string.length - 1; i++) {
            result[j] = string[i];
            j++;

            if (charBefore == ' ' & string[i] == ' ' & charBefore == string[i]) {
                j--;
            }

            charBefore = string[i];
        }

        return new String(result);
    }

    public static boolean isSingleLangWord(StringBuilder pair, String strPairSeparator) {
        int wordsCount = 0;

        String originString = pair.substring(0, pair.indexOf(strPairSeparator)).trim();

        for (char ch : originString.toCharArray()) {
            if (ch == ' ') {
                wordsCount++;
            }

            if (wordsCount > 1) {
                return false;
            }
        }

        return true;
    }

    public static void removeExcessSpaces(StringBuilder pair) {

        char[] charsArray = pair.toString().toCharArray();

        char charBefore = 'z';
        char[] result = new char[charsArray.length];
        int j = 0;

        for (int i = 0; i <= charsArray.length - 1; i++) {
            result[j] = charsArray[i];
            j++;

            if (charBefore == ' ' & charsArray[i] == ' ' & charBefore == charsArray[i]) {
                j--;
            }

            charBefore = charsArray[i];
        }

        pair.delete(0, pair.length());
        pair.append(result);
    }

    public static String removeAllSpaces(String string) {
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
        SR, SV, TR;
    }
}
