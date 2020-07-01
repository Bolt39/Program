/**  Oskar Berggren osbe8976 */

import javafx.scene.paint.Color;

import java.util.Objects;

public class Category {
    private Color color;
    private String name;

    public Category(String name, Color color) {
        this.color = color;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Color getColor(){
        return color;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Category){
            Category category = (Category) other;
            return color.equals(category.color) && name.equals(category.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, name);
    }

    @Override
    public String toString() {
        return name;
    }
}
