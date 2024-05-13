import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 14;
	private double pointPercentage;

	private int mapWidth;
	private int mapHeight;
	private double mediumConiferousHeight;
	private double mediumDeciduousHeight;
	private double mediumUnderstoryHeight;
	private double mediumFloorHeight;
	private double mediumConiferousHeightVariance;
	private double mediumDeciduousHeightVariance;
	private double mediumUnderstoryHeightHeightVariance;
	private double mediumFloorHeightVariance;
	private double windVelocity;
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

	public Board(int length, int height,double pointPercentage) {
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
				points[x][y].calculateNewState();

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].changeState();
		this.repaint();
	}

	// clearing board
	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].setState(0);
			}
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y] = new Point();

				if(Math.random() < pointPercentage){
					points[x][y].setState(0);
				}
			}
		}



		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				// Moore'a Neighbourhood
				int startX = Math.max(0, x - 1);
				int endX = Math.min(points.length - 1, x + 1);

				int startY = Math.max(0, y - 1);
				int endY = Math.min(points[x].length - 1, y + 1);

				for (int i = startX; i <= endX; i++) {
					for (int j = startY; j <= endY; j++) {
						if (x != i || y != j) {
							points[x][y].addNeighbor(points[i][j]);
						}
					}
				}
			}
		}


	}

	//paint background and separators between cells
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
				if (points[x][y].getState() != 0) {
					switch (points[x][y].getState()) {
					case 1:
						g.setColor(new Color(0x00ff00));

						break;
					case 2:
						g.setColor(new Color(0xff0000));

						break;
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
			points[x][y].clicked();
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			points[x][y].setState(1);
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
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
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
