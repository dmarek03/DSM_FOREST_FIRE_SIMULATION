package simulation;

import simulation.records.BoardConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField mapWidthField;
    private JTextField mapHeightField;
    private JTextField windVelocityField;
    private JComboBox<Directions> windDirectionField;
    private JTextField mediumTreeAgeField;
    private JTextField mediumTreeAgeVarianceField;
    private JTextField mediumMoistureField;
    private JTextField mediumMoistureVarianceField;
    private JTextField treeBurningTemperatureField;
    private JTextField understoryBurningTemperatureField;
    private JTextField floorBurningTemperatureField;
    private JTextField litterBurningTemperatureField;
    private JTextField overcastField;
    private JTextField atmosphericPressureField;
    private JTextField maxFireTemperatureField;
    private JTextField sizeField;
    private JTextField pointPercentageField;

    public StartScreen() {
        setTitle("Forest Fire Simulation - Start");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel headingLabel = new JLabel("Put start Forest parameters", SwingConstants.CENTER);
        headingLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(headingLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(18, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mapWidthField = createField(formPanel, "Map Width:", "60");
        mapHeightField = createField(formPanel, "Map Height:", "60");
        windVelocityField = createField(formPanel, "Wind Velocity:", "5.0");
        windDirectionField = new JComboBox<>(Directions.values());
        formPanel.add(new JLabel("Wind Direction:"));
        formPanel.add(windDirectionField);
        mediumTreeAgeField = createField(formPanel, "Medium Tree Age:", "1.0");
        mediumTreeAgeVarianceField = createField(formPanel, "Medium Tree Age Variance:", "1.0");
        mediumMoistureField = createField(formPanel, "Medium Moisture:", "1.0");
        mediumMoistureVarianceField = createField(formPanel, "Medium Moisture Variance:", "1.0");
        treeBurningTemperatureField = createField(formPanel, "Tree Burning Temperature:", "1.0");
        understoryBurningTemperatureField = createField(formPanel, "Understory Burning Temperature:", "1.0");
        floorBurningTemperatureField = createField(formPanel, "Floor Burning Temperature:", "1.0");
        litterBurningTemperatureField = createField(formPanel, "Litter Burning Temperature:", "1.0");
        overcastField = createField(formPanel, "Overcast:", "1.0");
        atmosphericPressureField = createField(formPanel, "Atmospheric Pressure:", "1.0");
        maxFireTemperatureField = createField(formPanel, "Max Fire Temperature:", "1.0");
        sizeField = createField(formPanel, "Size:", "14");
        pointPercentageField = createField(formPanel, "Point Percentage:", "0.1");

        JButton startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.setSize(1440, 1080);
        this.setLocationRelativeTo(null); // Center the window
        this.setVisible(true);
    }

    private JTextField createField(JPanel panel, String label, String defaultValue) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField(defaultValue);
        panel.add(textField);
        return textField;
    }

    private void startSimulation() {
        int mapWidth = Integer.parseInt(mapWidthField.getText());
        int mapHeight = Integer.parseInt(mapHeightField.getText());
        double windVelocity = Double.parseDouble(windVelocityField.getText());
        Directions windDirection = (Directions) windDirectionField.getSelectedItem();
        double mediumTreeAge = Double.parseDouble(mediumTreeAgeField.getText());
        double mediumTreeAgeVariance = Double.parseDouble(mediumTreeAgeVarianceField.getText());
        double mediumMoisture = Double.parseDouble(mediumMoistureField.getText());
        double mediumMoistureVariance = Double.parseDouble(mediumMoistureVarianceField.getText());
        double treeBurningTemperature = Double.parseDouble(treeBurningTemperatureField.getText());
        double understoryBurningTemperature = Double.parseDouble(understoryBurningTemperatureField.getText());
        double floorBurningTemperature = Double.parseDouble(floorBurningTemperatureField.getText());
        double litterBurningTemperature = Double.parseDouble(litterBurningTemperatureField.getText());
        double overcast = Double.parseDouble(overcastField.getText());
        double atmosphericPressure = Double.parseDouble(atmosphericPressureField.getText());
        double maxFireTemperature = Double.parseDouble(maxFireTemperatureField.getText());
        int size = Integer.parseInt(sizeField.getText());
        double pointPercentage = Double.parseDouble(pointPercentageField.getText());

        BoardConfig config = new BoardConfig(
                mapWidth,
                mapHeight,
                windVelocity,
                windDirection,
                mediumTreeAge,
                mediumTreeAgeVariance,
                mediumMoisture,
                mediumMoistureVariance,
                treeBurningTemperature,
                understoryBurningTemperature,
                floorBurningTemperature,
                litterBurningTemperature,
                overcast,
                atmosphericPressure,
                maxFireTemperature,
                size,
                pointPercentage
        );

        new Program(config);
        dispose();
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}
