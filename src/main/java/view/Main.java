package main.java.view;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.database.DriverDAO;
import main.java.database.PenggunaDAO;
import main.java.database.PenjualanDAO;
import main.java.model.Customer;
import main.java.model.Driver;
import main.java.model.Order;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        try {
            DriverDAO driverDAO = new DriverDAO();
            ArrayList<Driver> drivers = driverDAO.getAllDriver();
            root.getChildren().add(new Label("Jumlah driver: " + drivers.size()));
            root.getChildren().add(new Label("Driver acak: " + driverDAO.getDriverRandom()));

            Customer c = new PenggunaDAO().getCustomerByTelp("081576665555");
            root.getChildren().add(new Label(
                "Customer 081576665555: " + (c == null ? "tidak ada" : c.getNama())));

            ArrayList<Order> orders = new PenjualanDAO().getAllPenjualan();
            root.getChildren().add(new Label("Jumlah penjualan: " + orders.size()));

            if (!orders.isEmpty()) {
                Order o = orders.get(0);
                root.getChildren().add(new Label(
                    "Penjualan terbaru: " + o.getOrderId()
                    + " - total Rp" + String.format("%,.0f", o.hitungTotal())));
            }
        } catch (SQLException ex) {
            root.getChildren().add(new Label("Error: " + ex.getMessage()));
        }

        primaryStage.setTitle("U-Ride - Tes DAO");
        primaryStage.setScene(new Scene(root, 650, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}