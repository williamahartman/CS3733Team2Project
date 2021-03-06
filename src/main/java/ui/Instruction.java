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
    //private List<String> instruction;
    private LinkedHashMap<StartEnd, String> instruction;
    private List<String> totals;
    private double totalDistance;
    private int count;
    private StartEnd prev;

    public Instruction(){
        //this.instruction = new ArrayList<>();
        this.totals = new ArrayList<>();
        this.instruction = new LinkedHashMap<>();
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
    public LinkedHashMap<StartEnd, String> stepByStepInstruction(List<Location> locList, int scaleX, int scaleY) {
        totalDistance = 0.0;
        int flag; //records the relation between previous edge and next edge
        int flag2 = 0; //records the relation between previous edge and the edge before previous edge
        int flagInit = 0; //checks whether "straight" direction has been added after N/S/E/W direction
        double distance = 0; //records the sum of distance after previous turning
        //double distance2 = 0; //stores the distance between previous location and current location
        int listSize = locList.size();
        int temp; //used to add appropriate distance to the instruction


        for (int i = 0; i < (listSize - 1); i++) {
            String curInst = "";
            String currentInst = "";

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

            // To be used to add to the hashmap
            StartEnd cur = new StartEnd(locCurrent, locNext);
            /*if (i != 0) {
                prev = new StartEnd(locPrev, locCurrent);
            } else {
                prev = cur;
            }*/
            //System.out.print("Init StartEnd and strings\n");

            //vector1 is parallel to vector pointing from current location to previous location
            //the tail of vector1 is (0, 0)
            Point2D.Double vector1 = new Point2D.Double((locPrevX - locCurrentX), (locPrevY - locCurrentY));
            //vector2 is parallel to the vector pointing from current location to next location
            //the tail of vector2 is (0, 0)
            Point2D.Double vector2 = new Point2D.Double((locNextX - locCurrentX), (locNextY - locCurrentY));
            if (locCurrent.getFloorNumber() != locPrev.getFloorNumber()) {
                //System.out.println("if statement 1");
                for (Edge e : locCurrent.getEdges()) {
                    if (e.getNode1().equals(locCurrent) && e.getNode2().equals(locPrev) ||
                            e.getNode1().equals(locPrev) && e.getNode2().equals(locCurrent)) {
                        //System.out.println("if statement 2");
                        if (e.hasAttribute(EdgeAttribute.STAIRS)
                                && locCurrent.getFloorNumber() == locPrev.getFloorNumber() + 1) {
                            instruction.put(cur, "Go up the stairs\n");
                            //instruction.add("");
                        } else if (e.hasAttribute(EdgeAttribute.STAIRS)
                                && locCurrent.getFloorNumber() == locPrev.getFloorNumber() - 1) {
                            instruction.put(cur, "Go down the stairs\n");
                            //                            instruction.add("Go down the stairs\n");
                            //                            instruction.add("");
                        } else if (e.hasAttribute(EdgeAttribute.STAIRS)
                                && locCurrent.getFloorNumber() == locPrev.getFloorNumber()) {
                            instruction.put(cur, "Take the stairs\n");
                        } else if (e.hasAttribute(EdgeAttribute.ELEVATOR)) {
                            String elevatorFloorText = "";
                            int floorDif = locCurrent.getFloorNumber() - locPrev.getFloorNumber();
                            if (floorDif > 0) {
                                elevatorFloorText = "Take the elevator up " + floorDif + " floor";
                                elevatorFloorText += floorDif == 1 ? "" : "s";
                            } else {
                                elevatorFloorText = "Take the elevator down " + -floorDif + " floor";
                                elevatorFloorText += floorDif == 1 ? "" : "s";
                            }

                            instruction.put(cur, elevatorFloorText);

                        } else {
                            //do nothing
                        }
                    }
                }
            } else {
                //System.out.print("In else statement\n");
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
                            curInst = curInst + "Head East. ";
                        } else if (vector2.getY() < 0) {
                            curInst = curInst + "Head West. ";
                        }
                        instruction.put(cur, curInst);
                    } else if (vector2.getY() > 0) {
                        if (deg < 22.5) {
                            curInst = curInst + "Head East. ";
                        } else if (deg >= 22.5 && deg <= 67.5) {
                            curInst = curInst + "Head North East. ";
                        } else if (deg > 67.5 && deg < 112.5) {
                            curInst = curInst + "Head North. ";
                        } else if (deg >= 112.5 && deg <= 157.5) {
                            curInst = curInst + "Head North West. ";
                        } else if (deg > 157.5) {
                            curInst = curInst + "Head West. ";
                        }

                    } else if (vector2.getY() < 0) {
                        if (deg < 22.5) {
                            curInst = curInst + "Head East. ";
                        } else if (deg >= 22.5 && deg <= 67.5) {
                            curInst = curInst + "Head South East. ";
                        } else if (deg > 67.5 && deg < 112.5) {
                            curInst = curInst + "Head South. ";
                        } else if (deg >= 112.5 && deg <= 157.5) {
                            curInst = curInst + "Head South West. ";
                        } else if (deg > 157.5) {
                            curInst = curInst + "Head West. ";
                        }
                    }
                    if (!curInst.isEmpty()) {
                        //System.out.println("Adding heading inst");
                        instruction.put(cur, curInst);
                    }
                    //System.out.print(instruction.get(cur));
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
                    //System.out.println("Time to turn\n");
                    flagInit = 1;

                    if (flag2 == 1) {
                        //if previous step is going straight, then add up all distance after last turning
                        distance += l1;
                    } else {
                        //if previous step is turning
                        distance = l1;
                    }
                    temp = this.make2Decimal(distance);
                    totalDistance += temp;

                    String prevInst = instruction.get(prev);
                    if (prevInst != null) {
                        prevInst = prevInst + "<br>" + "Go " + temp + " feet. ";
                        instruction.remove(prev);
                        instruction.put(prev, prevInst);
                        //System.out.println("i" + ":" + i + prevInst);
                    } else {
                      //System.out.println("UH OH PREV WAS NULL");
                    }

                    count = 0;

                    curInst = "Turn" + str + turn + ". ";
                    //curInst = curInst + "Turn" + str + turn + ". ";
                    //System.out.println("i" + ":" + i + curInst);
                    //if next location is at the end of the location list
                    if (i == listSize - 2) {
                        temp = this.make2Decimal(l2);
                        curInst = curInst + "Go " + temp + " feet.";
                        //System.out.println("Go" + ":" + i + curInst);
                        for (int j = 0; j < count; j++) {
                            if (i != 0) {
                                //curInst = curInst + "Continue straight. ";
                                //System.out.println("i" + ":" + i + curInst);
                            }
                        }
                        totalDistance += temp;
                    }
                    if (!curInst.isEmpty()) {
                        //System.out.println("Adding go/turn: " + curInst);
                        instruction.put(cur, curInst);
                    }
                    flag2 = 0;
                    distance = 0;
                    prev = cur;
                }
                else {
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
                        currentInst = currentInst + "Go " + temp + " feet.";
                        //System.out.println("i2" + ":" + i + currentInst);

                        if (flagInit != 0) {
                            String tempStr = instruction.get(prev);
                            tempStr = tempStr + currentInst;
                            //System.out.println("Adding1: " + tempStr);
                            instruction.remove(prev);
                            instruction.put(prev, tempStr);
                        }
                    }
                    if (!curInst.isEmpty() && !instruction.containsKey(cur)) {
                        //System.out.println("Adding go inst");
                        instruction.put(cur, currentInst);
                    } else if (!curInst.isEmpty()) {
                        String tempStr = instruction.get(cur);
                        tempStr = tempStr + currentInst;
                        //System.out.println("Adding2: " + tempStr);
                        instruction.remove(cur);
                        instruction.put(cur, tempStr);
                    }

                    flag2 = 1;
                    if (i == 0) {
                        //Set the prev equal to the current if going through loop for first time
                        prev = cur;
                    }
                }
            }
        }

    StartEnd end = new StartEnd(locList.get(locList.size() - 1), locList.get(locList.size() - 1));
    instruction.put(end, "You have arrived at your destination.");

    //totals.add("You have arrived at your destination.\n");
    totalDistance = this.make2Decimal(totalDistance);
    totals.add("Total distance: " + (int) totalDistance + " feet\n");

    //human's average walking speed is 3.1 miles per hour/16,368 feet per hour/273 feet per minute
    int timeNeed = (int) (totalDistance / 237);
    totals.add("Average arrival time: " + timeNeed + " minutes \n");

    return instruction;
}


    public List<String> getTotals() {
        return totals;
    }

    /**
     * adds the first instruction.
     * @param cur current location to add to hashmap
     * @param next next location to add to hashmap
     * @param deg degree between the vector2(parallel to the vector pointing from current location to next location)
     *            and x-axis
     * @param direction a possible direction
     */

    private void addFirstDirection(Location cur, Location next, double deg, String current, String direction) {
        /*if (deg < 10) {
            instruction.add("Head East\n");
        } else if (deg >= 10 && deg <= 80) {
            instruction.add("Head " + direction + " East\n");
        } else if (deg > 80 && deg < 100) {
            instruction.add("Head " + direction + "\n");
        } else if (deg >= 100 && deg <= 170) {
            instruction.add("Head " + direction + " West\n");
        } else if (deg > 170) {
            instruction.add("Head West\n");
        }*/
        StartEnd mapLoc = new StartEnd(cur, next);
        if (deg < 22.5) {
            //instruction.add("Head East\n");
            current = current + "Head East. ";
        } else if (deg >= 22.5 && deg <= 67.5) {
            //instruction.add("Head " + direction + " East\n");
            current = current + "Head North East";
        } else if (deg > 67.5 && deg < 112.5) {
            //instruction.add("Head " + direction + "\n");
            current = current + "Head North";
        } else if (deg >= 112.5 && deg <= 157.5) {
            //instruction.add("Head " + direction + " West\n");
            current = current + "Head North West";
        } else if (deg > 157.5) {
            //instruction.add("Head West\n");
            current = current + "Head West";
        }
        System.out.println("put heading");
        instruction.put(mapLoc, current);
    }

    /*public List<String> getInstruction(){
        return instruction;
    }*/
    public HashMap<StartEnd, String> getInstruction(){
        return instruction;
    }

    /**
     *
     * A helper function to make the distance be two decimal number.
     * @param distance distance wanted to save two decimal
     * @return the distance
     */
    private int make2Decimal(double distance){
        String temp = String.format(("%.2f"), distance);
        double math = Double.parseDouble(temp);
        return (int) math;
    }

    private double makeScaledDistance(Point2D.Double pos1, Point2D.Double pos2, int scaleX, int scaleY) {
        double x1 = pos1.x * scaleX;
        double y1 = pos1.y * scaleY;
        double x2 = pos2.x * scaleX;
        double y2 = pos2.y * scaleY;

        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
