import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Lexical {
    static private Lexical obj;
    final private int integerIndex = 0;
    final private int floatIndex = 1;
    final private int userIdentifiersIndex = 2;
    private List<String> reservedWords;
    private List<String> standardIdentifiers;
    private List<String> specialSymbols;
    private Map<String, Integer> reservedMap;
    private Map<String, Integer> specialMap;


    private Lexical() {
    }

    static public Lexical getObject() {
        if (obj == null) {
            obj = new Lexical();
        }
        return obj;
    }


    public void setReservedWords(List<String> reservedWords) {
        this.reservedWords = reservedWords;
    }


    public void setStandardIdentifiers(List<String> standardIdentifiers) {
        this.standardIdentifiers = standardIdentifiers;
    }


    public void setSpecialSymbols(List<String> specialSymbols) {
        this.specialSymbols = specialSymbols;
    }

    public Map<String, Integer> getReservedMap() {
        return reservedMap;
    }

    public Map<String, Integer> getSpecialMap() {
        return specialMap;
    }

    public List<Map.Entry<String , Integer>> compile(File sourceCode) throws FileNotFoundException, IllegalCharacter, WrongNumberFormat {
        reservedMap = new HashMap<String, Integer>();
        specialMap = new HashMap<String, Integer>();
        List<Map.Entry<String , Integer>> output = new LinkedList<>();

        int i = 0;
        Iterator<String> it = reservedWords.iterator();
        while (it.hasNext()) {
            while (i == integerIndex || i == floatIndex || i == userIdentifiersIndex) {
                i++;
            }
            reservedMap.put(it.next(), i);
            i++;
        }

        it = standardIdentifiers.iterator();
        while (it.hasNext()) {
            while (i == integerIndex || i == floatIndex || i == userIdentifiersIndex) {
                i++;
            }
            reservedMap.put(it.next(), i);
            i++;
        }

        it = specialSymbols.iterator();
        while (it.hasNext()) {
            while (i == integerIndex || i == floatIndex || i == userIdentifiersIndex) {
                i++;
            }
            specialMap.put(it.next(), i);
            i++;
        }

        Scanner scanner = new Scanner(sourceCode);
        int commentFlag =0;
        while (scanner.hasNext()) {
            String next = scanner.next();
            int line = 0;
            while (next.length() > 0) {
                if(commentFlag == 1 ){
                    int k;
                    for(k = 0 ; k < next.length() ; k++){
                        if(next.charAt(k) == '}'){
                            commentFlag = 0 ;
                            break;
                        }
                    }
                    if(k<next.length()) {
                        next = next.substring(k + 1);
                    }
                    else {
                        next ="";
                    }
                }

                if(commentFlag == 2 ){
                    int k;
                    for(k = 0 ; k < next.length() ; k++){
                        if(next.charAt(k) == '*'){
                            if(k+1 < next.length()){
                                if(next.charAt(k+1) == ')'){
                                    commentFlag = 0 ;
                                    break;
                                }
                            }
                        }
                    }
                    if(k<next.length()) {
                        next = next.substring(k + 2);
                    }
                    else {
                        next ="";
                    }
                }

                if(next.length()==0){
                    continue;
                }
                int j = 0;
                if (Character.isDigit(next.charAt(0))) {
                    while (j < next.length() && (Character.isDigit(next.charAt(j)) || next.charAt(j) == '.')) {
                        j++;
                    }
                    String token = next.substring(0, j);
                    if (isInteger(token)) {
                        output.add(new AbstractMap.SimpleEntry<String, Integer>(next.substring(0,j), integerIndex));
                    } else if (isFloat(token)) {
                        output.add(new AbstractMap.SimpleEntry<String, Integer>(next.substring(0,j), floatIndex));
                    } else {
                        throw new WrongNumberFormat(token);
                    }
                    next = next.substring(j);
                } else if (Character.isAlphabetic(next.charAt(0)) || next.charAt(0) == '_') {
                    while (j < next.length() && (Character.isAlphabetic(next.charAt(j)) || next.charAt(j) == '_')) {
                        j++;
                    }
                    String token = next.substring(0, j);
                    if (reservedMap.containsKey(token)) {
                        output.add(new AbstractMap.SimpleEntry<String, Integer>(token, reservedMap.get(token)));
                    } else {
                        output.add(new AbstractMap.SimpleEntry<String, Integer>(token, userIdentifiersIndex));
                    }
                    next = next.substring(j);
                } else if (next.charAt(0) == '{'){
                    commentFlag = 1 ;
                    next = next.substring(1);
                } else if(next.charAt(0) == '(' && next.length()>1 && next.charAt(1) == '*'){
                    commentFlag = 2;
                    next = next.substring(2);
                }
                else {
                    String token;
                    if (next.length() == 1) {
                        token = next;
                        if (specialMap.containsKey(token)) {
                            output.add(new AbstractMap.SimpleEntry<String, Integer>(token, specialMap.get(token)));
                            next = next.substring(1);
                        } else {
                            throw new IllegalCharacter(token);
                        }
                    } else {
                        token = next.substring(0, 2);
                        if (specialMap.containsKey(token)) {
                            output.add(new AbstractMap.SimpleEntry<String, Integer>(token, specialMap.get(token)));
                            next = next.substring(2);
                        } else {
                            token = next.substring(0, 1);
                            if (specialMap.containsKey(token)) {
                                output.add(new AbstractMap.SimpleEntry<String, Integer>(token, specialMap.get(token)));
                                next = next.substring(1);
                            } else {
                                throw new IllegalCharacter(token);
                            }
                        }
                    }


                }
            }
        }
        return output;
    }

    private boolean isFloat(String token) {
        boolean pointExists = false;
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                if (token.charAt(i) == '.') {
                    if (pointExists) {
                        return false;
                    } else {
                        pointExists = true;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInteger(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
