import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MainPageController {
    @FXML
    private TextField reservedWordsField;

    @FXML
    private TextField srcCodeField;

    @FXML
    private TextField standerdField;

    @FXML
    private TextField symbolsField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label succesLabel;

    @FXML
    private ListView<Map.Entry<String , Integer>> tokensList;

    private File reservedFile;
    private File identifiersFile;
    private File specialFile;
    private File sourceFile;

    @FXML
    public void initialize(){
        reservedFile = new File("src/main/java/files/reserved_words");
        identifiersFile = new File("src/main/java/files/standard_identifires");
        specialFile = new File("src/main/java/files/special_symbols");
        //sourceFile = new File("src/main/java/files/source.pas");

        reservedWordsField.setText(reservedFile.getPath());
        standerdField.setText(identifiersFile.getPath());
        symbolsField.setText(specialFile.getPath());

        tokensList.setVisible(false);
        errorLabel.setText("");
        succesLabel.setVisible(false);
    }


    public void chooseReservedWordsFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/java/files"));
        fileChooser.setTitle("Browse Reserved Words File");
        File file = fileChooser.showOpenDialog(stage);
        if(file!= null) {
            reservedFile = file;
            reservedWordsField.setText(file.getPath());
        }
    }

    public void chooseStandardIdentifiersFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/java/files"));
        fileChooser.setTitle("Browse Standard Identifiers File");
        File file = fileChooser.showOpenDialog(stage);
        if(file!= null) {
            identifiersFile = file;
            standerdField.setText(file.getPath());
        }
    }

    public void chooseSpecialSymbolsFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/java/files"));
        fileChooser.setTitle("Browse Special Symbols File");
        File file = fileChooser.showOpenDialog(stage);
        if(file!= null) {
            specialFile = file;
            symbolsField.setText(file.getPath());
        }
    }

    public void chooseSrcCodeFile()  {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/java/files"));
        fileChooser.setTitle("Browse Source Code File");
        File file = fileChooser.showOpenDialog(stage);
        if(file!= null) {
            sourceFile = file;
            srcCodeField.setText(file.getPath());
        }
    }

    public void run() {
        if(sourceFile == null || specialFile==null || identifiersFile==null || reservedFile==null){
            errorLabel.setText("Please Specify All Files");
            errorLabel.setStyle("-fx-text-fill: red");
            tokensList.setVisible(false);
            succesLabel.setVisible(false);
        }
        else {
            Lexical lexical = Lexical.getObject();
            errorLabel.setText("");

            Scanner scanner = null;
            try {
                scanner = new Scanner(reservedFile);
            } catch (FileNotFoundException e) {
                errorLabel.setText("Reserved File not Found");
                errorLabel.setStyle("-fx-text-fill: red");
                tokensList.setVisible(false);
                succesLabel.setVisible(false);
            }
            List<String> reservedList = new LinkedList<>();
            while (scanner.hasNext()){
                reservedList.add(scanner.next());
            }
            lexical.setReservedWords(reservedList);

            try {
                scanner = new Scanner(identifiersFile);
            } catch (FileNotFoundException e) {
                errorLabel.setText("Standard Identifiers File not Found");
                errorLabel.setStyle("-fx-text-fill: red");
                tokensList.setVisible(false);
                succesLabel.setVisible(false);
            }
            List<String> identifiersList = new LinkedList<>();
            while (scanner.hasNext()){
                identifiersList.add(scanner.next());
            }
            lexical.setStandardIdentifiers(identifiersList);

            try {
                scanner = new Scanner(specialFile);
            } catch (FileNotFoundException e) {
                errorLabel.setText("Special Symbols File not Found");
                errorLabel.setStyle("-fx-text-fill: red");
                tokensList.setVisible(false);
                succesLabel.setVisible(false);
            }
            List<String> specialList = new LinkedList<>();
            while (scanner.hasNext()){
                specialList.add(scanner.next());
            }
            lexical.setSpecialSymbols(specialList);

            List<Map.Entry<String , Integer>> out = null;
            try {
                out = lexical.compile(sourceFile);
            } catch (IllegalCharacter IllegalCharacter) {
                errorLabel.setText("Illegal character found: " + IllegalCharacter.getMessage() + " at line " + Lexical.getLine(IllegalCharacter.getMessage() , sourceFile));
                errorLabel.setStyle("-fx-text-fill: red");
                tokensList.setVisible(false);
                succesLabel.setVisible(false);
            } catch (WrongNumberFormat wrongNumberFormat) {
                errorLabel.setText("Wrong number format: " + wrongNumberFormat.getMessage() + " at line " + Lexical.getLine(wrongNumberFormat.getMessage() , sourceFile));
                errorLabel.setStyle("-fx-text-fill: red");
                succesLabel.setVisible(false);
                tokensList.setVisible(false);
            } catch (FileNotFoundException e) {
                errorLabel.setText("Source Code File not Found");
                errorLabel.setStyle("-fx-text-fill: red");
                tokensList.setVisible(false);
                succesLabel.setVisible(false);
            }

            if(out != null) {
                    succesLabel.setVisible(true);
                    tokensList.setVisible(true);
                    tokensList.setItems(FXCollections.observableArrayList(out));
            }
        }
    }
}
