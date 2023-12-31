/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package appointmentmaker;

import java.sql.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;



/**
 *
 * @author ajohnson132490
 */
public class AppointmentMaker extends Application {
    private Connection conn;
    private static final String URL = "jdbc:mysql://localhost:3306/client_schedule";
    private static final String USERNAME = "sqlUser";
    private static final String PASSWORD = "Passw0rd!";
    private Locale locale;
    private ResourceBundle lang;
    private IntUtils h;
    private String currentUser;
    
    
    
    /**
     * The start function is the initial landing screen of the application
     * where the user will be able to log in and see their current location.
     * <p>
     * Language is determined in the start function.
     * 
     * @param primaryStage the main window of the application
     */
    @Override
    public void start(Stage primaryStage) {
        h = new IntUtils();
        ///Start on the Login screen
        //Set Language
        locale = new Locale("fr", "FR");
        lang = ResourceBundle.getBundle("appointmentmaker.lang", locale);
        
        //Create Scene
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 400);
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());
        
        //Create the main VBox
        VBox mainVBox = new VBox();
        Label title = new Label("Appointment Maker");
        title.setStyle("-fx-font: 24 ariel;");
        mainVBox.getChildren().add(title);
        
        //Username and Password fields
        VBox form = new VBox();
        form.getStyleClass().add("loginForm");
        
        Label uName = new Label("Username");
        TextField uField = new TextField();
        
        Label pWord = new Label("Password");
        TextField pField = new TextField();
        
        //Login Button
        HBox loginBtnPadding = new HBox();
        loginBtnPadding.setPadding(new Insets(10, 0, 0, 150));
        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(50);
        loginBtnPadding.getChildren().add(loginBtn);
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //Connect to the database
                    conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    h.setConnection(conn);
                    
                    //Query the database
                    String query = "SELECT Password FROM users WHERE User_Name = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, uField.getText());
                    ResultSet rs = stmt.executeQuery();
                    
                    //Check if the password entered matches the username
                    if (rs.next() && rs.getString("Password").equals(pField.getText())) {
                        System.out.println("Login Success");
                        currentUser = uField.getText();
                        viewAppointments(primaryStage);
                    } else {
                        System.out.println("Username or password is incorrect");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:    
                        try {
                            //Connect to the database
                            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                            h.setConnection(conn);
                            
                            //Query the database
                            String query = "SELECT Password FROM users WHERE User_Name = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, uField.getText());
                            ResultSet rs = stmt.executeQuery();
                            
                            //Check if the password entered matches the username
                            if (rs.next() && rs.getString("Password").equals(pField.getText())) {
                                System.out.println("Login Success");
                                currentUser = uField.getText();
                                viewAppointments(primaryStage);
                            } else {
                                System.out.println("Username or password is incorrect");
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                }
            }
        });
        
        //Add Form parts to form VBox
        form.getChildren().add(uName);
        form.getChildren().add(uField);
        form.getChildren().add(pWord);
        form.getChildren().add(pField);
        form.getChildren().add(loginBtnPadding);
        mainVBox.getChildren().add(form);
        
        //User Location
        HBox lower = new HBox();
        lower.setPadding(new Insets(70, 0, 0, 150));
        ZoneId zoneID = ZoneId.systemDefault();
        Label location = new Label("Location: " + zoneID.toString());
        location.setPrefWidth(150);        
        lower.getChildren().add(location);
        mainVBox.getChildren().add(lower);
        
        
        
        //Add all items to the root
        root.getChildren().add(mainVBox);
        mainVBox.getStyleClass().add("loginRoot");
        
        //Set Stage
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void viewCustomers(Stage primaryStage) {
        //Create the main VBox
        VBox mainVBox = new VBox(15);
        //Add all items to the root
        Pane root = new Pane();
        root.getChildren().add(mainVBox);
        mainVBox.getStyleClass().add("mainPage");
        
        //Create Scene
        Scene scene = new Scene(root, 1250, 600);
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());
        
        //Create title HBox
        HBox upper = new HBox();
        upper.setAlignment(Pos.TOP_LEFT);
        Label title = new Label("View Customers");
        title.setStyle("-fx-font: 24 ariel;");
        upper.getChildren().add(title);
        mainVBox.getChildren().add(upper);
        
        //Creating tableview VBox
        VBox tableVBox = new VBox(5);
        
        //All Customers TableView Table
        TableView customersTable = new TableView();
                customersTable.setPrefWidth(1200);

        ObservableList<ObservableList> csrData = FXCollections.observableArrayList();
        try {
            //Connect to the database
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
            //Query the database
            String query = "SELECT * FROM customers";
            ResultSet rs = conn.createStatement().executeQuery(query);
            
            //Populate the table with columns
            for (int i = 0; i<rs.getMetaData().getColumnCount(); i++) {
                final int finI = i;
                //Create a new column
                TableColumn col = new TableColumn<>();
                col.setText(h.customerTableColumnName(rs.getMetaData().getColumnName(i+1)));    
                
                //Set Column formatting
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(finI).toString());                        
                    }                    
                });
                
                //Add column to the table
                customersTable.getColumns().add(col);
            }
            
            //Populate the customers data into the data ObservableList
            while(rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Add the data to a row
                    if (rs.getMetaData().getColumnName(i).equals("Address")) {
                        row.add(h.formatAddress(rs.getString(i)));
                        
                    } else {
                        row.add(rs.getString(i));

                    }
                }
                //Add the full row to the observableList
                csrData.add(row);
            }
            
            //Populate table with customer data
            customersTable.setItems(csrData);   
        } catch (Exception e) {
            System.out.println(e);
        }
        
        tableVBox.getChildren().add(customersTable);
        
        //Buttons HBox
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(0, 0, 0, 995));
        
        //Create the buttons
        Button addBtn = new Button("Add");
        EventHandler<ActionEvent> addEvent = (ActionEvent e) -> {
            //todo: create add form
        };
        addBtn.setOnAction(addEvent);
        
        Button updateBtn = new Button("Update");
        EventHandler<ActionEvent> updateEvent = (ActionEvent e) -> {
            //todo: create update form
        };
        updateBtn.setOnAction(updateEvent);
        
        Button deleteBtn = new Button("Delete");
        EventHandler<ActionEvent> deleteEvent = (ActionEvent e) -> {
            //todo: figure out how to delete items and refresh the tableview
        };
        deleteBtn.setOnAction(deleteEvent);
        
        //Add the buttons to the table VBox
        buttons.getChildren().addAll(addBtn, updateBtn, deleteBtn);
        tableVBox.getChildren().add(buttons);

        //Add the tableview VBox to the main VBox
        mainVBox.getChildren().add(tableVBox);
        
        //Lower page controls
        HBox lower = new HBox();
        Button viewAppointmentsBtn = new Button("View Appointments");
        EventHandler<ActionEvent> viewAppointmentsEvent = (ActionEvent e) -> {
            viewAppointments(primaryStage);
        };
        viewAppointmentsBtn.setOnAction(viewAppointmentsEvent);
        viewAppointmentsBtn.setPrefWidth(625);
        viewAppointmentsBtn.setPrefHeight(75);
        
        Button viewCustomersBtn = new Button("View Customers");
        EventHandler<ActionEvent> viewCustomersEvent = (ActionEvent e) -> {
            viewCustomers(primaryStage);
        };
        viewCustomersBtn.setOnAction(viewCustomersEvent);
        viewCustomersBtn.setPrefWidth(625);
        viewCustomersBtn.setPrefHeight(75);
        
        //Add buttons to lower HBox
        lower.getChildren().addAll(viewAppointmentsBtn, viewCustomersBtn);
        mainVBox.getChildren().add(lower);
        
        
        primaryStage.setTitle("View Customers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void viewAppointments(Stage primaryStage) {
        //Create the main VBox
        VBox mainVBox = new VBox(15);
        //Add all items to the root
        Pane root = new Pane();
        root.getChildren().add(mainVBox);
        mainVBox.getStyleClass().add("mainPage");
        
        //Create Scene
        Scene scene = new Scene(root, 1250, 625);
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());
        
        //Create title HBox
        HBox upper = new HBox();
        upper.setAlignment(Pos.TOP_LEFT);
        Label title = new Label("View Appointments");
        title.setStyle("-fx-font: 24 ariel;");
        upper.getChildren().add(title);
        mainVBox.getChildren().add(upper);
        
        //Creating tableview VBox
        VBox tableVBox = new VBox(5);
        
        
        //Week or Month Radio Buttons
        HBox radioButtons = new HBox(20);
        ToggleGroup weekOrMonth = new ToggleGroup();
        RadioButton week = new RadioButton("Week");
        week.setToggleGroup(weekOrMonth);
        week.setSelected(true);
        RadioButton month = new RadioButton("Month");
        month.setToggleGroup(weekOrMonth);
        radioButtons.getChildren().addAll(week, month);
        tableVBox.getChildren().add(radioButtons);
        
        
        //All Appointments TableView Table
        TableView appointmentsTable = new TableView();
                appointmentsTable.setPrefWidth(1200);

        ObservableList<ObservableList> csrData = FXCollections.observableArrayList();
        try {            
            //Query the database
            String query = "SELECT * FROM appointments";
            ResultSet rs = conn.createStatement().executeQuery(query);
            
            //Populate the table with columns
            for (int i = 0; i<rs.getMetaData().getColumnCount(); i++) {
                final int finI = i;
                //Create a new column
                TableColumn col = new TableColumn<>();
                col.setText(h.appointmentTableColumnName(rs.getMetaData().getColumnName(i+1)));    
                
                //Set Column formatting
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(finI).toString());                        
                    }                    
                });
                
                //Add column to the table
                appointmentsTable.getColumns().add(col);
            }
            
            //TODO READ BASED ON RADIO BUTTONS
            csrData = h.getAppointments(0, rs);
            
            //Populate table with customer data
            appointmentsTable.setItems(csrData);   
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        tableVBox.getChildren().add(appointmentsTable);
        
        //Buttons HBox
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(0, 0, 0, 995));
        
        //Create the buttons
        Button addBtn = new Button("Add");
        EventHandler<ActionEvent> addEvent = (ActionEvent e) -> {
            addAppointments(primaryStage);
        };
        addBtn.setOnAction(addEvent);
        
        Button updateBtn = new Button("Update");
        EventHandler<ActionEvent> updateEvent = (ActionEvent e) -> {
            //todo: create update form
        };
        updateBtn.setOnAction(updateEvent);
        
        Button deleteBtn = new Button("Delete");
        EventHandler<ActionEvent> deleteEvent = (ActionEvent e) -> {
            //todo: figure out how to delete items and refresh the tableview
        };
        deleteBtn.setOnAction(deleteEvent);
        
        //Add the buttons to the table VBox
        buttons.getChildren().addAll(addBtn, updateBtn, deleteBtn);
        tableVBox.getChildren().add(buttons);

        //Add the tableview VBox to the main VBox
        mainVBox.getChildren().add(tableVBox);
        
        //Lower page controls
        HBox lower = new HBox();
        Button viewAppointmentsBtn = new Button("View Appointments");
        EventHandler<ActionEvent> viewAppointmentsEvent = (ActionEvent e) -> {
            viewAppointments(primaryStage);
        };
        viewAppointmentsBtn.setOnAction(viewAppointmentsEvent);
        viewAppointmentsBtn.setPrefWidth(625);
        viewAppointmentsBtn.setPrefHeight(75);
        
        Button viewCustomersBtn = new Button("View Customers");
        EventHandler<ActionEvent> viewCustomersEvent = (ActionEvent e) -> {
            viewCustomers(primaryStage);
        };
        viewCustomersBtn.setOnAction(viewCustomersEvent);
        viewCustomersBtn.setPrefWidth(625);
        viewCustomersBtn.setPrefHeight(75);
        
        //Add buttons to lower HBox
        lower.getChildren().addAll(viewAppointmentsBtn, viewCustomersBtn);
        mainVBox.getChildren().add(lower);
        
        primaryStage.setTitle("View Appointments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void addAppointments(Stage primaryStage) {
        //Create the main vbox 
        VBox mainVBox = new VBox(20);
        
        //Add all items to the root
        Pane root = new Pane();
        root.getChildren().add(mainVBox);
        mainVBox.getStyleClass().add("mainPage");
        
        //Create Scene
        Scene scene = new Scene(root, 525, 610);
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());
        
        //Create title HBox
        HBox upper = new HBox();
        upper.setAlignment(Pos.TOP_LEFT);
        Label mTitle = new Label("Create an Appointment");
        mTitle.setStyle("-fx-font: 24 ariel;");
        upper.getChildren().add(mTitle);
        mainVBox.getChildren().add(upper);
        
        //Creating tableview VBox
        GridPane form = new GridPane();
        form.setVgap(25);
        form.setHgap(5);
        
        //Creating some string arrays for the combo boxes
        String hours[] = {"8", "9", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22"};
        String minutes[] = {"00", "01", "02", "03", "04", "05", "06", "07", "08",
            "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
            "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52",
            "53", "54", "55", "56", "57", "58", "59", "60"};        
        
        //Creating all Labels 
        Label id = new Label("ID");
        Label title = new Label("Title");
        Label desc = new Label("Description");
        Label loc = new Label("Location");
        Label contact = new Label("Contact");
        Label type = new Label("Type");
        Label sDate = new Label("Start Date");
        Label sTime = new Label("Start Time");
        Label eDate = new Label("End Date");
        Label eTime = new Label("End Time");
        Label csr = new Label("Customer ID");
        Label user = new Label("User ID");
        
        //Creating all fields
        TextField idField = new TextField(Integer.toString(h.getNextApptId()));
        idField.setDisable(true);
        TextField titleField = new TextField();
        titleField.setPromptText("Appointment title");
        TextField descField = new TextField();
        descField.setPromptText("A brief description");
        TextField locField = new TextField();
        locField.setPromptText("Where is the appointment");
        ComboBox contactField = new ComboBox(h.getAllContacts());
        contactField.setPromptText("Contact");
        TextField typeField = new TextField();
        typeField.setPromptText("What type of appointment");
        
        DatePicker sDateField = new DatePicker();
        ComboBox sTHour = new ComboBox(FXCollections.observableArrayList(hours));
        sTHour.setPromptText("hh");
        ComboBox sTMinute = new ComboBox(FXCollections.observableArrayList(minutes));
        sTMinute.setPromptText("mm");
        
        DatePicker eDateField = new DatePicker();
        ComboBox eTHour = new ComboBox(FXCollections.observableArrayList(hours));
        eTHour.setPromptText("hh");
        ComboBox eTMinute = new ComboBox(FXCollections.observableArrayList(minutes));
        eTMinute.setPromptText("mm");
        
        TextField csrField = new TextField();
        csrField.setPromptText("Who is the customer");
        TextField userField = new TextField();
        userField.setPromptText("Who is the user");
        
        //Create the buttons
        Button add = new Button("Add");
        EventHandler<ActionEvent> addEvent = (ActionEvent e) -> {
            h.validateAppointment(currentUser, idField.getText(), titleField.getText(),
                    descField.getText(), locField.getText(), contactField.getValue().toString(),
                    typeField.getText(), sDateField, sTHour.getValue().toString(),
                    sTMinute.getValue().toString(), eDateField, eTHour.getValue().toString(),
                    eTMinute.getValue().toString(), csrField.getText(), userField.getText());
        };
        add.setOnAction(addEvent);
        Button cancel = new Button("Cancel");
        
        //Add it all to the form
        form.add(id, 0, 0);
        form.add(idField, 1, 0);
        form.add(title, 0, 1);
        form.add(titleField, 1, 1);
        form.add(desc, 0, 2);
        form.add(descField, 1, 2);
        form.add(loc, 0, 3);
        form.add(locField, 1, 3);
        form.add(contact, 0, 4);
        form.add(contactField, 1, 4);
        form.add(type, 0, 5);
        form.add(typeField, 1, 5);
        
        form.add(sDate, 0, 6);
        form.add(sDateField, 1, 6);
        form.add(sTime, 0, 7);
        HBox sTimeField = new HBox();
        sTimeField.getChildren().addAll(sTHour, sTMinute);
        form.add(sTimeField, 1, 7);
        
        form.add(eDate, 0, 8);
        form.add(eDateField, 1, 8);
        form.add(eTime, 0, 9);
        HBox eTimeField = new HBox();
        eTimeField.getChildren().addAll(eTHour, eTMinute);
        form.add(eTimeField, 1, 9);
        
        form.add(csr, 2, 0);
        form.add(csrField, 3, 0);
        form.add(user, 2, 1);
        form.add(userField, 3, 1);
        
        HBox buttons = new HBox(10);
        buttons.setPadding(new Insets(0, 0, 0, 50));
        
        buttons.getChildren().addAll(add, cancel);
        form.add(buttons, 3, 10);
        
        //Add the form to the mainVBox
        mainVBox.getChildren().add(form);
        
        primaryStage.setTitle("View Appointments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
