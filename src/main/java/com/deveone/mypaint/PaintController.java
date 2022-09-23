package com.deveone.mypaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;

import javax.imageio.ImageIO;
import java.io.File;

import static com.deveone.mypaint.Tools.*;

public class PaintController {
    @FXML
    private ToggleButton playButton;
    @FXML
    private Canvas canvas;
    @FXML
    private Spinner<Integer> brushSize;
    @FXML
    private ToggleButton eraserTool;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    ToggleGroup Tools;
    DrawingHelper drawingHelper;
    double scale = 0.7;

    public void initialize() {
        brushSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 15));

        drawingHelper = new DrawingHelper(canvas, brushSize.getValue(), colorPicker.getValue());

        brushSize.valueProperty().addListener((obs, oldValue, newValue) -> drawingHelper.setBrushSize(newValue));
        colorPicker.setOnAction(o -> drawingHelper.setColor(colorPicker.getValue()));

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.addEventFilter(ScrollEvent.ANY, o -> {
            o.consume();
            double zoomFactor = Math.exp(o.getTextDeltaY() * 0.09);
            scale *= zoomFactor;
            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
        });
    }

    public void onToolChanged() {
        ToggleButton selected = (ToggleButton) Tools.getSelectedToggle();

        if (selected == null)
            return;

        drawingHelper.onToolChanged(selected.getId());
    }

    public void onSave() {
        try {
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("savedPaint.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void onLoad() {
    }

    public void onClear() {
    }
}