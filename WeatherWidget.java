import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONObject;

public class WeatherWidget extends JFrame {

    private JLabel weatherLabel;
    private JLabel temperatureLabel;
    private JLabel windSpeedLabel;
    private JLabel windDirectionLabel;
    private JLabel pressureLabel;
    private JLabel humidityLabel;
    private Font font;
    private int posX = 100, posY = 100;
    private Color backgroundColor = new Color(0, 0, 0, 200);
    private Color foregroundColor = Color.WHITE;
    private ImageIcon sunnyIcon;
    private ImageIcon cloudyIcon;
    private ImageIcon rainyIcon;
    private ImageIcon snowyIcon;
    private ImageIcon foggyIcon;
    private ImageIcon thunderstormIcon;
    private ImageIcon dustyIcon;

    private static final int ICON_WIDTH = 50; // Desired width
    private static final int ICON_HEIGHT = 50; // Desired height

    public WeatherWidget() {
        // Load the font
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\res\\font.OTF"));
        } catch (FontFormatException | IOException ffe) {
            ffe.printStackTrace();
        }

        // Load and resize the weather icons
        sunnyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\sunny.png");
        cloudyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\cloudy.png");
        rainyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\rainy.png");
        snowyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\snow.png");
        foggyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\foggy.png");
        thunderstormIcon = loadIcon(System.getProperty("user.dir") + "\\res\\thunderstorm.png");
        dustyIcon = loadIcon(System.getProperty("user.dir") + "\\res\\dusty.png");

        // Load the widget position
        loadPosition();

        setUndecorated(true);
        setSize(400, 400); // Increase the height to accommodate more information
        setLocation(posX, posY);
        setBackground(new Color(0, 0, 0, 0));  // Transparent background
        setAlwaysOnTop(false);
        setFocusableWindowState(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
        RoundedPanel roundedPanel = new RoundedPanel();
        roundedPanel.setLayout(null);

        // Initialize the weather and additional parameter labels
        weatherLabel = new JLabel();
        temperatureLabel = new JLabel();
        windSpeedLabel = new JLabel();
        windDirectionLabel = new JLabel();
        pressureLabel = new JLabel();
        humidityLabel = new JLabel();

        // Set label properties
        weatherLabel.setForeground(foregroundColor);
        temperatureLabel.setForeground(foregroundColor);
        windSpeedLabel.setForeground(foregroundColor);
        windDirectionLabel.setForeground(foregroundColor);
        pressureLabel.setForeground(foregroundColor);
        humidityLabel.setForeground(foregroundColor);

        weatherLabel.setBounds(25, 20, 350, 50);
        temperatureLabel.setBounds(25, 80, 350, 50);
        windSpeedLabel.setBounds(25, 140, 350, 50);
        windDirectionLabel.setBounds(25, 200, 350, 50);
        pressureLabel.setBounds(25, 260, 350, 50);
        humidityLabel.setBounds(25, 320, 350, 50);

        // Set custom fonts
        weatherLabel.setFont(font.deriveFont(24.0f));
        temperatureLabel.setFont(font.deriveFont(24.0f));
        windSpeedLabel.setFont(font.deriveFont(24.0f));
        windDirectionLabel.setFont(font.deriveFont(24.0f));
        pressureLabel.setFont(font.deriveFont(24.0f));
        humidityLabel.setFont(font.deriveFont(24.0f));

        // Add labels to the rounded panel
        roundedPanel.add(weatherLabel);
        roundedPanel.add(temperatureLabel);
        roundedPanel.add(windSpeedLabel);
        roundedPanel.add(windDirectionLabel);
        roundedPanel.add(pressureLabel);
        roundedPanel.add(humidityLabel);

        // Update the weather labels initially
        updateWeather();

        // Create and start the timer to update weather every 5 minutes
        Timer timer = new Timer(300000, e -> updateWeather());
        timer.start();

        // Create the "Edit" button
        JButton editButton = new RoundedButton("Edit");
        editButton.setFocusPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setBorderPainted(false);
        editButton.setBounds(300, 360, 90, 30);
        editButton.setFont(font.deriveFont(12.0f));
        editButton.setBackground(roundedPanel.getBackground());
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> {
            if (editButton.getText().equals("Edit")) {
                enableDragging();
                editButton.setText("Save");
            } else {
                disableDragging();
                editButton.setText("Edit");
                savePosition();
            }
        });
        roundedPanel.add(editButton);

