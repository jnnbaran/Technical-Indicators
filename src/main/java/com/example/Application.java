import reader.Reader;
import reader.WrongDataFormatException;

public class Application {
    public static void main(String[]args) {
        try {
            Reader.readFromFile("src/main/resources/data/01CYBATON.mst").forEach(System.out::println);
        } catch (WrongDataFormatException e) {
            System.out.println("Couldn't read file... \n" + e.getMessage());
        }
    }


}
