import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
public class ClockWidget extends JFrame {
    private JLabel timeLabel;
    private SimpleDateFormat timeFormat;
    private Timer timer;
    private Font font;
    private int posX = 200, posY = 200,w = 400,h = 150;
    private Color foregroundColor = Color.WHITE;
    private JFrame controlFrame;
    private boolean isResizing = false;
    private boolean isMoving = false;
    private MouseAdapter mouseAdapter;

    public ClockWidget(){
        // Load the font
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\res\\font.OTF"));
        } catch (FontFormatException | IOException ffe) {
            ffe.printStackTrace();
        }
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException cnfe)
        {
            cnfe.printStackTrace();
        }
        // Load the widget position
        loadPosition();

        setUndecorated(true);
        setSize(w, h);
        setLocation(posX, posY);
        setBackground(new Color(0, 0, 0, 0));  // Transparent background
        setAlwaysOnTop(false);
        setFocusableWindowState(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        // Initialize the time label
        timeLabel = new JLabel();

        // Set label properties
        timeLabel.setForeground(foregroundColor);
        timeLabel.setBounds(0, 0, w, h);

        // Set custom fonts
        timeLabel.setFont(font.deriveFont(77.0f));

        // Add the time label directly to the frame
        add(timeLabel);

        // Initialize date formats
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // Update the time label initially
        updateDateTime();

        // Create and start timers
        timer = new Timer(1000, e -> updateDateTime());
        timer.start();

        // Add component listener to handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeClock();
            }
        });

        // Initialize control frame for system tray
        initializeControlFrame();
    }

    private void updateDateTime() {
        Date now = new Date();
        timeLabel.setText(timeFormat.format(now));
    }

    private void resizeClock() {
        timeLabel.setBounds(30, 30, getWidth() - 60, getHeight() - 60);
        timeLabel.setFont(font.deriveFont((float) getHeight() / 2));
    }

    private void initializeControlFrame() {
        controlFrame = new JFrame("Clock Widget Control");
        controlFrame.setSize(250, 220);
        controlFrame.setIconImage((new ImageIcon(System.getProperty("user.dir"+"\\res\\icon.jpg"))).getImage());
        controlFrame.setResizable(false);
        controlFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        controlFrame.setLayout(null);
        controlFrame.setLocationRelativeTo(null); // Center the control frame

        JButton toggleVisibilityButton = new JButton("Remove Widget");
        toggleVisibilityButton.setBounds((controlFrame.getWidth()/2)-70,20,130,30);
        toggleVisibilityButton.setFocusable(false);
        toggleVisibilityButton.addActionListener(e -> {
            boolean isVisible = ClockWidget.this.isVisible();
            ClockWidget.this.setVisible(!isVisible);
            toggleVisibilityButton.setText(isVisible ? "Add Widget" : "Remove Widget");
        });

        JButton resizeButton = new JButton("Resize Widget");
        resizeButton.setBounds((controlFrame.getWidth()/2)-65,60,120,30);
        resizeButton.setFocusable(false);
        resizeButton.addActionListener(e -> {
            if (!isResizing) {
                isResizing = true;
                resizeButton.setText("Stop Resizing");
                //ClockWidget.this.setResizable(true);
                enableResizing();
                ClockWidget.this.setBackground(new Color(0, 0, 0, 1));
                ClockWidget.this.repaint();
            } else {
                isResizing = false;
                resizeButton.setText("Resize Widget");
                //ClockWidget.this.setResizable(false);
                disableResizing();
                ClockWidget.this.setBackground(new Color(0, 0, 0, 0));
                ClockWidget.this.repaint();
            }
        });

        JButton moveButton = new JButton("Move Widget");
        moveButton.setBounds((controlFrame.getWidth()/2)-65,100,120,30);
        moveButton.setFocusable(false);
        moveButton.addActionListener(e -> {
            if (!isMoving) {
                isMoving = true;
                moveButton.setText("Stop Moving");
                enableDragging();
                ClockWidget.this.setBackground(new Color(0, 0, 0, 1));
                ClockWidget.this.repaint();
            } else {
                isMoving = false;
                moveButton.setText("Move Widget");
                disableDragging();
                ClockWidget.this.setBackground(new Color(0, 0, 0, 0));
                ClockWidget.this.repaint();
            }
        });

        JButton colorButton = new JButton("Change Clock Color");
        colorButton.setBounds((controlFrame.getWidth()/2)-90,140,170,30);
        colorButton.setFocusable(false);
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Clock Color", foregroundColor);
            if (newColor != null) {
                foregroundColor = newColor;
                timeLabel.setForeground(foregroundColor);
                saveColor();
            }
        });

        controlFrame.add(toggleVisibilityButton);
        controlFrame.add(resizeButton);
        controlFrame.add(moveButton);
        controlFrame.add(colorButton);

        // Add to system tray
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\res\\icon.jpg");
            PopupMenu trayMenu = new PopupMenu();

            MenuItem openItem = new MenuItem("Open Control");
            openItem.addActionListener(e -> controlFrame.setVisible(true));
            trayMenu.add(openItem);

            TrayIcon trayIcon = new TrayIcon(image, "Clock Widget", trayMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> controlFrame.setVisible(true));
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isResizing || isMoving) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(foregroundColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private void enableDragging() {
        mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posX = e.getX();
                posY = e.getY();
            }

            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
            }

            public void mouseReleased(MouseEvent e) {
                savePosition();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void disableDragging() {
        removeMouseListener(mouseAdapter);
        removeMouseMotionListener(mouseAdapter);
    }
    
    private void enableResizing() {
        mouseAdapter = new MouseAdapter() {
            public void mouseDragged(MouseEvent e){
                setSize(e.getXOnScreen()-getLocation().x,e.getYOnScreen()-getLocation().y);
            }

            public void mouseReleased(MouseEvent e) {
                savePosition();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
    
    private void disableResizing() {
        removeMouseListener(mouseAdapter);
        removeMouseMotionListener(mouseAdapter);
    }

    private void loadPosition() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("widget.properties")) {
            props.load(in);
            posX = Integer.parseInt(props.getProperty("posX", "200"));
            posY = Integer.parseInt(props.getProperty("posY", "200"));
            w = Integer.parseInt(props.getProperty("width", "440"));
            h = Integer.parseInt(props.getProperty("height", "150"));
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
        props.setProperty("width", Integer.toString(getWidth()));
        props.setProperty("height", Integer.toString(getHeight()));
        props.setProperty("foregroundColor", Integer.toString(foregroundColor.getRGB()));
        try (FileOutputStream out = new FileOutputStream("widget.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveColor() {
        Properties props = new Properties();
        props.setProperty("foregroundColor", Integer.toString(foregroundColor.getRGB()));
        try (FileOutputStream out = new FileOutputStream("widget.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create and display the frame
        SwingUtilities.invokeLater(() -> {
            ClockWidget widget = new ClockWidget();
            widget.setVisible(true);
            widget.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    widget.savePosition();
                }
            });
        });
    }
}