        // Add the rounded panel to the frame
        setContentPane(roundedPanel);
    }

    private void updateWeather() {
        WeatherData weatherData = getWeatherData(weatherSetting("apiKey", ""), weatherSetting("city", "London"));
        ImageIcon conditionIcon = getIconForCondition(weatherData.condition);
        if (conditionIcon != null) {
            weatherLabel.setIcon(conditionIcon);
        }
        weatherLabel.setText(weatherData.condition);
        temperatureLabel.setText("Temperature: " + weatherData.temperature + "°C");
        windSpeedLabel.setText("Wind Speed: " + weatherData.windSpeed + " km/h");
        windDirectionLabel.setText("Wind Direction: " + weatherData.windDirection);
        pressureLabel.setText("Pressure: " + weatherData.pressure + " hPa");
        humidityLabel.setText("Humidity: " + weatherData.humidity + " %");
    }

    private WeatherData getWeatherData(String apiKey, String cityName) {
        String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + cityName;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder inline = new StringBuilder();
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject data = new JSONObject(inline.toString());
                JSONObject current = data.getJSONObject("current");
                String condition = current.getJSONObject("condition").getString("text");
                String temperature = String.valueOf(current.getDouble("temp_c"));
                String windSpeed = String.valueOf(current.getDouble("wind_kph"));
                String windDirection = current.getString("wind_dir");
                String pressure = String.valueOf(current.getDouble("pressure_mb"));
                String humidity = String.valueOf(current.getDouble("humidity"));

                return new WeatherData(condition, temperature, windSpeed, windDirection, pressure, humidity);
            } else {
                System.err.println("Error: " + connection.getResponseMessage());
                return new WeatherData("Weather data not available", "--", "--", "--", "--", "--");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherData("Weather data not available", "--", "--", "--", "--", "--");
        }
    }

    private ImageIcon getIconForCondition(String condition) {
        switch (condition.toLowerCase()) {
            case "clear": case "sunny" :
                return resizeIcon(sunnyIcon, ICON_WIDTH, ICON_HEIGHT);
            case "cloudy": case "partly cloudy":
                return resizeIcon(cloudyIcon, ICON_WIDTH, ICON_HEIGHT);
            case "rain": case "rainy": case "moderate rain": case "patchy rain nearby": case "patchy rain possible":
                return resizeIcon(rainyIcon, ICON_WIDTH, ICON_HEIGHT);
            case "snow": case "snowy": case "patchy snow nearby": case "patchy snow possible":
                return resizeIcon(snowyIcon, ICON_WIDTH, ICON_HEIGHT);
            case "fog": case "foggy": case "mist": case "freezing fog":
                return resizeIcon(foggyIcon, ICON_WIDTH, ICON_HEIGHT);
            case "thunderstorm": case "moderate or heavy rain with thunder":
                return resizeIcon(thunderstormIcon, ICON_WIDTH, ICON_HEIGHT);
            case "dusty": case "dust storm":
                return resizeIcon(dustyIcon, ICON_WIDTH, ICON_HEIGHT);
            default:
                return null;
        }
    }

    private ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        return resizeIcon(icon, ICON_WIDTH, ICON_HEIGHT);
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void loadPosition() {
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("widget.properties");
            properties.load(inputStream);
            inputStream.close();
            posX = Integer.parseInt(properties.getProperty("posX", "100"));
            posY = Integer.parseInt(properties.getProperty("posY", "100"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePosition() {
        Properties properties = new Properties();
        properties.setProperty("posX", Integer.toString(getX()));
        properties.setProperty("posY", Integer.toString(getY()));
        try {
            FileOutputStream outputStream = new FileOutputStream("widget.properties");
            properties.store(outputStream, null);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableDragging() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int mouseX, mouseY;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - mouseX, getLocation().y + e.getY() - mouseY);
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void disableDragging() {
        for (MouseListener listener : getMouseListeners()) {
            removeMouseListener(listener);
        }
        for (MouseMotionListener listener : getMouseMotionListeners()) {
            removeMouseMotionListener(listener);
        }
    }

    private class RoundedPanel extends JPanel {
        //private static final long serialVersionUID = 1L;

        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,215));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        }
    }

    private static class RoundedButton extends JButton {
        public RoundedButton(String label) {
            super(label);
            setBackground(new Color(0, 0, 0, 0));
            setOpaque(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border painting
        }
    }

    private static class WeatherData {
        String condition;
        String temperature;
        String windSpeed;
        String windDirection;
        String pressure;
        String humidity;

        WeatherData(String condition, String temperature, String windSpeed, String windDirection, String pressure, String humidity) {
            this.condition = condition;
            this.temperature = temperature;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
            this.pressure = pressure;
            this.humidity = humidity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherWidget().setVisible(true));
    }

    /** The WeatherAPI key and city live in weather.properties next to the app, not in the code. */
    private static String weatherSetting(String name, String fallback) {
        Properties settings = new Properties();
        try (FileInputStream in = new FileInputStream("weather.properties")) {
            settings.load(in);
        } catch (IOException e) {
            // no settings file yet: fall back to the default
        }
        return settings.getProperty(name, fallback).trim();
    }
}
