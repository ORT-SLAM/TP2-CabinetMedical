module sio.tp2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens sio.tp2 to javafx.fxml;
    exports sio.tp2;
}