package ui;
import core.EdgeAttribute;
import core.Location;

import java.awt.geom.Point2D;
import java.util.*;
import core.Edge;

/**
 * Created by ziyanding on 11/19/15.
 */
public class Instruction {
    private List<String> instruction;
    private double totalDistance;
    private int count;
    public Instruction(){
        this.instruction = new ArrayList<>();
        this.totalDistance = 0;
        this.count = 0;
    }

    /**
     *
     * Return a list of step by step instructions.
     * @param locList a list of locations
     * @param scaleX The width of the map in feet
     * @param scaleY The height of the map in feet
     * @return The list of step by step instructions
     */

    public List<String> stepByStepInstruction(List<Location> locList, int scaleX, int scaleY) {
        totalDistance = 0.0;
        int flag; //records the relation between previous edge and next edge
        int flag2 = 0; //records the relation between previous edge and the edge before previous edge
        double distance = 0; //records the sum of distance after previous turning
        //double distance2 = 0; //stores the distance between previous location and current location
        int listSize = locList.size();
        double temp; //used to add appropriate distance to the instruction

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
            for (int i = 0; i < (listSize - 1); i++) {
                Location locPrev = locList.get(i);
                //if i is not 0, set locPrev to the previous location
                if (i != 0) {
                    locPrev = locList.get(i - 1);
                }
                Location locCurrent = locList.get(i); //current location
                Location locNext = locList.get(i + 1); //next location
                double locPrevX = locPrev.getPosition().getX(); //x value of previous location
                double locPrevY = -locPrev.getPosition().getY(); //y value +of previous location
                double locCurrentX = locCurrent.getPosition().getX(); //x value of current location
                double locCurrentY = -locCurrent.getPosition().getY(); //y value of current location
                double locNextX = locNext.getPosition().getX(); //x value of next location
                double locNextY = -locNext.getPosition().getY(); //y value of next location

                //vector1 is parallel to vector pointing from current location to previous location
                //the tail of vector1 is (0, 0)
                Point2D.Double vector1 = new Point2D.Double((locPrevX - locCurrentX), (locPrevY - locCurrentY));
                //vector2 is parallel to the vector pointing from current location to next location
                //the tail of vector2 is (0, 0)
                Point2D.Double vector2 = new Point2D.Double((locNextX - locCurrentX), (locNextY - locCurrentY));
                if (locCurrent.getFloorNumber() != locPrev.getFloorNumber()) {
                    System.out.println("if statement 1");
                    for (Edge e: locCurrent.getEdges()) {
                        System.out.println("for loop");
                        if (e.getNode1().equals(locCurrent) && e.getNode2().equals(locPrev) ||
                                e.getNode1().equals(locPrev) && e.getNode2().equals(locCurrent)) {
                            System.out.println("if statement 2");
                            if (e.hasAttribute(EdgeAttribute.STAIRS)
                                    && locCurrent.getFloorNumber() == locPrev.getFloorNumber() + 1) {
                                instruction.add("Go down the stairs\n");
                                instruction.add("");
                            } else if (e.hasAttribute(EdgeAttribute.STAIRS)
                                    && locCurrent.getFloorNumber() == locPrev.getFloorNumber() - 1) {
                                instruction.add("Go up the stairs\n");
                                instruction.add("");
                            } else if (e.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)
                                    && locCurrent.getFloorNumber() < locPrev.getFloorNumber()) {
                                instruction.add("Go down the elevator to " + locCurrent.getFloorNumber() + "\n");
                                instruction.add("");
                            } else if (e.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)
                                    && locCurrent.getFloorNumber() > locPrev.getFloorNumber()){
                                instruction.add("Go up the elevator to " + locCurrent.getFloorNumber() + "\n");
                                instruction.add("");
                            }
                            else
                            {
                                //do nothing
                            }
                        }
                    }

                } else {
                    if (i == 0) {
                        //vector3 is a vector point from (0, 0) to (1, 0)
                        Point2D.Double vector3 = new Point2D.Double(1, 0);
                        //length of vector2
                        double vector2Length = vector2.distance(0, 0);
                        //distance between vector2 and vector3
                        double l = vector2.distance(vector3);
                        //deg is the degree between vector2 and positive x-axis (vector3)
                        double deg = Math.toDegrees(
                                Math.acos((1 + vector2Length * vector2Length - l * l) / (2 * vector2Length)));
                        if (vector2.getY() == 0) {
                            if (vector2.getX() > 0) {
                                instruction.add("Head East\n");
                            } else if (vector2.getY() < 0) {
                                instruction.add("Head West\n");
                            }
                        } else if (vector2.getY() > 0) {
                            addFirstDirection(deg, "North");
                        } else if (vector2.getY() < 0) {
                            addFirstDirection(deg, "South");
                        }
                    }
                    String turn = "";
                    //determines if the vector2 rotates counterclockwise or clockwise from vector1
                    //math function v1 * v2 = |v1||v2|sin(x)
                    double cross = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
                    //sin(x) is negative if it rotates clockwise, sin(x) is positive if it rotates counterclockwise
                    //sin(x) is 0 if it rotates 180 degree
                    if (cross < 0) { //clockwise
                        flag = 1; //left
                        turn = "left";
                    } else if (cross > 0) { //counterclockwise
                        flag = 2; //right
                        turn = "right";
                    } else {
                        flag = 3; //straight
                    }

                    String str = " ";

                    //distance between previous node and current node
                    double l1 = makeScaledDistance(locCurrent.getPosition(), locPrev.getPosition(), scaleX, scaleY);
                    //distance between next node and current node
                    double l2 = makeScaledDistance(locCurrent.getPosition(), locNext.getPosition(), scaleX, scaleY);
                    //distance between previous node and next node
                    double l3 = makeScaledDistance(locPrev.getPosition(), locNext.getPosition(), scaleX, scaleY);

                    //if it is not going straight, check what degree need to turn
                    if (flag != 3) {
                        //calculates degree between previous edge and next edge(vector1 and vector2)
                        //Using inverse trigonometric functions and Law of cosines
                        double degree = Math.toDegrees(Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2)));
                        if (degree >= 170 || degree <= 10) {
                            flag = 3; //treats the small turn as going straight
                        } else if (degree > 120 && degree < 170) {
                            str = " slightly ";
                        } else if (degree < 60 && degree > 10) {
                            str = " hard ";
                        }
                    }
                    //if it is time to turn
                    if (flag == 1 || flag == 2) {
                        if (flag2 == 1) {
                            //if previous step is going straight, then add up all distance after last turing
                            distance += l1;
                        } else {
                            //if previous step is turning
                            distance = l1;
                        }
                        temp = this.make2Decimal(distance);
                        totalDistance += temp;
                        instruction.add("Go " + temp + " feet.\n");
                        for (int j = 0; j < count; j++) {
                            if (i != 0) {
                                instruction.add("Continue straight\n");
                                instruction.add("");
                            }
                        }
                        count = 0;
                        instruction.add("Turn" + str + turn + "\n");
                        //if next location is at the end of the location list
                        if (i == listSize - 2) {
                            temp = this.make2Decimal(l2);
                            instruction.add("Go " + temp + " feet.\n");
                            for (int j = 0; j < count; j++) {
                                if (i != 0) {
                                    instruction.add("Continue straight\n");
                                    instruction.add("");
                                }
                            }
                            totalDistance += temp;

                        }
                        flag2 = 0;
                        distance = 0;
                    } else {
                        //this step is going straight, add the distance between previous location and current location
                        distance += l1;
                        if (i != 0) {
                            count++;
                        }
                        //if next location is at the end of the location list
                        if (i == listSize - 2) {
                            distance += l2;
                            temp = this.make2Decimal(distance);
                            totalDistance += temp;
                            instruction.add("Go " + temp + " feet.\n");
                            for (int j = 0; j < count; j++) {
                                if (i != 0) {
                                    instruction.add("Continue straight\n");
                                    instruction.add("");
                                }
                            }
                        }
                        flag2 = 1;
                    }
                }
            }

            instruction.add("You arrive at your destination.\n");
            totalDistance = this.make2Decimal(totalDistance);
            instruction.add("The total distance is " + totalDistance + " feet.\n");
            //human's average walking speed is 3.1 miles per hour/16,368 feet per hour/273 feet per minute
            int timeNeed = (int) (totalDistance / 237);
            instruction.add("On average it takes " + timeNeed + " minutes to arrive at your destination.\n");
            return instruction;
        }
    }
    /**
     *
     * A helper function to make the distance be two decimal number.
     * @param distance distance wanted to save two decimal
     * @return the distance
     */
    private double make2Decimal(double distance){
        String temp = String.format(("%.2f"), distance);
        return Double.parseDouble(temp);
    }

    private double makeScaledDistance(Point2D.Double pos1, Point2D.Double pos2, int scaleX, int scaleY) {
        double x1 = pos1.x * scaleX;
        double y1 = pos1.y * scaleY;
        double x2 = pos2.x * scaleX;
        double y2 = pos2.y * scaleY;

        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * adds the first instruction.
     * @param deg degree between the vector2(parallel to the vector pointing from current location to next location)
     *            and x-axis
     * @param direction a possible direction
     */

    private void addFirstDirection(double deg, String direction) {
        if (deg < 10) {
            instruction.add("Head East\n");
        } else if (deg >= 10 && deg <= 80) {
            instruction.add("Head " + direction + " East\n");
        } else if (deg > 80 && deg < 100) {
            instruction.add("Head " + direction + "\n");
        } else if (deg >= 100 && deg <= 170) {
            instruction.add("Head " + direction + " West\n");
        } else if (deg > 170) {
            instruction.add("Head West\n");
        }
    }

    public List<String> getInstruction(){
        return instruction;
    }
}
