module org.studentfx.womenshop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.studentfx.womenshop to javafx.fxml;
    exports org.studentfx.womenshop;
}