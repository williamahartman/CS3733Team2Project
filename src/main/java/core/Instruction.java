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
     * @return The list of step by step instructions
     */

    public List<String> stepByStepInstruction(List<Location> locList) {
        int i = 0;
        int flag = 0;
        int flag2 = 0;
        double distance = 0;
        double distance2 = 0;
        int listSize = locList.size();
        if (listSize == 0) {
            instruction.add("Can't find route between these two locations.");
            return instruction;
        } else if (listSize == 1 || (locList.get(listSize - 1) == locList.get(0))) {
            instruction.add("You arrive at your destination.");
            return instruction;
        } else {
            while (i < (listSize - 1)) {
                Location locPrev = locList.get(i);
                if (i != 0){
                    locPrev = locList.get(i - 1);
                }
                Location locCurrent = locList.get(i);
                Location locNext = locList.get(i + 1);
                double locPrevX = locPrev.getPosition().getX();
                double locPrevY = locPrev.getPosition().getY();
                double locCurrentX = locCurrent.getPosition().getX();
                double locCurrentY = locCurrent.getPosition().getY();
                double locNextX = locNext.getPosition().getX();
                double locNextY = locNext.getPosition().getY();

                Point2D.Double vector1 = new Point2D.Double((locPrevX - locCurrentX), (locPrevY - locCurrentY));
                Point2D.Double vector2 = new Point2D.Double((locNextX - locCurrentX), (locNextY - locCurrentY));

                double vector2Length = vector2.distance(0, 0);
                double deg = Math.toDegrees(Math.acos(vector2.getX() / vector2Length));

                if (i == 0){
                    if (deg < 20 || deg > 340){
                        instruction.add("\nHead East.");
                    } else if (deg >= 20 && deg <= 70){
                        instruction.add("\nHead North East.");
                    } else if (deg > 70 && deg < 110){
                        instruction.add("\nHead North.");
                    } else if (deg >= 110 && deg <= 160) {
                        instruction.add("\nHead North West.");
                    } else if (deg > 160 && deg < 200){
                        instruction.add("\nHead West.");
                    } else if (deg >= 200 && deg <= 250){
                        instruction.add("\nHead South West.");
                    } else if (deg > 250 && deg < 290){
                        instruction.add("\nHead South.");
                    } else if (deg >= 290 && deg <= 340){
                        instruction.add("\nHead South Ease.");
                    }
                }
                //determines if the vector2 rotates counterclockwise or clockwise
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
                double l1 = locCurrent.getTwoDecimalDistance(locPrev); //distance between previous node and current node
                double l2 = locCurrent.getTwoDecimalDistance(locNext); //distance between next node and current node
                double l3 = locPrev.getTwoDecimalDistance(locNext); //distance between previous node and next node
                if (flag != 3){
                    //calculates degree between previous edge and next edge
                    //Using inverse trigonometric functions and Law of cosines
                    double degree = Math.toDegrees(Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2)));
                    if (degree > 120){
                        str = " slightly ";
                    } else if (degree < 60){
                        str = " hard ";
                    }
                }
                if (flag == 1){
                    distance = leftRightDirection(i, flag2, distance, distance2);
                    instruction.add("\nTurn" + str + "left.");
                    if (i == listSize - 2){
                        instruction.add("\nGo " + l2 + " feet.");
                    }
                    distance2 = l2;
                    flag2 = 2;
                } else if (flag == 2){
                    distance = leftRightDirection(i, flag2, distance, distance2);
                    instruction.add("\nTurn" + str + "right.");
                    distance2 = l2;
                    flag2 = 2;
                    if (i == listSize - 2){
                        instruction.add("\nGo " + l2 + " feet.");
                    }
                } else {
                    if (flag == 3){
                        if (flag2 == 2){
                            distance += distance2;
                        }
                        distance += l2;
                        flag2 = 1;
                    }
                    if (i == listSize - 2){
                        instruction.add("\nGo straight and go " + distance + " feet.");
                    }
                }
                i++;
            }
        }
        instruction.add("\nYou arrive at your destination.");
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
        if (i != 0 && i != 1) {
            instruction.add("\nGo " + distance2 + " feet.");
        }
        if (flag2 == 1){
            instruction.add("\nGo straight and go " + distance + " feet.");
            distance = 0;
        }
        return distance;
    }

    public List<String> getInstruction(){
        return instruction;
    }
}
