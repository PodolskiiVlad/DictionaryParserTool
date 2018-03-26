public class Main {
    private static final String PATH_FROM = "/home/kaa-developer/Рабочий стол/from.txt";
    private static final String PATH_TO = "/home/kaa-developer/Рабочий стол/to.txt";

    public static void main(String[] args) {

        DictParser parser = new DictParser(PATH_FROM, PATH_TO, '\t', '\n', LanguageTextUtils.Language.DE, LanguageTextUtils.Language.EN);

        parser.processFile();
    }
}
