module com.project.bagelshop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.project.bagelshop to javafx.fxml;
    exports com.project.bagelshop;
}