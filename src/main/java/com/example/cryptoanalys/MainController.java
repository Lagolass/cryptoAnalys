package com.example.cryptoanalys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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

    private enum OPERATION_MODE {
        ENCRYPT,
        DECRYPT,
        BRUTE_FORCE
    }

    private OPERATION_MODE currentMode = OPERATION_MODE.ENCRYPT;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int aLen = EncryptionCesar.ALPHABET_ENGLISH.size();
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

        try (FileReader fileReader = new FileReader(currentFile);
             BufferedReader reader = new BufferedReader(fileReader);
             FileWriter writer = new FileWriter(createFile())) {
            while (reader.ready()) {
                String data = reader.readLine();
                if (data.length() > 0) {
                    if (currentMode != OPERATION_MODE.BRUTE_FORCE) {
                        String newData = encryptionCesar.encrypt(data);
                        writer.write(newData);
                    }
                }
            }

            addLog("Finished!");
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