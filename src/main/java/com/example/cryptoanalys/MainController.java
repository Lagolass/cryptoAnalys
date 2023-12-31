package com.example.cryptoanalys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private RadioButton radioLangEn;
    @FXML
    private RadioButton radioLangUa;

    @FXML
    private RadioButton modeEncrypt;
    @FXML
    private RadioButton modeDecrypt;
    @FXML
    private RadioButton modeBruteForce;

    @FXML
    private Spinner spinnerKey;
    @FXML
    private Label labelInfoFile;
    @FXML
    private TextArea txtAreaLog;

    @FXML
    Button btnExecute;

    private File currentFile;
    private File cacheDirectory;

    private EncryptionCesar encryptionCesar = EncryptionCesar.getInstance();
    private AnalyseDecryption analyseDecryption = AnalyseDecryption.getInstance();

    private enum OPERATION_MODE {
        ENCRYPT,
        DECRYPT,
        BRUTE_FORCE
    }

    private OPERATION_MODE currentMode = OPERATION_MODE.ENCRYPT;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int aLen = encryptionCesar.sizeAlphabet();
        spinnerKey.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(aLen * -1, aLen)
        );
        spinnerKey.getValueFactory().setValue(encryptionCesar.getKey());

        spinnerKey.valueProperty().addListener((obs, oldValue, newValue) -> encryptionCesar.setKey((int) newValue));

        ToggleGroup groupLangAlpha = new ToggleGroup();
        radioLangEn.setSelected(true);
        radioLangEn.setUserData("en");
        radioLangEn.setToggleGroup(groupLangAlpha);

        radioLangUa.setUserData("ua");
        radioLangUa.setToggleGroup(groupLangAlpha);

        groupLangAlpha.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            encryptionCesar.switchAlphabet(newVal.getUserData().toString());
            analyseDecryption.switchLangList(newVal.getUserData().toString());
        });

        ToggleGroup selectMode = new ToggleGroup();
        modeEncrypt.setSelected(true);
        modeEncrypt.setUserData(OPERATION_MODE.ENCRYPT);
        modeEncrypt.setToggleGroup(selectMode);

        modeDecrypt.setUserData(OPERATION_MODE.DECRYPT);
        modeDecrypt.setToggleGroup(selectMode);

        modeBruteForce.setUserData(OPERATION_MODE.BRUTE_FORCE);
        modeBruteForce.setToggleGroup(selectMode);

        selectMode.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            this.currentMode = OPERATION_MODE.valueOf(newVal.getUserData().toString());
            if (this.currentMode == OPERATION_MODE.BRUTE_FORCE) {
                spinnerKey.setDisable(true);
            } else {
                spinnerKey.setDisable(false);
            }
        });

    }

    @FXML
    protected void onSelectFileButtonClick(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        Node node = (Node) event.getSource();

        if(cacheDirectory != null)
            fileChooser.setInitialDirectory(cacheDirectory);

        currentFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        if(currentFile != null) {
            cacheDirectory = currentFile.getParentFile();
            labelInfoFile.setText(currentFile.getName());
            clearLog();
            addLog("Selected file for process: " + currentFile.getPath());

            btnExecute.setDisable(false);
        } else {
            btnExecute.setDisable(true);
            labelInfoFile.setText("no selected");
        }
    }
    @FXML
    protected void onExecuteButtonClick() throws IOException {

        if (currentMode != OPERATION_MODE.BRUTE_FORCE) {
            doEncryption();
        } else {
            doBruteForce();
        }

        addLog("Finished!");
    }

    protected void doEncryption() throws IOException {

        try (FileReader fileReader = new FileReader(currentFile);
             BufferedReader reader = new BufferedReader(fileReader);
             FileWriter writer = new FileWriter(createFile())) {
            while (reader.ready()) {
                String data = reader.readLine();
                if (data.length() > 0) {
                    String newData = encryptionCesar.encrypt(data);
                    writer.write(newData);
                }
            }
        }
    }

    protected void doBruteForce() throws IOException {

        try (FileReader fileReader = new FileReader(currentFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            StringBuilder stringForBruteForce = new StringBuilder();
            while (reader.ready()) {
                String data = reader.readLine();
                if (data.length() > 0) {
                    stringForBruteForce.append(data).append(" ");
                }
            }

            if(stringForBruteForce.length() > 0) {
                addLog("Executing BruteForce...");
                btnExecute.setDisable(true);
                runBruteForce(stringForBruteForce.toString());
                btnExecute.setDisable(false);
            }
        }
    }

    protected void runBruteForce(String data) {
        int maxRange = encryptionCesar.sizeAlphabet();
        int minRange = maxRange * -1;
        for (int i = minRange; i < maxRange; i++) {
            encryptionCesar.setKey(i);
            String newData = encryptionCesar.encrypt(data);

            analyseDecryption.analyse(newData, i);
        }

        if(analyseDecryption.keyWasFind()) {
            addLog("Decryption key value found: " + analyseDecryption.getKey());

            spinnerKey.getValueFactory().setValue(analyseDecryption.getKey());
        } else {
            addLog("Decryption key value not found!");
        }

    }

    protected String createFile () throws IOException {
        String oldName = currentFile.getName().substring(0, currentFile.getName().lastIndexOf("."));
        String ext = currentFile.getName().substring(currentFile.getName().lastIndexOf("."));

        String newFileName = oldName + "_" + Instant.now().getEpochSecond() + ext;
        Path newFilePath = Files.createFile(Path.of(currentFile.getParent()).resolve(newFileName));

        addLog("Result will be saved to file: " + newFilePath);

        return newFilePath.toString();
    }

    protected void addLog(String data) {
        StringBuilder stringBuilder = new StringBuilder(txtAreaLog.getText());
        stringBuilder.append("\n" + data);
        txtAreaLog.setText(stringBuilder.toString());
    }

    protected void clearLog() {
        txtAreaLog.setText("");
    }
}