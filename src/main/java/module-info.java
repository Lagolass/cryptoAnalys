module com.example.cryptoanalys {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.cryptoanalys to javafx.fxml;
    exports com.example.cryptoanalys;
}