//Oskar Berggren, osbe8976

public class DescribedTriangle extends Triangle {
    private String description;


    public DescribedTriangle(String name, Category category, Position coordinates, String description) {
        super(name, category, coordinates);
        this.description = description;
    }

    @Override
    public String toString() {
        return "Described," + super.toString() + "," + description;
    }

    public String getDescription() {
        return description;
    }
}
