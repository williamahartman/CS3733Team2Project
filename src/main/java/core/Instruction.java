package core;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by ziyanding on 11/19/15.
 */
public class Instruction {
    private List<String> instruction;
    public Instruction(){
        this.instruction = new ArrayList<String>();
    }

    /**
     *
     * Return a list of step by step instructions.
     * @param locList a list of locations
     * @param scale the map scale
     * @return The list of step by step instructions
     */

    public List<String> stepByStepInstruction(List<Location> locList, int scale) {
        int i = 0;
        int flag = 0; //records the relation between previous edge and next edge
        int flag2 = 0; //records the relation between previous edge and the edge before previous edge
        double distance = 0;
        double distance2 = 0;
        int listSize = locList.size();
        //if there is no location in location list
        if (listSize == 0) {
            instruction.add("Can't find route between these two locations.");
            return instruction;
        }
        //if there is only one location in location list
        else if (listSize == 1 || (locList.get(listSize - 1) == locList.get(0))) {
            instruction.add("You arrive at your destination.");
            return instruction;
        }
        //if there are more than one location in location list
        else {
            while (i < (listSize - 1)) {
                Location locPrev = locList.get(i);
                //if i is not 0, set locPrev to the previous location
                if (i != 0){
                    locPrev = locList.get(i - 1);
                }
                Location locCurrent = locList.get(i); //current location
                Location locNext = locList.get(i + 1); //next location
                double locPrevX = locPrev.getPosition().getX(); //x value of previous location
                double locPrevY = locPrev.getPosition().getY(); //y value of previous location
                double locCurrentX = locCurrent.getPosition().getX(); //x value of current location
                double locCurrentY = locCurrent.getPosition().getY(); //y value of current location
                double locNextX = locNext.getPosition().getX(); //x value of next location
                double locNextY = locNext.getPosition().getY(); //y value of next location

                //vector1 is the vector pointing from current location to previous location
                Point2D.Double vector1 = new Point2D.Double((locPrevX - locCurrentX), (locPrevY - locCurrentY));
                //vector2 is the vector pointing from current location to next location
                Point2D.Double vector2 = new Point2D.Double((locNextX - locCurrentX), (locNextY - locCurrentY));

                //calculates the length of vector2
                double vector2Length = vector2.distance(0, 0);
                //calculate the degree between vector2 and the positive side of x axis
                double deg = Math.toDegrees(Math.acos(vector2.getX() / vector2Length));
                //determines what direction user should head first
                if (i == 0){
                    if (deg < 20 || deg > 340){
                        instruction.add("Head East.\n");
                    } else if (deg >= 20 && deg <= 70){
                        instruction.add("Head North East.\n");
                    } else if (deg > 70 && deg < 110){
                        instruction.add("Head North.\n");
                    } else if (deg >= 110 && deg <= 160) {
                        instruction.add("Head North West.\n");
                    } else if (deg > 160 && deg < 200){
                        instruction.add("Head West.\n");
                    } else if (deg >= 200 && deg <= 250){
                        instruction.add("Head South West.\n");
                    } else if (deg > 250 && deg < 290){
                        instruction.add("Head South.\n");
                    } else if (deg >= 290 && deg <= 340){
                        instruction.add("Head South Ease.\n");
                    }
                }

                //determines if the vector2 rotates counterclockwise or clockwise from vector1
                //math function v1 * v2 = |v1||v2|sin(x)
                double cross = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
                //sin(x) is negative if it rotates counterclockwise, sin(x) is positive if it rotates clockwise
                //sin(x) is 0 if it rotates 180 degree
                if (cross < 0) { //counterclockwise
                    flag = 1; //left
                } else if (cross > 0) { //clockwise
                    flag = 2; //right
                } else {
                    flag = 3; //straight
                }
                String str = " ";
                double l1 = locCurrent.getTwoDecimalDistance(locPrev, scale);
                //^distance between previous node and current node
                double l2 = locCurrent.getTwoDecimalDistance(locNext, scale);
                //^distance between next node and current node
                double l3 = locPrev.getTwoDecimalDistance(locNext, scale);
                //^distance between previous node and next node
                if (flag != 3){
                    //calculates degree between previous edge and next edge(vector1 and vector2)
                    //Using inverse trigonometric functions and Law of cosines
                    double degree = Math.toDegrees(Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2)));
                    if (degree > 120){
                        str = " slightly ";
                    } else if (degree < 60){
                        str = " hard ";
                    }
                }
                if (flag == 1){
                    //user should turn left and then add all distance after last turn together
                    distance = leftRightDirection(i, flag2, distance, distance2);
                    instruction.add("Turn" + str + "left.\n");
                    if (i == listSize - 2){
                        //if reach the end of the location list
                        instruction.add("Go " + l2 + " miles.\n");
                    }
                    distance2 = l2; //records the distance between current and next location
                    flag2 = 2; //records current action turning
                } else if (flag == 2){
                    //user should turn left and then add all distance after last turn together
                    distance = leftRightDirection(i, flag2, distance, distance2);
                    instruction.add("Turn" + str + "right.\n");
                    distance2 = l2; //records the distance after last turn
                    flag2 = 2;
                    if (i == listSize - 2){
                        //if reach the end of the location list
                        instruction.add("Go " + l2 + " miles.\n");
                    }
                } else {
                    if (flag == 3){
                        //this is for the situation that after turing, user go straight passing several nodes
                        if (flag2 == 2){ //if previous action is either turing right or turing left
                            distance += distance2;
                        }
                        distance += l2;
                        flag2 = 1; //records current action is going straight
                    }
                    if (i == listSize - 2){
                        //if reach the end of the location list
                        instruction.add("Go straight and go " + distance + " miles.\n");
                    }
                }
                i++;
            }
        }
        instruction.add("You arrive at your destination.\n");
        return instruction;
    }


    /**
     *
     * A helper function to add instruction.
     * @param i
     * @param flag2
     * @param distance
     * @param distance2
     * @return the distance
     */

    private double leftRightDirection(int i, int flag2, double distance, double distance2){
        if (flag2 == 1){
            instruction.add("Go straight and go " + distance + " miles.\n");
            distance = 0;
            return distance;
        }
        if (i != 0 && i != 1) {
            instruction.add("Go " + distance2 + " miles.\n");
        }
        return distance;
    }
    public List<String> getInstruction(){
        return instruction;
    }
}
