//Oskar Berggren, osbe8976

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class Triangle extends Polygon {
    private String name;
    private Position position;
    private Category category;
    private boolean marked;

    public Triangle(String name, Category category, Position position){
        super(position.getXcoord(), position.getYcoord() + 25, position.getXcoord() - 15, position.getYcoord() - 5, position.getXcoord() + 15, position.getYcoord() - 5);
        this.name = name;
        this.position = position;
        this.category = category;
    }

    public boolean getMarked() {
        return marked;
    }

    public String getName() {
        return name;
    }

    public void setMarked(boolean marked){
        this.marked = marked;
        if (marked)
            setStroke(Color.HOTPINK);
        else if(category == null)
            setStroke(Color.BLACK);
        else
            setStroke(category.getColor());
    }

    public Position getPosition() {
        return position;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return noCategory() + "," + position + "," + name;
    }

    public String noCategory(){
        if (category == null)
            return "None";
        else
            return category.getName();
    }
}
