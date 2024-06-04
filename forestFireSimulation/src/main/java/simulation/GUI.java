package simulation;

import simulation.components.TextAreaRenderer;
import simulation.records.PointStatistics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Class containing simulation.GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 1L;
    private Timer timer;
    private Board board;
    private JPanel statsPanel;
    private JButton start;
    private JButton clear;
    private JSlider pred;
    private JFrame frame;
    private JComboBox<PointStates> drawType;
    private int iterNum = 0;
    private final int maxDelay = 500;
    private final int initDelay = 100;
    private boolean running = false;
    private Font statsFont = new Font("SansSerif", Font.PLAIN, 12);
    private DecimalFormat decimalFormat = new DecimalFormat("#.####");
    private JLabel initialMessageLabel;

    public GUI(JFrame jf) {
        frame = jf;
        timer = new Timer(initDelay, this);
        timer.stop();
    }

    /**
     * @param container to which simulation.GUI and board is added
     */
    public void initialize(Container container) {
        // simulation.Board
        container.setLayout(new BorderLayout());
        container.setSize(new Dimension(1024, 768));

        // Button Panel
        JPanel buttonPanel = new JPanel();

        start = new JButton("Start");
        start.setActionCommand("Start");
        start.setToolTipText("Starts clock");
        start.addActionListener(this);

        clear = new JButton("Clear");
        clear.setActionCommand("clear");
        clear.setToolTipText("Clears the board");
        clear.addActionListener(this);

        pred = new JSlider();
        pred.setMinimum(0);
        pred.setMaximum(maxDelay);
        pred.setToolTipText("Time speed");
        pred.addChangeListener(this);
        pred.setValue(maxDelay - timer.getDelay());

        drawType = new JComboBox<PointStates>(Point.types);
        drawType.addActionListener(this);
        drawType.setActionCommand("drawType");

        buttonPanel.add(start);
        buttonPanel.add(clear);
        buttonPanel.add(pred);
        buttonPanel.add(drawType);

        // Stats Panel
        statsPanel = new JPanel();
        statsPanel.setPreferredSize(new Dimension(200, 600));
        statsPanel.setLayout(new BorderLayout());
        initialMessageLabel = new JLabel("<html><div style='text-align: center;'>Move the mouse over the board to see stats</div></html>", SwingConstants.CENTER);
        initialMessageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        initialMessageLabel.setVerticalAlignment(SwingConstants.CENTER);
        statsPanel.add(initialMessageLabel, BorderLayout.CENTER);

        board = new Board(this, 1024, 768 - buttonPanel.getHeight(), 0.1);
        container.add(board, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        container.add(statsPanel, BorderLayout.WEST);
    }

    /**
     * handles clicking on each button
     *
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(timer)) {
            iterNum++;
            frame.setTitle("Forest Fire Simulation (" + Integer.toString(iterNum) + " iteration)");
            board.iteration();
        } else {
            String command = e.getActionCommand();
            if (command.equals("Start")) {
                if (!running) {
                    timer.start();
                    start.setText("Pause");
                } else {
                    timer.stop();
                    start.setText("Start");
                }
                running = !running;
                clear.setEnabled(true);

            } else if (command.equals("clear")) {
                iterNum = 0;
                timer.stop();
                start.setEnabled(true);
                board.clear();
                frame.setTitle("Cellular Automata Toolbox");
            } else if (command.equals("drawType")) {
                board.editType = PointStates.fromDescription(drawType.getSelectedItem().toString());
            }

        }
    }

    /**
     * slider to control simulation speed
     *
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        timer.setDelay(maxDelay - pred.getValue());
    }

    public void statsChanged(Point point, int x, int y) {
        statsPanel.removeAll();

        PointStatistics stats = point.toPointStatistics(x, y);

        String[] columnNames = {"Property", "Value"};
        Object[][] data = {
                {"Coordinates", "(" + stats.pointX() + ", " + stats.pointY() + ")"},
                {"Elevation", stats.elevation()},
                {"Litter", stats.litter()},
                {"Floor", stats.floor()},
                {"Understory", stats.understory()},
                {"Coniferous", stats.coniferous()},
                {"Deciduous", stats.deciduous()},
                {"Height", formatDecimal(stats.height())},
                {"Fire Source", stats.fireSource()},
                {"Humidity", formatDecimal(stats.humidity())},
                {"Current State", stats.currentState()},
                {"States", listToString(stats.state())},
                {"Temperatures", listToString(stats.temperature())},
                {"On Fire", listToString(stats.onFire())}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable statsTable = new JTable(model) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                return super.prepareRenderer(renderer, row, column);
            }
        };
        statsTable.setFont(statsFont);
        statsTable.setFillsViewportHeight(true);

        statsTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());

        adjustRowHeights(statsTable);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        statsPanel.add(scrollPane, BorderLayout.CENTER);

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    public void showInitialMessage() {
        statsPanel.removeAll();
        statsPanel.add(initialMessageLabel, BorderLayout.CENTER);
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void adjustRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            Component comp = table.prepareRenderer(table.getCellRenderer(row, 1), row, 1);
            rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            table.setRowHeight(row, rowHeight);
        }
    }

    private String formatDecimal(double value) {
        return decimalFormat.format(value);
    }

    private String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            sb.append(item.toString()).append("\n");
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
