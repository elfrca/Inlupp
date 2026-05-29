package se.su.inlupp;

import javafx.scene.shape.Line;

public class Connection {
    private IdeaNode first;
    private IdeaNode second;
    private Line line;

    public Connection (IdeaNode first, IdeaNode second, Line line) {
        this.first = first;
        this.second = second;
        this.line = line;
    }

    public void update() {
        line.setStartX(first.getCenterX());
        line.setStartY(first.getCenterY());

        line.setEndX(second.getCenterX());
        line.setEndY(second.getCenterY());
    }
}
