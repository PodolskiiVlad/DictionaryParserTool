import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DictParser {

    public static final String EMPTY_STRING = "";
    public static final int DEFAULT_PAIR_NUMBER = 1000000;

    private String pathTo;
    private String pathFrom;

    private String strPairSeparator;
    private char chPairSeparator;
    private char startOfString;
    private char endOfString;

    private StringBuilder finalText;
    private Map<String, String> pairMap;

    private String [] customWordClasses;
    private String [] firstLangAbbrs;
    private String [] secLangAbbrs;

    private Language firstLang;
    private Language secLang;

    DictParser(String pathFrom, String pathTo, char chPairSeparator, char endOfString, Language firstLang, Language secLang) {
        this.pathFrom = pathFrom;
        this.pathTo = pathTo;
        this.chPairSeparator = chPairSeparator;
        this.endOfString = endOfString;
        this.firstLang = firstLang;
        this.secLang = secLang;
        strPairSeparator = new String(new char[]{chPairSeparator});
    }

    private void writeToFile() {
        System.out.println("writing");
        try {
            FileWriter writer = new FileWriter(pathTo);
            writer.write(finalText.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        System.out.println("reading");

        Path path = Paths.get(pathFrom);

        if (Files.isReadable(path)) {
            StringBuilder pair = new StringBuilder(100);

            try {
                Files.readAllLines(path).forEach(n -> {
                    pair.append(n);
                    parse(pair);

                    finalText.append(pair);
                    pair.delete(0, pair.length());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse(StringBuilder pair) {
        System.out.println("Parsing");

        checkExcessStrings(pair, customWordClasses, firstLangAbbrs, secLangAbbrs);
        checkForBrackets(pair);
    }

    private void removeExcessVariants(StringBuilder pair){

        while (pair.indexOf("/") != -1){
            if (selectWholeWord(pair.toString(), pair.indexOf("/") - 2).trim().toLowerCase().equals(selectWholeWord(pair.toString(), pair.indexOf("/") + 2).trim().toLowerCase())){
                pair.delete(pair.indexOf("/") - selectWholeWord(pair.toString(), pair.indexOf("/") -2).length(), pair.indexOf("/") + 2);
            }
        }
    }

    private void checkForBrackets(StringBuilder pair){
        for (int i = 0; i < PartsOfSpeech.BRACKETS.length - 1; i = i + 2) {
            String startBracket = PartsOfSpeech.BRACKETS[i];
            String endBracket = PartsOfSpeech.BRACKETS[i+1];

            while (pair.indexOf(startBracket) != -1 & pair.indexOf(endBracket) != -1){
                int startIndex = pair.indexOf(startBracket);
                int endIndex = pair.indexOf(endBracket);

                if (pair.indexOf(startBracket) != 0
                        && pair.charAt(pair.indexOf(startBracket) - 1) == '\t'
                        && endIndex + 1 <= pair.length() - 1){
                    boolean ans = true;

                    for (String ch: PartsOfSpeech.BRACKETS) {
                        if ( String.valueOf(pair.charAt(pair.indexOf(endBracket)+1)).equals(ch)){
                            ans = false;
                        }
                    }

                    if (ans & endIndex + 1 <= pair.length() - 1) endIndex++;

                }

                StringBuilder temp = new StringBuilder();
                for (int j = startIndex;j <= endIndex; j++){

                    if (String.valueOf(pair.charAt(j)).equals(startBracket)){
                        temp.delete(0, temp.length());
                    }

                    temp.append(pair.charAt(j));
                }

                pair.replace(pair.indexOf(temp.toString()), pair.indexOf(temp.toString()) + temp.length(), EMPTY_STRING);
            }
        }
    }

    private void checkExcessStrings(StringBuilder pair, String [] customWordClasses, String [] firstLangAbbrs, String [] secLangAbbrs) {
        String [] wordClasses = customWordClasses == null ? PartsOfSpeech.WORD_CLASSES : customWordClasses;
        String [] firstAbbrs = firstLangAbbrs == null ? getLanguageAbbereviations(firstLang) : firstLangAbbrs;
        String [] secAbbrs = secLangAbbrs == null ? getLanguageAbbereviations(secLang) : secLangAbbrs;

        for (int i = 0; i < 3; i++) {
        for (String wordClass : wordClasses){
            if (pair.lastIndexOf(wordClass) != - 1
                    & pair.charAt(pair.lastIndexOf(wordClass) - 1) == (' ')
                    & pair.charAt(pair.lastIndexOf(wordClass) + wordClass.length() + 1) == (' '|endOfString)){
                pair.replace(pair.lastIndexOf(wordClass), pair.lastIndexOf(wordClass) + wordClass.length(), EMPTY_STRING);
            }
        }
        }

        int newVal;
        int fromindex = 0;

        if (firstAbbrs != null){
            for (String abbr : firstAbbrs) {
                while (pair.indexOf(abbr, fromindex) < pair.indexOf(strPairSeparator) & pair.indexOf(abbr, fromindex) != -1){
                    newVal = pair.indexOf(abbr);
                    if (pair.charAt(pair.indexOf(abbr) - 1) == ' ' | pair.indexOf(abbr) == 0
                            & pair.charAt(pair.indexOf(abbr) + abbr.length() + 1) == (' '|this.chPairSeparator)) {
                        pair.replace(pair.indexOf(abbr, fromindex), pair.indexOf(abbr, fromindex) + abbr.length(), EMPTY_STRING);
                    }
                    fromindex = newVal;
                }
            }
        }

        fromindex = pair.indexOf(strPairSeparator);

        if (secAbbrs != null){
            for (String abbr : secAbbrs) {
                while (pair.indexOf(abbr, pair.indexOf(strPairSeparator)) != -1){
                    newVal = pair.indexOf(abbr);
                    if (pair.charAt(pair.indexOf(abbr) - 1) == (this.chPairSeparator |' ')
                            & pair.charAt(pair.indexOf(abbr) + abbr.length() + 1) == (' '|endOfString)) {
                        pair.replace(pair.indexOf(abbr, fromindex), pair.indexOf(abbr, fromindex) + abbr.length(), EMPTY_STRING);
                    }
                    fromindex = newVal;
                }
            }
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

    private String[] getLanguageAbbereviations(Language lang) {
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

    private String selectWholeWord(String wholeText, int index){
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

    private String removeExcessSpaces(){
        return null;
    }

    private String removeAllSpaces(String string){
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' '){
                continue;
            }
            builder.append(string.charAt(i));
        }

        return builder.toString();
    }
}
