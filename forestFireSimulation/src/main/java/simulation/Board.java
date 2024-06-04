package simulation;

import simulation.records.TrackedPoint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * simulation.Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points = new Point[0][0];
    public PointStates editType = PointStates.NO_FIRE;
    private int size = 14;
    private double pointPercentage;

    private int mapWidth = 60;
    private int mapHeight = 60;
    private double windVelocity = 5.0;
    private Directions windDirection;
    private double mediumTreeAge;
    private double mediumTreeAgeVariance;
    private double mediumMoisture;
    private double mediumMoistureVariance;
    private double treeBurningTemperature;
    private double understoryBurningTemperature;
    private double floorBurningTemperature;
    private double litterBurningTemperature;
    private double overcast;
    private double atmosphericPressure;
    private double maxFireTemperature;

    private final GUI gui;
    private TrackedPoint trackedPoint = null;

    public Board(GUI gui, int length, int height, double pointPercentage) {
        this.gui = gui;
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
        this.pointPercentage = pointPercentage;
    }

    // single iteration
    public void iteration() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].calculateNewState(windVelocity);

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].update();

        if (trackedPoint != null) {
            gui.statsChanged(trackedPoint.point(), trackedPoint.pointX(), trackedPoint.pointY());
        } else {
            gui.showInitialMessage();
        }

        this.repaint();
    }

    // clearing board
    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].initializeEmpty();
            }
        this.repaint();
    }

    private void initializeBoard(int width, int height) {
        for (int x = 8; x < 8 + width; ++x) {
            for (int y = 5; y < 5 + height; ++y) {
                Random rnd = new Random();
                switch (rnd.nextInt(5)) {
                    case 0:
                        points[x][y].initializeLitter();
                        break;
                    case 1:
                        points[x][y].initializeFloor();
                        break;
                    case 2:
                        points[x][y].initializeUnderstory();
                        break;
                    case 3:
                        points[x][y].initializeConiferous();
                        break;
                    case 4:
                        points[x][y].initializeDeciduous();
                        break;
                }
            }
        }
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];


        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y] = new Point(x, y);

                if (Math.random() < pointPercentage) {
                    points[x][y].currentState = PointStates.NO_FIRE;
                }
            }
        }
        initializeBoard(mapWidth, mapHeight);

        for (int x = 1; x < points.length - 1; ++x) {
            for (int y = 1; y < points[x].length - 1; ++y) {
                // Moore'a Neighbourhood
                points[x][y].addNeighbor(points[x][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y]);
                points[x][y].addNeighbor(points[x][y + 1]);
                points[x][y].addNeighbor(points[x - 1][y]);
                points[x][y].addNeighbor(points[x - 1][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y + 1]);
                points[x][y].addNeighbor(points[x - 1][y + 1]);
            }
        }
    }

    // Paint background and separators between cells
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
        drawNetting(g, size);
    }

    // draws the background netting
    private void drawNetting(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }

        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {
                if (points[x][y].getState() != PointStates.NO_FIRE) {
                    switch (points[x][y].getState()) {
                        case LITTER:
                            g.setColor(new Color(0x59350e));

                            break;
                        case FLOOR:
                            g.setColor(new Color(0x9c6427));

                            break;
                        case UNDERSTORY:
                            g.setColor(new Color(0xadd962));

                            break;
                        case CONIFEROUS:
                            g.setColor(new Color(0x364f0d));

                            break;
                        case DECIDUOUS:
                            g.setColor(new Color(0x6aa60a));

                            break;
                    }
                    for (int i = 0; i < Point.LEVELS; i++) {
                        if (points[x][y].state.get(i) < 1) {
                            g.setColor(new Color(0x333333));
                        }
                        if (points[x][y].onFire.get(i) == Boolean.TRUE) {
                            if (points[x][y].temperature.get(i) < 1000)
                                g.setColor(new Color(0xff0000));
                            else if (points[x][y].temperature.get(i) < 1200)
                                g.setColor(new Color(0xffa500));
                            else
                                g.setColor(new Color(0xffd700));
                            break;
                        }
                    }

                    g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
                }
            }
        }
    }


    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == PointStates.LITTER) {
                points[x][y].initializeLitter();
            } else if (editType == PointStates.FLOOR) {
                points[x][y].initializeFloor();
            } else if (editType == PointStates.UNDERSTORY) {
                points[x][y].initializeUnderstory();
            } else if (editType == PointStates.CONIFEROUS) {
                points[x][y].initializeConiferous();
            } else if (editType == PointStates.DECIDUOUS) {
                points[x][y].initializeDeciduous();
            } else if (editType == PointStates.FIRE) {
                points[x][y].addFireSource();
            } else {
                points[x][y].initializeEmpty();
            }
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
        int width = (this.getWidth() / size) + 1;
        int height = (this.getHeight() / size) + 1;
        initialize(width, height);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == PointStates.LITTER) {
                points[x][y].initializeLitter();
            } else if (editType == PointStates.FLOOR) {
                points[x][y].initializeFloor();
            } else if (editType == PointStates.UNDERSTORY) {
                points[x][y].initializeUnderstory();
            } else if (editType == PointStates.CONIFEROUS) {
                points[x][y].initializeConiferous();
            } else if (editType == PointStates.DECIDUOUS) {
                points[x][y].initializeDeciduous();
            } else if (editType == PointStates.FIRE) {
                points[x][y].addFireSource();
            } else {
                points[x][y].initializeEmpty();
            }
            this.repaint();
        }
    }

    public void mouseExited(MouseEvent e) {
        gui.showInitialMessage();
        trackedPoint = null;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            gui.statsChanged(points[x][y], x, y);
            trackedPoint = new TrackedPoint(points[x][y], x, y);
        } else {
            gui.showInitialMessage();
            trackedPoint = null;
        }
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

}
