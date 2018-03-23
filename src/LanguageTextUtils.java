public class LanguageTextUtils {

    public static String selectWholeWord(String wholeText, int index) {
        StringBuilder temp = new StringBuilder(new String(new char[]{wholeText.charAt(index)}));
        StringBuilder result = new StringBuilder();
        char ch = ' ';

        for (int i = index; wholeText.charAt(i) != ch; i++) {
            temp.append(wholeText.charAt(i));
        }


        for (int i = index; wholeText.charAt(i) != ch; i--) {
            result.append(wholeText.charAt(i));
        }

        return result.reverse().append(temp).toString();
    }

    public static String removeExcessSpaces(char[] string) {

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
