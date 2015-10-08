import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Main
        extends Application
{
    private static final String FILE_NAME = "nba_info.xls";

    private static List<Team> teams;
    List<Team> teams_in_trade = new ArrayList<>();

    Scene scene0, scene1, scene2;
    Button button1, button2;
    Label label1, label2, label3, final_label;

    String name1, name2;
    ObservableList<String> string1, string2;

    ListView<String> list1 = new ListView<>();
    ObservableList<String> data1 = FXCollections.observableArrayList();

    ListView<String> list2 = new ListView<>();
    ObservableList<String> data2 = FXCollections.observableArrayList();

    ListView<String> list3 = new ListView<>();
    ObservableList<String> data3;

    ListView<String> list4 = new ListView<>();
    ObservableList<String> data4;

    public static void main(String[] args)
    {
        init_trade();
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        for (Team t : teams) {
            data1.add(t.get_full_name());
        }
        for (Team t : teams) {
            data2.add(t.get_full_name());
        }

        window.setTitle("Trade Calculator");

        ////////////////   TITLE SCREEN

        button1 = new Button("Enter");
        button1.setOnAction(e -> screen1(window));

        label1 = new Label("Welcome to the NBA Trade Calculator");

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.TOP_CENTER);

        scene0 = new Scene(layout, 300, 100);
        scene0.getStylesheets().add("theme.css");

        window.setScene(scene0);
        window.show();
    }

    private void screen1(Stage window)
    {
        /////////////////   SCREEN 1

        BorderPane layout2 = new BorderPane();

        // TOP

        HBox top1 = new HBox();
        top1.setPrefHeight(50);

        label2 = new Label("Pick your teams");
        top1.getChildren().add(label2);
        top1.setAlignment(Pos.CENTER);

        layout2.setTop(top1);

        // COL 1
        VBox left1 = new VBox();
        left1.getChildren().addAll(list1);

        list1.setItems(data1);
        list1.setPrefWidth(250);
        list1.setPrefHeight(430);
        layout2.setLeft(left1);

        // COL 2
        VBox center1 = new VBox();
        center1.getChildren().addAll(list2);

        list2.setItems(data2);
        list2.setPrefWidth(250);
        list2.setPrefHeight(430);
        layout2.setCenter(center1);

        // COL 3
        VBox right1 = new VBox();
        right1.setPrefWidth(140);

        button2 = new Button("Go");
        button2.setOnAction(e ->
        {
            name1 = list1.getSelectionModel().getSelectedItem();
            name2 = list2.getSelectionModel().getSelectedItem();

            if ((name1 == null || name2 == null) || name1.equals(name2)) {
                alert("Please pick 2 different teams.");
            } else {
                data3 = get_players(name1);
                data4 = get_players(name2);

                screen2(window, list3, list4, data3, data4);
            }

        });

        right1.getChildren().addAll(button2);
        right1.setAlignment(Pos.CENTER);
        layout2.setRight(right1);

        // DONE
        scene1 = new Scene(layout2, 640, 480);
        scene1.getStylesheets().add("theme.css");

        window.setScene(scene1);
    }

    public void screen2(Stage window, ListView list3, ListView list4, ObservableList data3, ObservableList data4)
    {
        /////////////// SCREEN 2

        BorderPane layout3 = new BorderPane();

        // TOP

        HBox top2 = new HBox();
        top2.setPrefHeight(50);

        label3 = new Label("Pick your players");
        top2.getChildren().add(label3);
        top2.setAlignment(Pos.CENTER);

        layout3.setTop(top2);

        // COL 1
        VBox left2 = new VBox();
        list3.setItems(data3);
        list3.setPrefWidth(250);
        list3.setPrefHeight(430);
        list3.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        left2.getChildren().addAll(list3);
        layout3.setLeft(left2);

        // COL 2
        VBox center2 = new VBox();
        list4.setItems(data4);
        list4.setPrefWidth(250);
        list4.setPrefHeight(430);
        list4.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        center2.getChildren().addAll(list4);
        layout3.setCenter(center2);


        // COL 3
        VBox right2 = new VBox();
        right2.setPrefWidth(140);

        button2 = new Button("Go");
        button2.setOnAction(e ->
        {
            string1 = list3.getSelectionModel().getSelectedItems();
            string2 = list4.getSelectionModel().getSelectedItems();
            System.out.println(string1);

            if(string1.isEmpty() || string2.isEmpty())
            {
                alert("You need to pick at least one player");
            }
            else
            {
                List<Player> full_list = make_full_list(string1, string2);

                screen3(window, Trade.trade(full_list, teams_in_trade));
            }
        });

        right2.getChildren().addAll(button2);
        right2.setAlignment(Pos.CENTER);
        layout3.setRight(right2);


        /// DONE

        scene2 = new Scene(layout3, 640, 480);
        scene2.getStylesheets().add("theme.css");
        window.setScene(scene2);
    }

    public void screen3(Stage window, ResultPair result)
    {
        ///////// SCREEN 3
        VBox layout4 = new VBox(20);

        if (result.pair.bool == true)
        {
            final_label = new Label("This trade works!");
        }

        else
        {
            String over = addcommas(result.pair.num);

            final_label = new Label("This trade is unsuccessful. "
                    + result.team.city + " receives $" + over + " more than allowed.");
        }

        Button another = new Button("Try another trade");
        another.setOnAction(e ->
        {
            window.setScene(scene1);
            teams_in_trade = new ArrayList<>();
        });

        layout4.getChildren().addAll(final_label, another);
        layout4.setAlignment(Pos.CENTER);

        Scene scene3 = new Scene(layout4, 640, 480);
        scene3.getStylesheets().add("theme.css");
        window.setScene(scene3);
    }

    //////////////////////////////////////////


    public static void init_trade()
    {
        try
        {
            File file = new File(FILE_NAME);
            FileInputStream fis = new FileInputStream(file);

            HSSFWorkbook wb = new HSSFWorkbook(fis);

            HSSFSheet players = wb.getSheetAt(0);
            HSSFSheet teams_ = wb.getSheetAt(1);
            HSSFSheet contracts = wb.getSheetAt(2);

            teams = Trade.load_teams(teams_, players, contracts);
        }
        catch (IOException e)
        {
            System.out.println("file not found");
        }
    }

    public ObservableList<String> get_players(String name)
    {
        ObservableList<String> players = FXCollections.observableArrayList();
        for (Team t: teams)
        {
            if (t.get_full_name().equals(name))
            {
                teams_in_trade.add(t);

                for (Player p: t.players)
                {
                    players.add(p.get_full_name());
                }
            }
        }
        return players;
    }

    public List<Player> make_full_list(ObservableList<String> string1, ObservableList<String> string2)
    {
        List<Player> full = new ArrayList<>();

        for (String s : string1)
        {
            for (Player p : teams_in_trade.get(0).players)
            {
                if (s.equals(p.get_full_name()))
                {
                    full.add(p);
                }
            }
        }
        for (String s : string2)
        {
            for (Player p : teams_in_trade.get(1).players)
            {
                if (s.equals(p.get_full_name()))
                {
                    full.add(p);
                }
            }
        }

        return full;
    }

    private static String addcommas(double d)
    {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(true);
        return format.format(d);
    }

    private static void alert(String message)
    {
        Stage window2 = new Stage();
        window2.initModality(Modality.APPLICATION_MODAL);
        window2.setTitle("Error");
        window2.setMinWidth(300);
        window2.setMinHeight(100);

        Label alert = new Label();
        alert.setText(message);
        Button close= new Button("OK");
        close.setOnAction(f -> window2.close());

        VBox close_layout = new VBox(10);
        close_layout.getChildren().addAll(alert, close);
        close_layout.setAlignment(Pos.CENTER);

        Scene closer = new Scene(close_layout);
        closer.getStylesheets().add("theme.css");
        window2.setScene(closer);
        window2.showAndWait();
    }

}