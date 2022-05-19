import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Lexical lexical = Lexical.getObject();

        File reservedFile = new File("src/main/java/files/reserved_words");
        Scanner scanner = new Scanner(reservedFile);
        List<String> reservedList = new LinkedList<>();
        while (scanner.hasNext()){
            reservedList.add(scanner.next());
        }
        lexical.setReservedWords(reservedList);

        File identifiersFile = new File("src/main/java/files/standard_identifires");
        scanner = new Scanner(identifiersFile);
        List<String> identifiersList = new LinkedList<>();
        while (scanner.hasNext()){
            identifiersList.add(scanner.next());
        }
        lexical.setStandardIdentifiers(identifiersList);

        File specialFile = new File("src/main/java/files/special_symbols");
        scanner = new Scanner(specialFile);
        List<String> specialList = new LinkedList<>();
        while (scanner.hasNext()){
            specialList.add(scanner.next());
        }
        lexical.setSpecialSymbols(specialList);

        List<Map.Entry<String , Integer>> out = null;
        try {
            out = lexical.compile(new File("src/main/java/files/source.pas"));
        } catch (IllegalCharacter IllegalCharacter) {
            System.out.println("Illegal character found: " + IllegalCharacter.getMessage());
        } catch (WrongNumberFormat wrongNumberFormat) {
            System.out.println("Wrong number format: " + wrongNumberFormat.getMessage());
        }
        Iterator<Map.Entry<String, Integer>> it = out.iterator();
        while(it.hasNext()){
            Map.Entry<String , Integer> x = it.next();
            System.out.println(x.getKey() + "\t\t\t" + x.getValue());
        }

    }
}
