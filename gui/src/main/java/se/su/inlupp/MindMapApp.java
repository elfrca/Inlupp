package se.su.inlupp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Line;
import javafx.scene.control.Alert;

public class MindMapApp extends Application {
    private Pane workspace;
    private List<IdeaNode> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        workspace = new Pane();

        Button addIdeaButton = new Button("Lägg till idé");
        Button removeIdeaButton = new Button("Ta bort idé");
        Button connectButton = new Button("Koppla två idéer");
        Button removeConnection = new Button("Ta bort en koppling");


        ToolBar toolBar = new ToolBar(addIdeaButton, removeIdeaButton, connectButton, removeConnection);

        root.setTop(toolBar);
        root.setCenter(workspace);

        addIdeaButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("Ny idé");
            dialog.setHeaderText("Skapa en ny idé");
            dialog.setContentText("Namn");

            Optional<String> result = dialog.showAndWait();

            if( result.isPresent()) {
                String ideaName = result.get();

                IdeaNode node = new IdeaNode(ideaName, 100, 100);

                node.setMoveListener(this::updateConnections);

                nodes.add(node);

                workspace.getChildren().add(node);
            }
            deselectAllNodes();
        });

        removeIdeaButton.setOnAction(event -> {
            List<IdeaNode> selectedNodes = getSelectedNodes();
            if (selectedNodes.size() != 1) {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Fel antal noder valda");
                alert.setContentText("Du måste markera exakt en idé att ta bort");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }
            IdeaNode nodeToRemove = selectedNodes.get(0);


            List<Connection> connectionsToRemove = new ArrayList<>();

            for(Connection connection : connections) {
                if(connection.getFirst() == nodeToRemove || connection.getSecond() == nodeToRemove) {
                    connectionsToRemove.add(connection);
                }
            }
            for(Connection connection : connectionsToRemove) {
                workspace.getChildren().remove(connection.getLine());
                connections.remove(connection);
            }
            workspace.getChildren().remove(nodeToRemove);
            nodes.remove(nodeToRemove);
            deselectAllNodes();
        });

        connectButton.setOnAction(event -> {

            List<IdeaNode> selectedNodes = getSelectedNodes();

            if (selectedNodes.size() != 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Fel antal noder valda");
                alert.setContentText("Du måste markera exakt två idéer");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }
            IdeaNode first = selectedNodes.get(0);
            IdeaNode second = selectedNodes.get(1);


            if(getConnection(first, second) != null) {  //använder hjälpmetod
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Koppling finns redan");
                alert.setContentText("Dessa idéer är redan kopplade");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }

            Line line = new Line(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());

            Connection connection = new Connection(first, second, line);
            connections.add(connection);

            workspace.getChildren().add(0, line);
            deselectAllNodes();


        });

        removeConnection.setOnAction( event -> {
            List<IdeaNode> selectedNodes = getSelectedNodes();
            if (selectedNodes.size() != 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Fel antal noder valda");
                alert.setContentText("Du måste markera exakt två idéer");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }
            IdeaNode first = selectedNodes.get(0);
            IdeaNode second = selectedNodes.get(1);

            Connection connection = getConnection(first, second);
            if(connection == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Koppling finns inte");
                alert.setContentText("Dessa idéer är inte kopplade");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }
            workspace.getChildren().remove(connection.getLine());
            connections.remove(connection);
            deselectAllNodes();
        });

        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Mind Map");
        stage.setScene(scene);
        stage.show();



    }

    private void updateConnections() {
        for(Connection connection : connections) {
            connection.update();
        }
    }

    private Connection getConnection(IdeaNode first, IdeaNode second) {

        for (Connection connection : connections) {
            if ((connection.getFirst() == first && connection.getSecond() == second)
                    ||
                    (connection.getFirst() == second && connection.getSecond() == first)) {
                return connection;
            }
        }
        return null;
    }

    private List<IdeaNode> getSelectedNodes() {

        List<IdeaNode> selectedNodes = new ArrayList<>();

        for (IdeaNode node : nodes) {
            if (node.isSelected()) {
                selectedNodes.add(node);
            }
        }

        return selectedNodes;
    }

    public void deselectAllNodes() {
        for(IdeaNode node : nodes) {
            node.deselect();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
