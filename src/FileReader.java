import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    public static void writeToFile(StringBuilder text, String path) {
        System.out.println("writing");
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String pathFrom) {
        System.out.println("reading");

        List<String> list = null;
        Path path = Paths.get(pathFrom);

        if (Files.isReadable(path)) {
            try {
                list = new ArrayList<>(Files.readAllLines(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
