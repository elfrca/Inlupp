package se.su.inlupp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.shape.Line;

import java.util.*;

public class MindMapApp extends Application {

    private Pane workspace;

    private MindMapModel model = new MindMapModel();

    private List<IdeaNode> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        workspace = new Pane();

        root.setCenter(workspace);


        MenuBar menuBar = new MenuBar();
        Menu algoMenu = new Menu("Algorithm");

        MenuItem dfs = new MenuItem("DFS");
        MenuItem bfs = new MenuItem("BFS");

        algoMenu.getItems().addAll(dfs, bfs);
        menuBar.getMenus().add(algoMenu);

        root.setTop(menuBar);

        dfs.setOnAction(e -> model.useDFS());
        bfs.setOnAction(e -> model.useBFS());


        Button add = new Button("Add");
        Button remove = new Button("Remove");
        Button connect = new Button("Connect");
        Button findPath = new Button("Find Path");

        ToolBar bar = new ToolBar(add, remove, connect, findPath);
        root.setBottom(bar);

        add.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("Add idea");

            Optional<String> res = d.showAndWait();
            if (res.isEmpty()) return;

            String name = res.get().trim();
            if (name.isEmpty()) return;

            model.addIdea(name);

            IdeaNode node = new IdeaNode(name, 100, 100);
            nodes.add(node);
            workspace.getChildren().add(node);
        });


        remove.setOnAction(e -> {
            List<IdeaNode> selected = getSelected();

            if (selected.size() != 1) return;

            IdeaNode n = selected.get(0);

            model.removeIdea(n.getIdeaName());

            nodes.remove(n);
            workspace.getChildren().remove(n);
        });

        connect.setOnAction(e -> {
            List<IdeaNode> selected = getSelected();

            if (selected.size() != 2) return;

            IdeaNode a = selected.get(0);
            IdeaNode b = selected.get(1);

            TextInputDialog rel = new TextInputDialog();
            rel.setHeaderText("Relation");
            Optional<String> relation = rel.showAndWait();
            if (relation.isEmpty()) return;

            TextInputDialog w = new TextInputDialog();
            w.setHeaderText("Weight");

            int weight;
            try {
                weight = Integer.parseInt(w.showAndWait().orElse("1"));
            } catch (Exception ex) {
                return;
            }

            model.connectIdeas(
                    a.getIdeaName(),
                    b.getIdeaName(),
                    relation.get(),
                    weight
            );

            Line line = new Line(
                    a.getCenterX(), a.getCenterY(),
                    b.getCenterX(), b.getCenterY()
            );

            connections.add(new Connection(a, b, line));
            workspace.getChildren().add(0, line);
        });

        findPath.setOnAction(e -> {
            List<IdeaNode> selected = getSelected();

            if (selected.size() != 2) return;

            IdeaNode a = selected.get(0);
            IdeaNode b = selected.get(1);

            Path<String> path = model.findPath(
                    a.getIdeaName(),
                    b.getIdeaName()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Path Result");
            alert.setContentText(path == null ? "No path found" : path.toString());
            alert.show();
        });

        stage.setScene(new Scene(root, 1000, 700));
        stage.setTitle("Mind Map");
        stage.show();
    }


    private List<IdeaNode> getSelected() {
        List<IdeaNode> sel = new ArrayList<>();
        for (IdeaNode n : nodes) {
            if (n.isSelected()) sel.add(n);
        }
        return sel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}