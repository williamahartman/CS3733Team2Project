package ui;

import core.*;

/**
 * Created by hollyn on 12/12/15.
 */
public class StartEnd {
    public Location start;
    public Location end;

    public StartEnd(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        StartEnd se = (StartEnd) o;
        return (this.start.getPosition() == se.start.getPosition()) &&
                (this.end.getPosition() == se.end.getPosition());
    }

    @Override
    public int hashCode() {
//
//        double x = this.start.getPosition().getX();
//        double y = this.start.getPosition().getY();
//
//        int tmp = (int) ( y +  ((x + 1) / 2));
//        return (int) x +  (tmp * tmp);

        return (int) ((this.start.getPosition().getX() * 33) + (this.end.getPosition().getX() * 33));

    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }
}
