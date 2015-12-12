package core;

import com.kitfox.svg.app.beans.SVGIcon;

/**
 * Created by Scott on 12/12/2015.
 * Holds the svg and scale of a map
 */
public class MapImage {
    private SVGIcon svg;
    private int scaleX;
    private int scaleY;

    public MapImage(SVGIcon img, int x, int y){
        svg = img;
        scaleX = x;
        scaleY = y;
    }

    public SVGIcon getSVG(){ return svg; }

    public int getScale_x(){ return scaleX;  }

    public int getScale_y(){ return scaleY; }

}
