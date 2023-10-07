package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UCDiagram extends Application {
    private List<AnchorPane> Actors = new ArrayList<>();
    private List<CustomLine> existLines = new ArrayList<>();
    private List<CustomLine> selectedLines = new ArrayList<>();
    private List<CustomLine> connections = new ArrayList<>();
    List<AnchorPane> selectedActors = new ArrayList<>();
    List<AnchorPane> existActors = new ArrayList<>();
    List<AnchorPane> existUsecase = new ArrayList<>();
    List<String> ucSenariosName = new ArrayList<>();
    List<String> ucSenarios = new ArrayList<>();
    int ActorCount = 0;
    int usecaseCount = 0;


    @Override
    public void start(Stage stage) {

        try (Connection con = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
            Statement st = con.createStatement();
            String query = "DELETE from permissions";
            String query2 = "DELETE from actors";
            String query3 = "DELETE from usecases";
            query = String.format(query);
            st.execute(query);
            st.execute(query2);
            st.execute(query3);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        AnchorPane mainRoot = new AnchorPane();
        Scene scene = new Scene(mainRoot, 1200, 750);


        Pane roott = new Pane();
        roott.setPrefWidth(168);
        roott.setPrefHeight(750);
        roott.setStyle("-fx-background-color: #eac4d5");
        mainRoot.getChildren().add(roott);
        AnchorPane.setTopAnchor(roott, 0.0);
        AnchorPane.setLeftAnchor(roott, 0.0);


        Pane root = new Pane();
        mainRoot.getChildren().add(root);
        root.setStyle("-fx-background-color: #ffffff");
        root.setPrefWidth(1032);
        root.setPrefHeight(750);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 168.0);


        Button addActorButton = new Button("add actor");
        addActorButton.setStyle("-fx-background-color:white");
        addActorButton.setPrefWidth(148);
        mainRoot.getChildren().add(addActorButton);
        AnchorPane.setTopAnchor(addActorButton, 10.0);
        AnchorPane.setLeftAnchor(addActorButton, 10.0);


        Button addUseCaseBtn = new Button("Add Use Case");
        addUseCaseBtn.setStyle("-fx-background-color:white");
        addUseCaseBtn.setPrefWidth(148);
        mainRoot.getChildren().add(addUseCaseBtn);

        AnchorPane.setTopAnchor(addUseCaseBtn, 50.0);
        AnchorPane.setLeftAnchor(addUseCaseBtn, 10.0);

        Button connectButton = new Button("Connect");
        connectButton.setStyle("-fx-background-color:white");
        connectButton.setPrefWidth(148);
        mainRoot.getChildren().add(connectButton);

        AnchorPane.setTopAnchor(connectButton, 90.0);
        AnchorPane.setLeftAnchor(connectButton, 10.0);

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color:white");
        removeButton.setPrefWidth(148);
        mainRoot.getChildren().add(removeButton);

        AnchorPane.setTopAnchor(removeButton, 130.0);
        AnchorPane.setLeftAnchor(removeButton, 10.0);

        Button removeAllButton = new Button("Remove All");
        removeAllButton.setStyle("-fx-background-color:white");
        removeAllButton.setPrefWidth(148);
        mainRoot.getChildren().add(removeAllButton);

        AnchorPane.setTopAnchor(removeAllButton, 170.0);
        AnchorPane.setLeftAnchor(removeAllButton, 10.0);


        ComboBox<String> language = new ComboBox<String>();
        language.setPromptText("Language");
        language.getItems().add("English");
        language.getItems().add("Persian");
        language.setStyle("-fx-background-color:white");
        language.setPrefWidth(148);


        language.setOnAction(e -> {
            if (language.getValue() == "Persian") {
                stage.hide();
                Stage s = new Stage();
                try {
                    new PersianUcDiagram().start(s);
                } catch (Exception en) {
                    en.printStackTrace();
                }
            }
        });
        mainRoot.getChildren().add(language);
        AnchorPane.setTopAnchor(language, 210.0);
        AnchorPane.setLeftAnchor(language, 10.0);


//code generation button
        Button saveButton = new Button("Generate Code");
        saveButton.setFont(Font.font("System", FontWeight.BOLD, 12));
        saveButton.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #54c291; -fx-border-width: 1px; -fx-text-fill:#54c291");
        saveButton.setPrefWidth(158);
        mainRoot.getChildren().add(saveButton);
        AnchorPane.setTopAnchor(saveButton, 700.0);
        AnchorPane.setLeftAnchor(saveButton, 1012.0);

        Rectangle rec = new Rectangle(130, 30);
        rec.setArcWidth(5);
        rec.setArcHeight(5);
        saveButton.setShape(rec);
        saveButton.setOnMouseEntered(mouseevent -> {
            saveButton.setFont(Font.font("System", FontWeight.BOLD, 12));
            saveButton.setStyle("-fx-background-color:#8cd6b5;  -fx-border-color: #8cd6b5; -fx-border-width: 1px; -fx-text-fill:white");
        });
        saveButton.setOnMouseExited(mouseEvent -> {
            saveButton.setFont(Font.font("System", FontWeight.BOLD, 12));
            saveButton.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #54c291; -fx-border-width: 1px; -fx-text-fill:#54c291");
        });

        saveButton.setOnMouseClicked(mouseEvent -> {

            String filePath = "";
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setDialogTitle("Select a folder");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                filePath = selectedFolder.getAbsolutePath();

            } else {
                System.out.println("No folder selected.");

            }


            for (int i = 0; i < ucSenarios.size(); i++) {
                File ucFile = new File(filePath + "/" + ucSenariosName.get(i).replace(" ", "_") + ".java");

                try {

                    FileWriter writer = new FileWriter(ucFile);
                    writer.write(ucSenarios.get(i));
                    writer.close();
                    System.out.println("Generated code in " + ucFile.getAbsolutePath());
                    DialogPane dialogPane = new DialogPane();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("INFORMATION");
                    alert.setContentText("generated code files path is:" + "\n" + ucFile.getAbsolutePath());
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


        addActorButton.setOnMouseMoved(mouseEvent -> {
            removeAllButton.setUnderline(false);
            addActorButton.setUnderline(true);
            addUseCaseBtn.setUnderline(false);
            connectButton.setUnderline(false);
            removeButton.setUnderline(false);
        });


        addUseCaseBtn.setOnMouseMoved(mouseEvent -> {
            addActorButton.setUnderline(false);
            addUseCaseBtn.setUnderline(true);
            connectButton.setUnderline(false);
            removeButton.setUnderline(false);
            removeAllButton.setUnderline(false);
        });

        connectButton.setOnMouseMoved(mouseEvent -> {
            addActorButton.setUnderline(false);
            addUseCaseBtn.setUnderline(false);
            connectButton.setUnderline(true);
            removeButton.setUnderline(false);
            removeAllButton.setUnderline(false);
        });
        removeAllButton.setOnMouseMoved(mouseEvent -> {
            addActorButton.setUnderline(false);
            addUseCaseBtn.setUnderline(false);
            connectButton.setUnderline(false);
            removeButton.setUnderline(false);
            removeAllButton.setUnderline(true);

        });

        removeButton.setOnMouseMoved(mouseEvent -> {
            addActorButton.setUnderline(false);
            addUseCaseBtn.setUnderline(false);
            connectButton.setUnderline(false);
            removeButton.setUnderline(true);
            removeAllButton.setUnderline(false);
        });


        removeAllButton.setOnMouseClicked(mouseEvent -> {
            root.getChildren().clear();
            try (Connection con = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                Statement st = con.createStatement();
                String query = "DELETE from permissions";
                String query2 = "DELETE from actors";
                String query3 = "DELETE from usecases";
                query = String.format(query);
                st.execute(query);
                st.execute(query2);
                st.execute(query3);
                st.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });


        removeButton.setOnAction(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("WARNING");
            dialog.setContentText("Are you sure you want to remove the selected objects?");
            Button Yes = new Button("Yes");
            Button No = new Button("No");
            AnchorPane rooot = new AnchorPane();
            rooot.getChildren().add(dialog.getDialogPane());
            rooot.getChildren().add(Yes);
            rooot.getChildren().add(No);
            AnchorPane.setTopAnchor(Yes, 60.0);
            AnchorPane.setLeftAnchor(Yes, 240.0);
            AnchorPane.setTopAnchor(No, 60.0);
            AnchorPane.setLeftAnchor(No, 320.0);
            Yes.setPrefWidth(70);
            No.setPrefWidth(70);
            dialog.getDialogPane().setStyle(" -fx-background-color: white");
            rooot.setStyle("-fx-background-color: white");
            Scene scene7 = new javafx.scene.Scene(rooot, 400, 100);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene7);
            primaryStage.setTitle("WARNING");

            primaryStage.show();


            Yes.setOnMouseClicked(mouseEvent -> {
                for (CustomLine line : selectedLines) {

                    root.getChildren().remove(line);
                    String actor1 = (String) line.getStartPane();
                    String actor2 = (String) line.getEndPane();

                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "DELETE from permissions  WHERE Actor='%s' AND usecase='%s'";
                        query = String.format(query, actor1, actor2);
                        st.execute(query);
                        st.close();
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                selectedLines.clear();

                for (AnchorPane actor : selectedActors) {

                    root.getChildren().remove(actor);
                    for (CustomLine Line : existLines) {
                        if (Line.getUsecase().equals(actor)) {

                            root.getChildren().remove(Line);
                        }
                        if (Line.getActor().equals(actor)) {
                            root.getChildren().remove(Line);
                        }

                    }

                }
                for (int i = 0; i < selectedActors.size(); i++) {

                    AnchorPane actor1 = selectedActors.get(i);

                    String actor1Name = " ";
                    TextField targetTextField = new TextField();

                    for (javafx.scene.Node node : actor1.getChildren()) {

                        if (node instanceof TextField) {
                            targetTextField = (TextField) node;
                            actor1Name = targetTextField.getText();
                            break;
                        }

                    }
                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "DELETE from actors  WHERE name='%s'";
                        String query2 = "DELETE from usecases  WHERE name='%s'";

                        query = String.format(query, actor1Name);
                        query2 = String.format(query2, actor1Name);

                        st.execute(query);
                        st.execute(query2);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }


                selectedActors.clear();
                primaryStage.hide();
            });

            No.setOnMouseClicked(e -> {
                primaryStage.hide();
            });

        });


        addUseCaseBtn.setOnAction(event -> {

            usecaseCount++;

            AtomicReference<Double> X = new AtomicReference<>((double) 0);
            AtomicReference<Double> Y = new AtomicReference<>((double) 0);
            AnchorPane childAnchor2 = new AnchorPane();
            AtomicBoolean isDragged = new AtomicBoolean(false);
            AtomicBoolean isSelectedBeforeDrag = new AtomicBoolean(false);
            root.getChildren().add(childAnchor2);
            Circle usecase = new Circle();
            childAnchor2.getChildren().add(usecase);
            existUsecase.add(childAnchor2);
            usecase.setRadius(45);
            usecase.setCenterX(48);
            usecase.setCenterY(48);
            usecase.setFill(Color.rgb(255, 255, 255));
            usecase.setStroke(Color.web("#000000"));
            childAnchor2.setLayoutX(100);
            childAnchor2.setLayoutY(100);
            Actors.add(childAnchor2);

            TextField ucText = new TextField();
            ucText.setFont(Font.font("System", FontWeight.BOLD, 12));
            ucText.setText("Use Case" + usecaseCount);
            ucText.setPrefWidth(80);
            ucText.setLayoutX(5);
            ucText.setLayoutY(30);
            ucText.setStyle("-fx-alignment:center; -fx-background-color:transparent; -fx-text-fill: #54c291");

            TextField ucText2 = new TextField();
            ucText2.setFont(Font.font("System", FontWeight.BOLD, 12));
            ucText2.setText("یوزکیس" + usecaseCount);
            ucText2.setPrefWidth(80);
            ucText2.setLayoutX(5);
            ucText2.setLayoutY(45);
            ucText2.setStyle("-fx-alignment:center; -fx-background-color:transparent; -fx-text-fill: #54c291");


            childAnchor2.getChildren().add(ucText);
            childAnchor2.getChildren().add(ucText2);

            try (Connection con = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                Statement st = con.createStatement();
                String query = "INSERT INTO usecases (name,scenario,persianName) values('%S' , '%S','%S')";
                query = String.format(query, ucText.getText(), " ", ucText2.getText());
                st.execute(query);
                st.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            ucText.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE permissions SET usecase='%s' WHERE usecase='%s'";
                        query = String.format(query, t1, s);

                        st.execute(query);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE usecases SET name='%s' WHERE name='%s'";


                        query = String.format(query, t1, s);

                        st.execute(query);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            ucText2.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {


                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE usecases SET persianName='%s' WHERE persianName='%s'";
                        query = String.format(query, t1, s);
                        st.execute(query);
                        st.close();
                        con.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            childAnchor2.setOnMouseDragged(mouseEvent -> {


                childAnchor2.setLayoutX(mouseEvent.getSceneX() - 2.25 * (childAnchor2.getWidth()));
                childAnchor2.setLayoutY(mouseEvent.getSceneY() - (childAnchor2.getHeight() / 2));


                isDragged.set(true);
                if (!selectedActors.contains(childAnchor2)) {
                    childAnchor2.setStyle("-fx-border-color: #c25485; -fx-border-width: 2px;-fx-border-radius: 10px;");
                    selectedActors.add(childAnchor2);
                }
            });

            childAnchor2.setOnMousePressed(mouseEvent -> {

                X.set(mouseEvent.getSceneX() - childAnchor2.getTranslateX());
                Y.set(mouseEvent.getSceneY() - childAnchor2.getTranslateY());

                if (!selectedActors.contains(childAnchor2)) {
                    childAnchor2.setStyle("-fx-border-color: #c25485; -fx-border-width: 2px;-fx-border-radius: 10px;");
                    selectedActors.add(childAnchor2);
                    isSelectedBeforeDrag.set(true);

                } else {
                    selectedActors.remove(childAnchor2);
                    childAnchor2.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
                    isSelectedBeforeDrag.set(false);

                }
            });


            childAnchor2.setOnMouseReleased(mouseEvent -> {
                if (isSelectedBeforeDrag.get() && isDragged.get()) {
                    selectedActors.remove(childAnchor2);
                    childAnchor2.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
                }
                isDragged.set(false);
            });


            childAnchor2.setOnMouseClicked(new EventHandler<MouseEvent>() {


                public void handle(MouseEvent event) {

                    if (event.getClickCount() == 2) {
                        String uc = ucText.getText();
                        Stage stage1 = new Stage();
                        AnchorPane root2 = new AnchorPane();
                        Scene scene1 = new Scene(root2, 600, 400);
                        root2.setStyle("-fx-background-color: #ffffff");
                        stage1.setTitle("Scenario Writing Page");
                        stage1.setScene(scene1);
                        stage1.show();
                        AtomicReference<String> senarios = new AtomicReference<>("");
                        TextArea senarioText = new TextArea();
                        senarioText.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
                            senarios.set(newvalue);
                        });
                        senarioText.setText(senarios.get());
                        senarioText.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #c25485; -fx-border-width: 3px; -fx-text-fill:#953560");
                        senarioText.setPrefWidth(540.0);
                        senarioText.setPrefHeight(250.0);

                        Button ucName = new Button("Use case " + uc + " scenario");
                        ucName.setStyle("-fx-background-color:#54c291");
                        ucName.setFont(Font.font("System", FontWeight.LIGHT, 14));
                        ucName.setPrefWidth(600);
                        ucName.setPrefHeight(30);
                        root2.getChildren().add(ucName);
                        AnchorPane.setTopAnchor(ucName, 0.0);
                        AnchorPane.setLeftAnchor(ucName, 0.0);


                        Label ucLabel = new Label();
                        ucLabel.setPrefWidth(250.0);
                        ucLabel.setPrefHeight(50.0);
                        ucLabel.setText("Note:Write each scenario in an individual line!");

                        Button ucScenarioSubmit = new Button("Submit");

                        ucScenarioSubmit.setFont(Font.font("System", FontWeight.BOLD, 12));
                        ucScenarioSubmit.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #c25485; -fx-border-width: 1px; -fx-text-fill:#c25485");
                        ucScenarioSubmit.setPrefWidth(130);
                        root2.getChildren().add(ucScenarioSubmit);
                        AnchorPane.setTopAnchor(ucScenarioSubmit, 350.0);
                        AnchorPane.setLeftAnchor(ucScenarioSubmit, 438.0);

                        Rectangle rec2 = new Rectangle(130, 30);
                        rec2.setArcWidth(5);
                        rec2.setArcHeight(5);
                        ucScenarioSubmit.setShape(rec2);

                        ucScenarioSubmit.setOnMouseEntered(mouseEvent -> {

                            ucScenarioSubmit.setFont(Font.font("System", FontWeight.BOLD, 12));
                            ucScenarioSubmit.setStyle("-fx-background-color:#eac4d5;  -fx-border-color: #eac4d5; -fx-border-width: 1px; -fx-text-fill:white");


                        });
                        ucScenarioSubmit.setOnMouseExited(mouseEvent -> {
                            stage1.close();
                            ucScenarioSubmit.setFont(Font.font("System", FontWeight.BOLD, 12));
                            ucScenarioSubmit.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #c25485; -fx-border-width: 1px; -fx-text-fill:#c25485");


                        });


                        ucScenarioSubmit.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                            try (Connection con = DriverManager
                                    .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                                Statement st = con.createStatement();
                                String query = "UPDATE usecases SET scenario='%S' where name ='%S'";
                                query = String.format(query, senarioText.getText(), ucText.getText());
                                st.execute(query);
                                st.close();
                                con.close();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            senarios.set(senarioText.getText());
                            ucSenariosName.add(ucText.getText());
                            ucSenarios.add(generateCode(ucText.getText(), senarioText.getText()));

                            ucScenarioSubmit.setFont(Font.font("System", FontWeight.LIGHT, 12));
                            ucScenarioSubmit.setStyle("-fx-background-color:#eac4d5;  -fx-border-color: #eac4d5; -fx-border-width: 1px; -fx-text-fill:#000000");

                        });


                        root2.getChildren().add(senarioText);
                        AnchorPane.setTopAnchor(senarioText, 61.0);
                        AnchorPane.setLeftAnchor(senarioText, 28.0);

                        root2.getChildren().add(ucLabel);
                        AnchorPane.setTopAnchor(ucLabel, 25.0);
                        AnchorPane.setLeftAnchor(ucLabel, 28.0);


                    }

                }
            });


        });


        addActorButton.setOnAction(event -> {


            ActorCount++;

            AtomicReference<Double> X = new AtomicReference<>((double) 0);
            AtomicReference<Double> Y = new AtomicReference<>((double) 0);
            AnchorPane childAnchor = new AnchorPane();
            AtomicBoolean isDragged = new AtomicBoolean(false);
            AtomicBoolean isSelectedBeforeDrag = new AtomicBoolean(false);
            root.getChildren().add(childAnchor);
            existActors.add(childAnchor);
            TextField actorText = new TextField();
            actorText.setFont(Font.font("System", FontWeight.BOLD, 12));
            actorText.setText("Actor" + ActorCount);
            actorText.setPrefWidth(70);
            actorText.setLayoutX(4);
            actorText.setLayoutY(80);
            actorText.setStyle("-fx-alignment:center; -fx-background-color:transparent; -fx-text-fill:#54c291");

            TextField actorText2 = new TextField();
            actorText2.setFont(Font.font("System", FontWeight.BOLD, 12));
            actorText2.setText("اکتور" + ActorCount);
            actorText2.setPrefWidth(70);
            actorText2.setLayoutX(4);
            actorText2.setLayoutY(92);
            actorText2.setStyle("-fx-alignment:center; -fx-background-color:transparent; -fx-text-fill:#54c291");

            childAnchor.setLayoutX(50);
            childAnchor.setLayoutY(50);

            try (Connection con = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                Statement st = con.createStatement();
                String query = "INSERT INTO actors (name,persianName) values('%S','%S')";
                query = String.format(query, actorText.getText(), actorText2.getText());
                st.execute(query);
                st.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            actorText.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE permissions SET Actor='%s' WHERE Actor='%s'";

                        query = String.format(query, t1, s);

                        st.execute(query);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE actors SET name='%s' WHERE name='%s' ";


                        query = String.format(query, t1, s);

                        st.execute(query);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            actorText2.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                    try (Connection con = DriverManager
                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                        Statement st = con.createStatement();
                        String query = "UPDATE actors SET persianName='%s' WHERE persianName='%s' ";


                        query = String.format(query, t1, s);

                        st.execute(query);
                        st.close();
                        con.close();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });


            childAnchor.setOnMouseDragged(mouseEvent -> {

                childAnchor.setLayoutX(mouseEvent.getSceneX() - 2.45 * (childAnchor.getWidth()));
                childAnchor.setLayoutY(mouseEvent.getSceneY() - (childAnchor.getHeight() / 2));

                isDragged.set(true);

                if (!selectedActors.contains(childAnchor)) {
                    childAnchor.setStyle("-fx-border-color: #c25485; -fx-border-width: 2px;-fx-border-radius: 10px;");
                    selectedActors.add(childAnchor);
                }
            });

            childAnchor.setOnMousePressed(mouseEvent -> {


                X.set(mouseEvent.getSceneX() - childAnchor.getTranslateX());
                Y.set(mouseEvent.getSceneY() - childAnchor.getTranslateY());

                if (!selectedActors.contains(childAnchor)) {
                    childAnchor.setStyle("-fx-border-color: #c25485; -fx-border-width: 2px;-fx-border-radius: 10px;");
                    selectedActors.add(childAnchor);
                    isSelectedBeforeDrag.set(true);

                } else {
                    selectedActors.remove(childAnchor);
                    childAnchor.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
                    isSelectedBeforeDrag.set(false);

                }
            });


            childAnchor.setOnMouseReleased(mouseEvent -> {
                if (isSelectedBeforeDrag.get() && isDragged.get()) {
                    selectedActors.remove(childAnchor);
                    childAnchor.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
                }
                isDragged.set(false);
            });


            Image images = new Image("file:src/images/actor1.png");
            ImageView imageViews = new ImageView(images);
            imageViews.setFitHeight(80.0);
            imageViews.setFitWidth(70.0);
            imageViews.setLayoutX(5);
            imageViews.setLayoutY(5);


            childAnchor.getChildren().add(imageViews);
            childAnchor.getChildren().add(actorText);
            childAnchor.getChildren().add(actorText2);

            Actors.add(childAnchor);

        });

        connectButton.setOnAction(event -> {

            if (selectedActors.size() != 2) {


            } else {
                for (int i = 0; i < selectedActors.size() - 1; i++) {

                    AnchorPane actor1 = selectedActors.get(i);
                    AnchorPane actor2 = selectedActors.get(i + 1);

                    String actor1Name = " ";
                    String actor2Name = " ";
                    TextField targetTextField = new TextField();

                    for (javafx.scene.Node node : actor1.getChildren()) {

                        if (node instanceof TextField) {

                            targetTextField = (TextField) node;
                            actor1Name = targetTextField.getText();
                            break;
                        }

                    }
                    for (javafx.scene.Node node : actor2.getChildren()) {
                        if (node instanceof TextField) {
                            targetTextField = (TextField) node;
                            actor2Name = targetTextField.getText();
                            break;
                        }
                    }


                    if (actor1.getLayoutX() <= actor2.getLayoutX()) {
                        double x1 = actor1.getLayoutX() + actor1.getWidth();
                        double x2 = actor2.getLayoutX();
                        double y1 = actor1.getLayoutY() + actor1.getHeight() / 2;
                        double y2 = actor2.getLayoutY() + actor2.getHeight() / 2;

                        CustomLine connection = new CustomLine(actor1, actor2, actor1Name, actor2Name, x1, x2, y1, y2);


                        connection.setStyle("-fx-stroke: #54c291");
                        connection.setStrokeWidth(2);
                        connections.add(connection);
                        root.getChildren().add(connection);
                        existLines.add(connection);


                        connection.setOnMouseEntered(touchEvent -> {
                            connection.setStrokeWidth(6);
                        });
                        connection.setOnMouseExited(touchEvent -> {
                            connection.setStrokeWidth(2);
                        });
                        /////////////////////////////////////////////////////////////////
                        Stage stage2 = new Stage();
                        AnchorPane root2 = new AnchorPane();
                        Scene scene1 = new Scene(root2, 330, 210);
                        root2.setStyle("-fx-background-color: white");


                        RadioButton create = new RadioButton("create");
                        RadioButton read = new RadioButton("read");
                        RadioButton update = new RadioButton("update");
                        RadioButton delete = new RadioButton("delete");

                        create.setFont(Font.font("System", FontWeight.BOLD, 12));

                        create.setStyle("-fx-text-fill:#c25485");
                        read.setFont(Font.font("System", FontWeight.BOLD, 12));
                        read.setStyle("-fx-text-fill:#c25485");
                        update.setFont(Font.font("System", FontWeight.BOLD, 12));
                        update.setStyle("-fx-text-fill:#c25485");
                        delete.setFont(Font.font("System", FontWeight.BOLD, 12));
                        delete.setStyle("-fx-text-fill:#c25485");

                        create.setLayoutX(50);
                        read.setLayoutX(50);
                        update.setLayoutX(50);
                        delete.setLayoutX(50);
                        Label labelP = new Label();

                        labelP.setText("select the permissions of the Actor:");
                        labelP.setStyle("-fx-background-color: #8cd6b5");
                        labelP.setPrefWidth(320.0);
                        labelP.setPrefHeight(25.0);
                        root2.getChildren().add(labelP);
                        AnchorPane.setTopAnchor(labelP, 0.0);
                        AnchorPane.setLeftAnchor(labelP, 5.0);

                        Button accessBtn = new Button("Submit");
                        accessBtn.setPrefWidth(130.0);

                        accessBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
                        accessBtn.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #c25485; -fx-border-width: 1px; -fx-text-fill:#c25485");

                        AnchorPane.setTopAnchor(accessBtn, 160.0);
                        AnchorPane.setLeftAnchor(accessBtn, 175.0);


                        Rectangle rec3 = new Rectangle(130, 30);
                        rec3.setArcWidth(5);
                        rec3.setArcHeight(5);
                        accessBtn.setShape(rec3);


                        create.setLayoutY(40);
                        read.setLayoutY(65);
                        update.setLayoutY(90);
                        delete.setLayoutY(115);

                        root2.getChildren().add(create);
                        root2.getChildren().add(read);
                        root2.getChildren().add(update);
                        root2.getChildren().add(delete);
                        root2.getChildren().add(accessBtn);

                        String finalActor1Name = actor1Name;
                        String finalActor2Name = actor2Name;

                        accessBtn.setOnMouseEntered(mouseEvent -> {

                            accessBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
                            accessBtn.setStyle("-fx-background-color:#eac4d5;  -fx-border-color: #eac4d5; -fx-border-width: 1px; -fx-text-fill:white");


                        });
                        accessBtn.setOnMouseExited(mouseEvent -> {

                            accessBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
                            accessBtn.setStyle("-fx-background-color:#ffffff;  -fx-border-color: #c25485; -fx-border-width: 1px; -fx-text-fill:#c25485");


                        });

                        accessBtn.setOnMouseClicked(Event -> {

                            accessBtn.setFont(Font.font("System", FontWeight.LIGHT, 12));
                            accessBtn.setStyle("-fx-background-color:#eac4d5;  -fx-border-color: #eac4d5; -fx-border-width: 1px; -fx-text-fill:#000000");


                            try (Connection con = DriverManager
                                    .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                                Statement st = con.createStatement();
                                String query = "UPDATE permissions SET `create`='%s' ,`read`='%s',`delete`='%s',`update`='%s' WHERE Actor='%s' AND usecase='%s' ";

                                query = String.format(query, String.valueOf(create.isSelected()), String.valueOf(read.isSelected()), String.valueOf(delete.isSelected()), String.valueOf(update.isSelected()), finalActor1Name, finalActor2Name);

                                st.execute(query);
                                st.close();
                                con.close();
                                stage2.close();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }


                        });


                        try (Connection con = DriverManager
                                .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                            Statement st = con.createStatement();
                            String query = "INSERT INTO permissions (`Actor`,`usecase`,`create`,`read`,`delete`,`update`) values('%S','%S','%S','%S','%S','%S')";
                            query = String.format(query, actor1Name, actor2Name, "c", "r", "d", "u");
                            st.execute(query);
                            st.close();
                            con.close();


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        stage2.setTitle("Permissions");
                        stage2.setScene(scene1);
                        stage2.show();

                        connection.setOnMouseClicked(mouseEvent -> {

                            if (mouseEvent.getClickCount() == 2) {
                                stage2.show();
                                accessBtn.setOnMouseClicked(Event -> {

                                    /////upadte the database

                                    try (Connection con = DriverManager
                                            .getConnection("jdbc:mysql://localhost:3306/mydatabase?user=root")) {
                                        Statement st = con.createStatement();
                                        String query = "UPDATE permissions SET `create`='%s' ,`read`='%s',`delete`='%s',`update`='%s' WHERE Actor='%s'AND usecase='%s'";

                                        query = String.format(query, String.valueOf(create.isSelected()), String.valueOf(read.isSelected()), String.valueOf(delete.isSelected()), String.valueOf(update.isSelected()), finalActor1Name, finalActor2Name);

                                        st.execute(query);
                                        st.close();
                                        con.close();
                                        stage2.close();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }


                                });
                            }


                            if (!selectedLines.contains(connection)) {
                                selectedLines.add(connection);
                                connection.setStroke(Color.web("#c25485"));
                            } else if (selectedLines.contains(connection)) {
                                selectedLines.remove(connection);
                                connection.setStroke(Color.web("54c291"));
                            }
                        });


                        actor1.layoutXProperty().addListener((obs, oldVal, newVal) -> {
                            double newX1 = newVal.doubleValue() + actor1.getWidth();
                            double newY1 = actor1.getLayoutY() + actor1.getHeight() / 2;
                            connection.setStartX(newX1);
                            connection.setStartY(newY1);
                        });
                        actor1.layoutYProperty().addListener((obs, oldVal, newVal) -> {
                            double newX1 = actor1.getLayoutX() + actor1.getWidth();
                            double newY1 = newVal.doubleValue() + actor1.getHeight() / 2;
                            connection.setStartX(newX1);
                            connection.setStartY(newY1);
                        });

                        actor2.layoutXProperty().addListener((obs, oldVal, newVal) -> {
                            double newX2 = newVal.doubleValue();
                            double newY2 = actor2.getLayoutY() + actor2.getHeight() / 2;
                            connection.setEndX(newX2);
                            connection.setEndY(newY2);
                        });
                        actor2.layoutYProperty().addListener((obs, oldVal, newVal) -> {
                            double newX2 = actor2.getLayoutX();
                            double newY2 = newVal.doubleValue() + actor2.getHeight() / 2;
                            connection.setEndX(newX2);
                            connection.setEndY(newY2);
                        });
                    } else if (actor1.getLayoutX() > actor2.getLayoutX()) {
                        double x1 = actor1.getLayoutX();
                        double x2 = actor2.getLayoutX() + actor2.getWidth();
                        double y1 = actor1.getLayoutY() + actor1.getHeight() / 2;
                        double y2 = actor2.getLayoutY() + actor2.getHeight() / 2;


                        CustomLine connection = new CustomLine(actor1, actor2, actor1Name, actor2Name, x1, y1, x2, y2);

                        connection.setStrokeWidth(2);
                        connections.add(connection);
                        root.getChildren().add(connection);
                        existLines.add(connection);

                        connection.setOnMouseEntered(touchEvent -> {
                            connection.setStrokeWidth(6);
                        });
                        connection.setOnMouseExited(touchEvent -> {
                            connection.setStrokeWidth(2);
                        });
                        connection.setOnMouseClicked(mouseEvent -> {
                            if (!selectedLines.contains(connection)) {
                                selectedLines.add(connection);
                                connection.setStroke(Color.web("#c25485"));
                            } else if (selectedLines.contains(connection)) {
                                selectedLines.remove(connection);
                                connection.setStroke(Color.web("#54c291"));
                            }
                        });


                        actor1.layoutXProperty().addListener((obs, oldVal, newVal) -> {
                            double newX1 = newVal.doubleValue();
                            double newY1 = actor1.getLayoutY() + actor1.getWidth();
                            connection.setStartX(newX1);
                            connection.setStartY(newY1);
                        });
                        actor1.layoutYProperty().addListener((obs, oldVal, newVal) -> {
                            double newX1 = actor1.getLayoutX() + actor1.getHeight() / 2;
                            double newY1 = newVal.doubleValue() + actor1.getHeight() / 2;
                            connection.setStartX(newX1);
                            connection.setStartY(newY1);
                        });

                        actor2.layoutXProperty().addListener((obs, oldVal, newVal) -> {
                            double newX2 = newVal.doubleValue() + actor2.getWidth();
                            double newY2 = actor2.getLayoutY() + actor1.getWidth();
                            connection.setEndX(newX2);
                            connection.setEndY(newY2);
                        });
                        actor2.layoutYProperty().addListener((obs, oldVal, newVal) -> {
                            double newX2 = actor2.getLayoutX() + actor2.getWidth();
                            double newY2 = newVal.doubleValue() + actor2.getHeight() / 2;
                            connection.setEndX(newX2);
                            connection.setEndY(newY2);
                        });
                    }
                }
            }
        });

        stage.setScene(scene);
        stage.show();

    }

    public String generateCode(String ucName, String senario) {

        StringBuilder builder = new StringBuilder();
        builder.append("package ").append("GeneratedCodes;").append("\n\n")
                .append("public class ").append(ucName.replace(" ", "")).append(" {\n\n")
                .append("    // methods for ").append(ucName).append(" \n\n");
        String[] methods = senario.split("\n");
        for (String method : methods) {
            builder.append("   public void ").append(capitalize(method)).append("(){\n\n")
                    .append(" }\n\n");
        }
        builder.append("}");
        return builder.toString();
    }

    public static final String capitalize(String str) {
        if (str == null || str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

