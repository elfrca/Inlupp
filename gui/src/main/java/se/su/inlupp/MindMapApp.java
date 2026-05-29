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

public class MindMapApp extends Application {
    private Pane workspace;
    private List<IdeaNode> nodes = new ArrayList<>();

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        workspace = new Pane();

        Button addIdeaButton = new Button("Lägg till idé");
        Button removeIdeaButton = new Button("Ta bort idé");


        ToolBar toolBar = new ToolBar(addIdeaButton, removeIdeaButton);

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

                nodes.add(node);

                workspace.getChildren().add(node);
            }
        });

        removeIdeaButton.setOnAction(event -> {
            for (IdeaNode node : nodes) {
                if (node.isSelected()) {
                    node = null;
                    break;
                }
            }
        });

        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Mind Map");
        stage.setScene(scene);
        stage.show();


    }



    public static void main(String[] args) {
        launch(args);
    }

}
