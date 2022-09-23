package com.deveone.mypaint;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;

import static com.deveone.mypaint.Tools.*;
import static com.deveone.mypaint.Tools.PAINT;

public class DrawingHelper {
    private final Canvas canvas;
    private final GraphicsContext g;
    private Point2D lastLocalClick;
    private Point2D lastSceneClick;
    private Point2D firstDragClick;
    private Tools currentTool = Tools.PAINT;
    private Color currColor;
    private Image snapshot;

    public DrawingHelper(Canvas canvas, double brushSize, Color currColor) {
        this.canvas = canvas;
        this.currColor = currColor;
        g = canvas.getGraphicsContext2D();

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setLineWidth(brushSize);
        g.setLineCap(StrokeLineCap.ROUND);
        initPaintTools();

        setColor(currColor);
    }

    private void initPaintTools() {
        EventHandler<MouseEvent> onCanvasMoving = o -> {
            Point2D currSceneClick = new Point2D(o.getSceneX(), o.getSceneY());

            if (lastSceneClick != null) {
                if (o.getButton().equals(MouseButton.MIDDLE)) {
                    canvas.setLayoutX(canvas.getLayoutX() - lastSceneClick.subtract(currSceneClick).getX());
                    canvas.setLayoutY(canvas.getLayoutY() - lastSceneClick.subtract(currSceneClick).getY());
                }
            }

            lastSceneClick = currSceneClick;
        };

        EventHandler<MouseEvent> onPaintAndErase = o -> {
            if (!(currentTool.equals(PAINT) || currentTool.equals(ERASER)))
                return;

            Point2D currLocalClick = new Point2D(o.getX(), o.getY());

            if (lastLocalClick != null) {
                if (o.getButton().equals(MouseButton.PRIMARY)) {
                    if (currentTool.equals(Tools.ERASER))
                        g.setStroke(Color.WHITE);

                    g.strokeLine(lastLocalClick.getX(), lastLocalClick.getY(), currLocalClick.getX(), currLocalClick.getY());
                }
            }

            lastLocalClick = currLocalClick;
        };

        EventHandler<MouseEvent> onLine = o -> {
            if (!currentTool.equals(LINE))
                return;

            Point2D currLocalClick = new Point2D(o.getX(), o.getY());

            if (lastLocalClick != null) {
                if (o.getButton().equals(MouseButton.PRIMARY)) {
                    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    g.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());

                    g.strokeLine(firstDragClick.getX(), firstDragClick.getY(), currLocalClick.getX(), currLocalClick.getY());
                }
            }

            lastLocalClick = currLocalClick;
        };

        EventHandler<MouseEvent> onRectangle = o -> {
            if (!currentTool.equals(RECTANGLE))
                return;

            Point2D currLocalClick = new Point2D(o.getX(), o.getY());

            if (lastLocalClick != null) {
                if (o.getButton().equals(MouseButton.PRIMARY)) {
                    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    g.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());

                    double width = Math.abs(currLocalClick.subtract(firstDragClick).getX());
                    double height = Math.abs(currLocalClick.subtract(firstDragClick).getY());

                    double startPosX = Math.min(firstDragClick.getX(), currLocalClick.getX());
                    double startPosY = Math.min(firstDragClick.getY(), currLocalClick.getY());

                    g.strokeRect(startPosX, startPosY, width, height);
                }
            }

            lastLocalClick = currLocalClick;
        };

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, onCanvasMoving);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, onPaintAndErase);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, onLine);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, onRectangle);

        canvas.setOnMouseReleased(o -> {
            lastLocalClick = null;
            lastSceneClick = null;
            firstDragClick = null;
            snapshot = null;
        });

        canvas.setOnMousePressed(o -> {
            if (o.getButton().equals(MouseButton.PRIMARY)) {
                if (currentTool.equals(PAINT) || currentTool.equals(ERASER)) {
                    double size = g.getLineWidth();
                    g.fillOval(o.getX() - size / 2, o.getY() - size / 2, size, size);
                } else {
                    firstDragClick = new Point2D(o.getX(), o.getY());
                    snapshot = canvas.snapshot(null, null);
                }
            }
        });
    }

    public void setColor(Color newColor) {
        currColor = newColor;
        Color color = currentTool.equals(Tools.ERASER) ? Color.WHITE : currColor;
        g.setStroke(color);
        g.setFill(color);
    }

    public void setBrushSize(double brushSize) {
        g.setLineWidth(brushSize);
    }

    public void onToolChanged(String toolId) {
        Tools selectedTool;

        switch (toolId) {
            case "lineTool" -> {
                selectedTool = LINE;
                initPaintTools();
            }
            case "rectangleTool" -> selectedTool = RECTANGLE;
            case "circleTool" -> selectedTool = CIRCLE;
            case "triangleTool" -> selectedTool = TRIANGLE;
            case "rhombusTool" -> selectedTool = RHOMBUS;
            case "ovalTool" -> selectedTool = OVAL;
            case "eraserTool" -> selectedTool = ERASER;
            default -> selectedTool = PAINT;
        }

        initPaintTools();

        this.currentTool = selectedTool;

        setColor(currColor);
    }
}
