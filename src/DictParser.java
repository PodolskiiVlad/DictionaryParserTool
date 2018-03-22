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

    private String pairSeparator;
    private String startOfString;
    private String endOfString;

    private StringBuilder finalText;
    private Map<String, String> pairMap;

    DictParser(String pathFrom, String pathTo, String pairSeparator, String endOfString) {
        this.pathFrom = pathFrom;
        this.pathTo = pathTo;
        this.pairSeparator = pairSeparator;
        this.endOfString = endOfString;
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

        checkExcessStrings(pair);
    }

    private void checkExcessStrings(StringBuilder pair) {

    }

    enum Language{
        EN, DE, RU, UA, BG,
        BS, CS, DA, EL, EO,
        ES, FI, FR, HR, HU,
        IS, IT, LA, NL, NO,
        PL, PT, RO, SK, SQ,
        SR, SV, TR;
    }
}
