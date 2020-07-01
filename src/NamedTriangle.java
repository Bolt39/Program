//Oskar Berggren, osbe8976

public class NamedTriangle extends Triangle {

    public NamedTriangle(String name, Category category, Position coordinates) {
        super(name, category, coordinates);
    }

    @Override
    public String toString() {
        return "Named," + super.toString();
    }

}
