package sample;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.io.Serializable;

public class CustomLine extends Line implements Serializable {
    private AnchorPane actor;
    private AnchorPane usecase;
    private String startPane;
    private String endPane;
    private double x1;
    private double x2;
    private double y1;
    private double y2;


    public CustomLine(AnchorPane actor,AnchorPane usecase,String startPane, String endPane, double x1,double x2,double y1,double y2) {
        this.actor = actor;
        this.usecase = usecase;
        this.startPane = startPane;
        this.endPane = endPane;
        this.setStartX(x1);
        this.setEndX(x2);
        this.setStartY(y1);
        this.setEndY(y2);
    }


    public AnchorPane getActor() {
        return actor;
    }
    public AnchorPane getUsecase() {
        return usecase;
    }
    public String getStartPane() {
        return startPane;
    }

    public String getEndPane() {
        return endPane;
    }
}
