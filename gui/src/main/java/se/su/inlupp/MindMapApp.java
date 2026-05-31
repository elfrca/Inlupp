package se.su.inlupp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Line;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class MindMapApp extends Application {
    private Pane backgroundLayer;
    private Pane connectionLayer;
    private Pane nodeLayer;
    private ImageView backgroundImage;
    private List<IdeaNode> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private MindMapModel model = new MindMapModel();

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        backgroundLayer = new Pane();
        connectionLayer = new Pane();
        nodeLayer = new Pane();

        backgroundImage = new ImageView();

        backgroundImage.fitWidthProperty().bind(backgroundLayer.widthProperty());
        backgroundImage.fitHeightProperty().bind(backgroundLayer.heightProperty());
        backgroundImage.setPreserveRatio(false);

        backgroundLayer.getChildren().add(backgroundImage);

        MenuBar menuBar = new MenuBar();
        Menu algorithMenu = new Menu("Algoritm");

        MenuItem dfsItem = new MenuItem("DFS");
        MenuItem bfsItem = new MenuItem("BFS");

        dfsItem.setOnAction(e -> model.useDFS());
        bfsItem.setOnAction(e -> model.useBFS());

        algorithMenu.getItems().addAll(dfsItem, bfsItem);
        menuBar.getMenus().add(algorithMenu);

        root.setTop(menuBar);

        Button addIdeaButton = new Button("Lägg till idé");
        Button removeIdeaButton = new Button("Ta bort idé");
        Button connectButton = new Button("Koppla två idéer");
        Button removeConnection = new Button("Ta bort en koppling");
        Button findPathButton = new Button("Hitta väg");
        Button loadImageButton = new Button("Ladda bild");

        ToolBar toolBar = new ToolBar(addIdeaButton, removeIdeaButton, connectButton, removeConnection, findPathButton, loadImageButton);

        root.setBottom(toolBar);
        StackPane rootCenter = new StackPane(
                backgroundLayer,
                connectionLayer,
                nodeLayer
        );

        root.setCenter(rootCenter);

        addIdeaButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("Ny idé");
            dialog.setHeaderText("Skapa en ny idé");
            dialog.setContentText("Namn");

            Optional<String> result = dialog.showAndWait();

            if( result.isPresent()) {
                String ideaName = result.get().trim();
                for (IdeaNode node : nodes) { //Kollar om samma namn
                    if (node.getIdeaName().equals(ideaName)) {

                        Alert alert = new Alert(Alert.AlertType.ERROR);

                        alert.setTitle("Fel");
                        alert.setHeaderText("Idén finns redan");
                        alert.setContentText("Det finns redan en idé med det namnet");

                        alert.showAndWait();
                        return;
                    }
                }

                if (ideaName.isEmpty()) { //Kollar om namn finns
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    alert.setTitle("Fel");
                    alert.setHeaderText("Ogiltigt namn");
                    alert.setContentText("Idén måste ha ett namn");

                    alert.showAndWait();
                    return;
                }

                IdeaNode node = new IdeaNode(ideaName, 100, 100);

                node.setMoveListener(this::updateConnections);

                nodes.add(node);

                nodeLayer.getChildren().add(node);
                model.addIdea(ideaName);
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
                connectionLayer.getChildren().remove(connection.getLine());
                connections.remove(connection);
            }
            model.removeIdea(nodeToRemove.getIdeaName());
            nodeLayer.getChildren().remove(nodeToRemove);
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
            TextInputDialog relationDialog = new TextInputDialog();

            relationDialog.setTitle("Ny koppling");
            relationDialog.setHeaderText("Ange relation");
            relationDialog.setContentText("Relationsnamn:");

            Optional<String> relationResult = relationDialog.showAndWait();

            if (relationResult.isEmpty()) {
                deselectAllNodes();
                return;
            }

            String relationName = relationResult.get().trim();

            if (relationName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Ogiltigt namn");
                alert.setContentText("Relationsnamnet får inte vara tomt");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }

            TextInputDialog weightDialog = new TextInputDialog();

            weightDialog.setTitle("Ny koppling");
            weightDialog.setHeaderText("Ange vikt");
            weightDialog.setContentText("Vikt:");

            Optional<String> weightResult = weightDialog.showAndWait();

            if (weightResult.isEmpty()) {
                deselectAllNodes();
                return;
            }

            int weight;

            try {
                weight = Integer.parseInt(weightResult.get());

                if (weight < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Ogiltig vikt");
                alert.setContentText("Vikten måste vara ett positivt heltal");

                alert.showAndWait();
                deselectAllNodes();
                return;
            }

            Line line = new Line(
                    first.getCenterX(),
                    first.getCenterY(),
                    second.getCenterX(),
                    second.getCenterY()
            );

            Connection connection = new Connection(first, second, line);

            connections.add(connection);


            model.connectIdeas(
                    first.getIdeaName(),
                    second.getIdeaName(),
                    relationName,
                    weight
            );

            connectionLayer.getChildren().add(line);
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
            connectionLayer.getChildren().remove(connection.getLine());
            model.disconnectIdeas(
                    first.getIdeaName(),
                    second.getIdeaName()
            );
            connections.remove(connection);
            deselectAllNodes();
        });

        findPathButton.setOnAction(event -> {
            List<IdeaNode> selectedNodes = getSelectedNodes();

            if (selectedNodes.size() != 2) {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fel");
                alert.setHeaderText("Fel antal noder valda");
                alert.setContentText("Du måste markera exakt två idéer");

                alert.showAndWait();
                return;
            }

            IdeaNode first = selectedNodes.get(0);
            IdeaNode second = selectedNodes.get(1);

            Path<String> path = model.findPath(
                    first.getIdeaName(),
                    second.getIdeaName()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Hittad väg");

            if (path == null) {
                alert.setContentText("Ingen väg hittades");
            } else {
                alert.setContentText(path.toString());
            }

            alert.showAndWait();
        });

        loadImageButton.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Images",
                            "*.png",
                            "*.jpg",
                            "*.jpeg"
                    )
            );

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                Image img = new Image(file.toURI().toString());

                backgroundImage.setImage(img);
                backgroundImage.toBack();
            }
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