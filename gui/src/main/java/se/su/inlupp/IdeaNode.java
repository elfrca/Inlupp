package se.su.inlupp;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class IdeaNode extends StackPane {

    private final String ideaName;
    private final Circle circle;
    private final Label label;

    private double dragOffsetX;
    private double dragOffsetY;

    private boolean selected;

    private Runnable moveListener; //Elias lade till

    public IdeaNode(String ideaName, double x, double y) {
        this.ideaName = ideaName;
        this.selected = false;

        circle = new Circle(40);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);

        label = new Label(ideaName);

        getChildren().addAll(circle, label);

        setLayoutX(x);
        setLayoutY(y);

        makeDraggable();
        makeSelectable();
    }

    private void makeDraggable() {

        setOnMousePressed(event -> {
            dragOffsetX = event.getSceneX() - getLayoutX();
            dragOffsetY = event.getSceneY() - getLayoutY();
        });

        setOnMouseDragged(event -> {
            setLayoutX(event.getSceneX() - dragOffsetX);
            setLayoutY(event.getSceneY() - dragOffsetY);

            if(moveListener != null) {//Elias lade till
                moveListener.run();
            }
        });


    }

    private void makeSelectable() {

        setOnMouseClicked(event -> {
            selected = !selected;

            if (selected) {
                circle.setStroke(Color.RED);
                circle.setStrokeWidth(3);
            } else {
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1);
            }

            event.consume();
        });
    }

    public String getIdeaName() {
        return ideaName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void deselect() {
        selected = false;
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);
    }

    public double getCenterX() {
        return getLayoutX() + circle.getRadius();
    }

    public double getCenterY() {
        return getLayoutY() + circle.getRadius();
    }

    public void setMoveListener(Runnable moveListener) { //Elias lade till
        this.moveListener = moveListener;
    }


}