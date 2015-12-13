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

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }
}
