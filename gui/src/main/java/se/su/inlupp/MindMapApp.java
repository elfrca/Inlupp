package se.su.inlupp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Line;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.Scanner;

public class MindMapApp extends Application {
    private Pane backgroundLayer;
    private Pane connectionLayer;
    private Pane nodeLayer;
    private ImageView backgroundImage;
    private List<IdeaNode> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private MindMapModel model = new MindMapModel();

    private boolean unsavedChanges = false;
    private String imagePath;

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
        Button loadImageButton = new Button("Byt bild");
        Button saveButton = new Button("Spara");
        Button loadButton = new Button("Ladda upp");

        ToolBar toolBar = new ToolBar(addIdeaButton, removeIdeaButton, connectButton, removeConnection, findPathButton, loadImageButton, saveButton, loadButton);

        root.setBottom(toolBar);
        StackPane rootCenter = new StackPane(backgroundLayer, connectionLayer, nodeLayer);

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
                unsavedChanges = true;
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
            unsavedChanges = true;
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

            Line line = new Line(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());

            Connection connection = new Connection(first, second, line);

            connections.add(connection);


            model.connectIdeas(first.getIdeaName(), second.getIdeaName(), relationName, weight);
            unsavedChanges = true;

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
            model.disconnectIdeas(first.getIdeaName(), second.getIdeaName());
            unsavedChanges = true;
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

            Path<String> path = model.findPath(first.getIdeaName(), second.getIdeaName());

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

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                Image img = new Image(file.toURI().toString());

                backgroundImage.setImage(img);
                imagePath = file.getAbsolutePath();
                unsavedChanges = true;
                backgroundImage.toBack();
            }
        });

        saveButton.setOnAction(e -> {

            FileChooser chooser = new FileChooser();

            File file = chooser.showSaveDialog(stage);

            if (file == null) {
                return;
            }

            try {
                save(file);
            } catch (IOException ex) {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setHeaderText("Kunde inte spara");

                alert.showAndWait();
            }
        });

        loadButton.setOnAction(e -> {

            if (unsavedChanges) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Osparade ändringar");
                alert.setHeaderText("Det finns osparade ändringar");
                alert.setContentText("Vill du fortsätta utan att spara?");

                Optional<ButtonType> result =
                        alert.showAndWait();

                if (result.isEmpty()
                        || result.get() != ButtonType.OK) {
                    return;
                }
            }

            FileChooser chooser = new FileChooser();

            File file = chooser.showOpenDialog(stage);

            if (file == null) {
                return;
            }

            try {
                load(file);
            } catch (IOException ex) {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setHeaderText("Kunde inte ladda filen");

                alert.showAndWait();
            }
        });




        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Mind Map");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {

            if (!unsavedChanges) {
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Osparade ändringar");
            alert.setHeaderText("Det finns osparade ändringar");
            alert.setContentText("Vill du verkligen avsluta?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                event.consume();
            }
        });
        stage.show();
    }

    private void updateConnections() {
        for(Connection connection : connections) {
            connection.update();
        }
    }

    private Connection getConnection(IdeaNode first, IdeaNode second) {
        for (Connection connection : connections) {

            if ((connection.getFirst() == first && connection.getSecond() == second) ||
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

    private void save(File file) throws IOException {

        PrintWriter writer = new PrintWriter(file);

        for (IdeaNode node : nodes) {
            writer.println("NODE;" + node.getIdeaName() + ";" + node.getLayoutX() + ";" + node.getLayoutY());
        }

        for (String idea : model.getIdeas()) {

            for (Edge<String> edge : model.getConnections(idea)) {

                if (idea.compareTo(edge.getDestination()) < 0) {

                    writer.println("EDGE;" + idea + ";" + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight());
                }
            }
        }
        if (imagePath != null) {

            writer.println("IMAGE;" + imagePath);
        }
        writer.close();
        unsavedChanges = false;
    }
    private void load(File file) throws IOException {

        nodes.clear();
        connections.clear();

        nodeLayer.getChildren().clear();
        connectionLayer.getChildren().clear();

        model = new MindMapModel();
        backgroundImage.setImage(null);
        imagePath = null;

        List<String> edgeLines = new ArrayList<>();

        Scanner scanner = new Scanner(file);
        String loadedImagePath = null;

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            String[] parts = line.split(";");

            if (parts[0].equals("NODE")) {

                String name = parts[1];

                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);

                IdeaNode node = new IdeaNode(name, x, y);

                node.setMoveListener(this::updateConnections);

                nodes.add(node);

                nodeLayer.getChildren().add(node);

                model.addIdea(name);

            } else if (parts[0].equals("EDGE")) {
                edgeLines.add(line);

            } else if (parts[0].equals("IMAGE")) {

                loadedImagePath = parts[1];
            }
        }

        scanner.close();

        if (loadedImagePath != null) {

            File imageFile = new File(loadedImagePath);

            if (imageFile.exists()) {

                Image image = new Image(imageFile.toURI().toString());

                backgroundImage.setImage(image);

                imagePath = loadedImagePath;
            }
        }

        for (String edgeLine : edgeLines) {

            String[] parts = edgeLine.split(";");

            String from = parts[1];
            String to = parts[2];
            String relation = parts[3];
            int weight = Integer.parseInt(parts[4]);

            model.connectIdeas(from, to, relation, weight);

            IdeaNode first = null;
            IdeaNode second = null;

            for (IdeaNode node : nodes) {

                if (node.getIdeaName().equals(from)) {
                    first = node;
                }

                if (node.getIdeaName().equals(to)) {
                    second = node;
                }
            }

            if (first != null && second != null) {

                Line line = new Line(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());

                Connection connection = new Connection(first, second, line);

                connections.add(connection);

                connectionLayer.getChildren().add(line);
            }
        }

        unsavedChanges = false;
    }
    public static void main(String[] args) {
        launch(args);
    }

}