import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONObject;

public class MixWidget extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;
    private JLabel weatherLabel;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;
    private Timer timer, timer2;
    private Font font;
    private int posX, posY;
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

    public MixWidget() {
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
        setSize(400, 300);
        setLocation(posX, posY);
        setBackground(new Color(0, 0, 0, 0));  // Transparent background
        setAlwaysOnTop(false);
        setFocusableWindowState(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RoundedPanel roundedPanel = new RoundedPanel();
        roundedPanel.setLayout(null);

        // Initialize the time, date, and weather labels
        timeLabel = new JLabel();
        dateLabel = new JLabel();
        weatherLabel = new JLabel();

        // Set label properties
        timeLabel.setForeground(foregroundColor);
        dateLabel.setForeground(foregroundColor);
        weatherLabel.setForeground(foregroundColor);
        timeLabel.setBounds(30, 20, 380, 100);
        dateLabel.setBounds(90, 120, 350, 50);
        weatherLabel.setBounds(5, 220, 400, 50);

        // Set custom fonts
        timeLabel.setFont(font.deriveFont(75.0f));
        dateLabel.setFont(font.deriveFont(36.0f));
        weatherLabel.setFont(font.deriveFont(24.0f));

        // Add labels to the rounded panel
        roundedPanel.add(timeLabel);
        roundedPanel.add(dateLabel);
        roundedPanel.add(weatherLabel);

        // Initialize date formats
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Update the time, date, and weather labels initially
        updateDateTime();
        updateWeather();

        // Create and start timers
        timer = new Timer(1000, e -> updateDateTime());
        timer.start();
        timer2 = new Timer(300000, e -> updateWeather());
        timer2.start();

        // Create the "Edit" button
        JButton editButton = new RoundedButton("Edit");
        editButton.setFocusPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setBorderPainted(false);
        editButton.setBounds(300, 260, 90, 30);
        editButton.setFont(font.deriveFont(12.0f));
        editButton.setBackground(new Color(0, 0, 0, 100));
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

    private void updateDateTime() {
        Date now = new Date();
        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));
    }

    private void updateWeather() {
        WeatherData weatherData = getWeatherData(weatherSetting("apiKey", ""), weatherSetting("city", "London"));
        ImageIcon conditionIcon = getIconForCondition(weatherData.condition);
        if (conditionIcon != null) {
            weatherLabel.setIcon(conditionIcon);
        }
        weatherLabel.setText(weatherData.condition + ", " + weatherData.temperature + "°C");
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

                return new WeatherData(condition, temperature);
            } else {
                System.err.println("Error: " + connection.getResponseMessage());
                return new WeatherData("Weather data not available", "--");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherData("Weather data not available", "--");
        }
    }

    private ImageIcon getIconForCondition(String condition) {
        switch (condition.toLowerCase()) {
            case "clear":
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
        try {
            File file = new File(path);
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            } else {
                System.err.println("Image not found at path: " + path);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void enableDragging() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posX = e.getX();
                posY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                savePosition();
            }
        });
    }

    private void disableDragging() {
        for (MouseListener listener : getMouseListeners()) {
            removeMouseListener(listener);
        }
        for (MouseMotionListener listener : getMouseMotionListeners()) {
            removeMouseMotionListener(listener);
        }
    }

    private void loadPosition() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("widget.properties")) {
            props.load(in);
            posX = Integer.parseInt(props.getProperty("posX", "200"));
            posY = Integer.parseInt(props.getProperty("posY", "200"));
            backgroundColor = new Color(Integer.parseInt(props.getProperty("backgroundColor", String.valueOf(backgroundColor.getRGB()))));
            foregroundColor = new Color(Integer.parseInt(props.getProperty("foregroundColor", String.valueOf(foregroundColor.getRGB()))));
        } catch (IOException e) {
            posX = 500;
            posY = 600;
        }
    }

    private void savePosition() {
        Properties props = new Properties();
        props.setProperty("posX", Integer.toString(getX()));
        props.setProperty("posY", Integer.toString(getY()));
        try (FileOutputStream out = new FileOutputStream("widget.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create and display the frame
        SwingUtilities.invokeLater(() -> {
            MixWidget widget = new MixWidget();
            widget.setVisible(true);
            widget.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    widget.savePosition();
                }
            });
        });
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
            g2.setColor(new Color(0,0,0,210));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        }
    }

    private class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;

        public RoundedButton(String label) {
            super(label);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isArmed() ? Color.LIGHT_GRAY : getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    private class WeatherData {
        String condition;
        String temperature;

        WeatherData(String condition, String temperature) {
            this.condition = condition;
            this.temperature = temperature;
        }
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
