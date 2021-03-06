package launch;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import static launch.WindowManager.isNextStaticObject;
import static launch.WindowManager.isPaused;
import org.dyn4j.geometry.Vector2;

public class LayoutManager implements ActionListener {

    static Font CurrentFont = null;
    WindowManager source;
    JButton deleteAll = new JButton("Delete all Objects");
    JButton close = new JButton("Close");
    JButton pause = new JButton("Pause");
    JPanel EnvPanel = new JPanel();
    JPanel ObjPanel = new JPanel();
    JCheckBox isStatic = new JCheckBox("Static Object?");
    JCheckBox showNames = new JCheckBox("Show Names");
    JPanel namePanel = new JPanel();
    static JTextField nameField = new JTextField();
    static JPanel buttonsPanel = new JPanel();
    static JPanel AllPanel = new JPanel();
    JPanel HoldingPanel = new JPanel();
    JPanel EnvChooserPanel = new JPanel();
    JPanel PresetChooserPanel = new JPanel();
    public static JComboBox<String> PresetChooser = new JComboBox<>(new String[]{"Mercury", "Venus", "Earth", "Moon", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto", "Custom"});
    public static final String EnvName = "Environment Variables";
    public static final String ObjName = "Object Variables";
    public static final String SetName = "Application Settings";
    public static final String BasicEnv = "Floor Only";
    public static final String BoxEnv = "Boxed";
    public static JComboBox<String> SettingsChooser = new JComboBox<>(new String[]{EnvName, ObjName, SetName});
    public static JComboBox<String> EnvChooser = new JComboBox<>(new String[]{BasicEnv, BoxEnv});
    private boolean PresetCustom = false;
    public static SysPanel SysPan = null;

    public double getPresetgrav(String s) {
        switch (s) {
            case "Mercury":
                return 3.7;
            case "Venus":
                return 8.87;
            case "Earth":
                return 9.8;
            case "Mars":
                return 3.71;
            case "Jupiter":
                return 24.92;
            case "Saturn":
                return 10.44;
            case "Uranus":
                return 8.87;
            case "Neptune":
                return 11.15;
            case "Pluto":
                return 0.58;
            case "Moon":
                return 1.62;
            default:
                return -source.world.getGravity().y;
        }
    }

    public double getAirRes(String s) {
        switch (s) {
            case "Mercury":
                return 10e-15;
            case "Venus":
                return 92;
            case "Earth":
                return 1;
            case "Mars":
                return 6e-3;
            case "Jupiter":
                return 100;
            case "Saturn":
                return 100;
            case "Uranus":
                return 100;
            case "Neptune":
                return 100;
            case "Pluto":
                return 3e-6;
            case "Moon":
                return 16e-2;
            default:
                return source.AirRes;
        }
    }

    public static void setFont(Font f) {
        for (Component comp : buttonsPanel.getComponents()) {
            comp.setFont(f);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.deleteAll)) {
            source.world.removeAllBodies();
            source.changeEnv(WindowManager.Env);
        } else if (e.getSource().equals(isStatic)) {
            isNextStaticObject = isStatic.isSelected();
        } else if (e.getSource().equals(close)) {
            System.exit(0);
        } else if (e.getSource().equals(pause)) {
            isPaused = !isPaused;
            if (isPaused) {
                pause.setText("Play");
            } else {
                pause.setText("Pause");
            }
        } else if (e.getSource().equals(showNames)) {
            WindowManager.NameShowing = showNames.isSelected();
        }
    }

    public interface SliderImp {

        public void run(JSlider slider);
    }

    public static void ShowPanel(final String ID) {
        CardLayout c = ((CardLayout) AllPanel.getLayout());
        c.show(AllPanel, ID);
    }

    public class SysPanel extends JPanel {
        public SysPanel() {
            super();
        }
        public void paint(Graphics g2) {
            Graphics2D g = (Graphics2D) g2;
            g.setColor(RandColor);
            g.fillRect(0, 0, 10000, 10);
            g.setColor(Color.BLACK);
            String StatusBarText = "Memory: ";
            StatusBarText += (
                    Runtime.getRuntime().maxMemory()
                    - Runtime.getRuntime().totalMemory()
                    + Runtime.getRuntime().freeMemory()
                    ) / 1024 / 1024;
            StatusBarText += "MB    Avaliable Cores: ";
            StatusBarText += Runtime.getRuntime().availableProcessors();
            StatusBarText += "    Number Of Bodies: ";
            StatusBarText += source.world.getBodyCount();
            g.drawString(StatusBarText, 0, 10);
        }
    }

    public JPanel layoutSettings() {
        source.setLayout(new BoxLayout(source, BoxLayout.Y_AXIS));
        SysPan = new SysPanel();
        HoldingPanel.setLayout(new BoxLayout(HoldingPanel, BoxLayout.Y_AXIS));
        HoldingPanel.add(SysPan);
        EnvChooserPanel.setBackground(RandColor);
        EnvChooserPanel.setLayout(new BoxLayout(EnvChooserPanel, BoxLayout.X_AXIS));
        EnvPanel.setLayout(new BoxLayout(EnvPanel, BoxLayout.Y_AXIS));
        AllPanel.setLayout(new CardLayout());
        ObjPanel.setLayout(new BoxLayout(ObjPanel, BoxLayout.Y_AXIS));
        EnvPanel.add(createLabelSliderPanel(9.8, new JSlider(0, 1000, 98), "Gravity: ", 1, 3,
                (JSlider slider) -> {
                    source.world.setGravity(new Vector2(0, slider.getValue() / -10.0));
                }));
        ObjPanel.add(createLabelSliderPanel(3, new JSlider(3, 25, 3), "Sides: ", 0, 2,
                (JSlider slider) -> {
                    source.Sides = (byte) slider.getValue();
                }));
        ObjPanel.add(createLabelSliderPanel(1.0, new JSlider(1, 50, 10), "Size: ", 1, 1,
                (JSlider slider) -> {
                    source.Size = slider.getValue() / 10.0;
                }));
        ObjPanel.add(createLabelSliderPanel(0, new JSlider(-36, 36, 0), "Angular Velocity: ", -1, 3,
                (JSlider slider) -> {
                    source.StartingAngVel = slider.getValue();
                }));
        ObjPanel.add(createLabelSliderPanel(0, new JSlider(0, 40, 0), "Friction: ", 1, 1,
                (JSlider slider) -> {
                    source.FricTion = slider.getValue() / 10.0;
                }));
        EnvPanel.add(createLabelSliderPanel(0, new JSlider(0, 1000, 0), "Air Resistance: ", 1, 0,
                (JSlider slider) -> {
                    source.AirRes = slider.getValue() / 100.0;
                }));
        ObjPanel.add(createLabelSliderPanel(0, new JSlider(0, 200, 0), "Bounciness: ", 2, 0,
                (JSlider slider) -> {
                    WindowManager.Bounce = slider.getValue() / 100.0;
                }));
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.setBackground(RandColor);
        JLabel t = new JLabel("Name of Object: ");
        t.setFont(CurrentFont);
        namePanel.add(t);
        nameField.setBackground(RandColor);
        EnvPanel.setBackground(RandColor);
        ObjPanel.setBackground(RandColor);
        AllPanel.setBackground(RandColor);
        nameField.setMaximumSize(new Dimension(nameField.getMaximumSize().width, nameField.getFont().getSize() * 5 + 3));
        namePanel.add(nameField);
        ObjPanel.add(namePanel);
        close.setBackground(RandColor);
        pause.setBackground(RandColor);
        deleteAll.setBackground(RandColor);
        isStatic.setBackground(RandColor);
        buttonsPanel.setBackground(RandColor);
        showNames.setBackground(RandColor);
        buttonsPanel.add(close);
        buttonsPanel.add(pause);
        buttonsPanel.add(deleteAll);
        buttonsPanel.add(isStatic);
        buttonsPanel.add(showNames);
        for (Component comp : buttonsPanel.getComponents()) {
            comp.setFont(CurrentFont);
        }
        deleteAll.addActionListener(this);
        isStatic.addActionListener(this);
        showNames.addActionListener(this);
        pause.addActionListener(this);
        close.addActionListener(this);
        AllPanel.add(EnvPanel, EnvName);
        AllPanel.add(ObjPanel, ObjName);
        AllPanel.add(new SettingsWindow(), SetName);
        ShowPanel(EnvName);
        HoldingPanel.setBackground(RandColor);
        HoldingPanel.add(SettingsChooser);
        HoldingPanel.add(AllPanel);
        HoldingPanel.add(buttonsPanel);
        SettingsChooser.addItemListener((ItemEvent s) -> {
            ShowPanel((String) SettingsChooser.getSelectedItem());
        });
        EnvChooserPanel.add(EnvChooser);
        EnvChooserPanel.setMaximumSize(new Dimension(EnvChooserPanel.getMaximumSize().width, EnvChooserPanel.getPreferredSize().height));
        EnvPanel.add(EnvChooserPanel);
        EnvChooser.addItemListener((ItemEvent l) -> {
            source.changeEnv((String) EnvChooser.getSelectedItem());
        });
        PresetChooserPanel.setLayout(new BoxLayout(PresetChooserPanel, BoxLayout.X_AXIS));
        PresetChooserPanel.setBackground(RandColor);
        PresetChooserPanel.add(PresetChooser);
        PresetChooserPanel.setMaximumSize(new Dimension(PresetChooserPanel.getMaximumSize().width, PresetChooserPanel.getPreferredSize().height));
        EnvPanel.add(PresetChooserPanel);
        return HoldingPanel;

    }

    public void setPauseButtonText(String text) {
        pause.setText(text);
    }
    static Color RandColor = null;

    public JPanel createLabelSliderPanel(double dv, JSlider s, String t, int div, int d, SliderImp r) {
        JPanel panel = new JPanel();
        JLabel l = new JLabel();
        panel.setBackground(RandColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        s.setBackground(RandColor);
        panel.add(l);
        panel.add(s);
        if (t.equals("Gravity: ")) {
            PresetChooser.addItemListener((ItemEvent e) -> {
                PresetCustom = false;
                if (PresetChooser.getSelectedItem().equals("Custom")) {
                    return;
                }
                int tmpVal = (int) (getPresetgrav((String) PresetChooser.getSelectedItem()) * 10);
                s.setValue(tmpVal);
                if (div > 0) {

                    char[] zeros = new char[div];
                    Arrays.fill(zeros, '0');
                    char[] zerosS = new char[d];
                    Arrays.fill(zerosS, '0');
                    DecimalFormat formatter = new DecimalFormat(new String(zerosS) + "." + new String(zeros));
                    String result = formatter.format((s.getValue() / Math.pow(10, div)));
                    if (result.charAt(0) == '.') {
                        result = "0" + result;
                    }
                    l.setText(t + result);
                } else if (div <= 0) {
                    char[] zeros = new char[(d)];
                    Arrays.fill(zeros, '0');
                    DecimalFormat formatter = new DecimalFormat(new String(zeros));
                    String result = formatter.format((s.getValue() / Math.pow(10, div)));
                    l.setText(t + result);
                }
                r.run(s);
            });
        }
        if (t.equals("Air Resistance: ")) {
            PresetChooser.addItemListener((ItemEvent e) -> {
                PresetCustom = false;
                if (PresetChooser.getSelectedItem().equals("Custom")) {
                    return;
                }
                int tmpVal = (int) (getAirRes((String) PresetChooser.getSelectedItem()) * 10);
                s.setValue(tmpVal);
                if (div > 0) {

                    char[] zeros = new char[div];
                    Arrays.fill(zeros, '0');
                    char[] zerosS = new char[d];
                    Arrays.fill(zerosS, '0');
                    DecimalFormat formatter = new DecimalFormat(new String(zerosS) + "." + new String(zeros));
                    String result = formatter.format((s.getValue() / Math.pow(10, div)));
                    if (result.charAt(0) == '.') {
                        result = "0" + result;
                    }
                    l.setText(t + result);
                } else if (div <= 0) {
                    char[] zeros = new char[(d)];
                    Arrays.fill(zeros, '0');
                    DecimalFormat formatter = new DecimalFormat(new String(zeros));
                    String result = formatter.format((s.getValue() / Math.pow(10, div)));
                    l.setText(t + result);
                }
                r.run(s);
            });
        }
        l.setFont(CurrentFont);

        s.addChangeListener((ChangeEvent e) -> {
            if (PresetCustom && (t.equals("Gravity: ") || t.equals("Air Resistance: "))) {
                PresetChooser.setSelectedItem("Custom");
            }
            PresetCustom = true;
            if (div > 0) {

                char[] zeros = new char[div];
                Arrays.fill(zeros, '0');
                char[] zerosS = new char[d];
                Arrays.fill(zerosS, '0');
                DecimalFormat formatter = new DecimalFormat(new String(zerosS) + "." + new String(zeros));
                String result = formatter.format((s.getValue() / Math.pow(10, div)));
                if (result.charAt(0) == '.') {
                    result = "0" + result;
                }
                l.setText(t + result);
            } else if (div <= 0) {
                char[] zeros = new char[(d)];
                Arrays.fill(zeros, '0');
                DecimalFormat formatter = new DecimalFormat(new String(zeros));
                String result = formatter.format((s.getValue() / Math.pow(10, div)));
                l.setText(t + result);
            }
            r.run(s);
        });
        s.getChangeListeners()[0].stateChanged(null);
        return panel;
    }

    public LayoutManager(WindowManager sourceWindow) {
        this.source = sourceWindow;
        RandColor = WindowManager.GameObject.colorGen();
        CurrentFont = new Font(MainWindow.prefs.get("FONTNAME", "verdana"),
                0, MainWindow.prefs.getInt("FONTSIZE", 15));
    }
}
