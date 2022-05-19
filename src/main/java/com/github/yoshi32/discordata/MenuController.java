package com.github.yoshi32.discordata;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MenuController {

    public Button mainButton;

    public void onMiniButtonMouseClicked() {
        Main.stage.setIconified(true);
    }

    public void onExitButtonMouseClicked() {
        Main.stage.close();
    }

    public void onMainButtonDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        File file = dragboard.getFiles().get(0);
        if (file.getName().endsWith(".json")) validFile(file);
        else invalidFile();
    }

    public void onMainButtonMouseClicked() {
        File file = chooseFile();
        if (file == null) invalidFile();
        else validFile(file);

    }

    private void awaitFile() {
        mainButton.setText("Drag'n'Drop or click to select JSON file.");
        mainButton.setDisable(false);
        mainButton.setStyle("-fx-border-color: #7289DA");
    }

    private void invalidFile() {
        mainButton.setStyle("-fx-border-color: #F04747");
        new ScheduledThreadPoolExecutor(1).schedule(() -> Platform.runLater(this::awaitFile), 800, TimeUnit.MILLISECONDS);
    }

    private void invalidFileData() {
        mainButton.setText("Invalid JSON File");
        mainButton.setStyle("-fx-border-color: #F04747");
        new ScheduledThreadPoolExecutor(1).schedule(() -> Platform.runLater(this::awaitFile), 800, TimeUnit.MILLISECONDS);
    }

    private void validFile(File file) {

        mainButton.setStyle("-fx-border-color: #43B581");
        new ScheduledThreadPoolExecutor(1).schedule(() -> mainButton.setStyle("-fx-border-color: #7289DA"), 800, TimeUnit.MILLISECONDS);
        mainButton.setText("Calculating ...");
        mainButton.setDisable(true);

        Discordata.countEvents(file).thenAcceptAsync(map -> Platform.runLater(() -> {
            awaitFile();
            Discordata.writeToFile(saveFile(new SimpleDateFormat("yyyy-MM-dd").format(new Date(map.remove("date_of_request")))), map);
        })).exceptionally(e -> {
            Platform.runLater(this::invalidFileData);
            return null;
        });

    }

    private static File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        return fileChooser.showOpenDialog(Main.stage);
    }

    public static File saveFile(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data File");
        fileChooser.initialFileNameProperty().setValue(fileName + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showSaveDialog(Main.stage);
    }

    public void onMainButtonDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }

}
