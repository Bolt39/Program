//Oskar Berggren, osbe8976

import java.util.Objects;

public class Position {
    private int xcoord;
    private int ycoord;
    public Position(int xcoord, int ycoord){
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }

    public int getXcoord() {
        return xcoord;
    }
    public int getYcoord() {
        return ycoord;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Position){
            Position position = (Position) other;
            return xcoord == position.xcoord && ycoord == position.ycoord;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xcoord, ycoord);
    }

    @Override
    public String toString() {
        return xcoord + "," + ycoord;
    }
}
