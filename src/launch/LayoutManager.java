package launch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;
import static launch.WindowManager.isNextStaticObject;
import static launch.WindowManager.isPaused;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class LayoutManager implements ActionListener {

    Font CurrentFont = null;
    WindowManager source;
    JButton deleteAll = new JButton("Delete all Objects");
    JButton close = new JButton("Close");
    JButton pause = new JButton("Pause");
    JCheckBox isStatic = new JCheckBox("Static Object?");

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.deleteAll)) {
            source.world.removeAllBodies();
            Rectangle floorRect = new Rectangle(15.0, 1.0);
            WindowManager.GameObject floor = new WindowManager.GameObject();
            floor.addFixture(new BodyFixture(floorRect));
            floor.setMass(MassType.INFINITE);
            floor.translate(0.0, -4.0);
            floor.setUserData("FLOOR");
            source.world.addBody(floor);
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
        }
    }

    public interface SliderImp {

        public void run(JSlider slider);
    }

    public JPanel layoutSettings() {
        source.setLayout(new BoxLayout(source.getContentPane(), BoxLayout.Y_AXIS));
        JPanel SettingsPane = new JPanel();
        SettingsPane.setLayout(new BoxLayout(SettingsPane, BoxLayout.Y_AXIS));
        SettingsPane.add(createLabelSliderPanel(9.8, new JSlider(0, 1000, 98), "Gravity: ", 1, 3,
                (JSlider slider) -> {
                    source.world.setGravity(new Vector2(0, slider.getValue() / -10.0));
                }));
        SettingsPane.add(createLabelSliderPanel(3, new JSlider(3, 25, 3), "Sides: ", 0, 2,
                (JSlider slider) -> {
                    source.Sides = (byte) slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(1.0, new JSlider(1, 50, 10), "Size: ", 1, 1,
                (JSlider slider) -> {
                    source.Size = slider.getValue() / 10.0;
                }));
        SettingsPane.add(createLabelSliderPanel(45, new JSlider(1, 45, 45), "Scale: ", 0, 2,
                (JSlider slider) -> {
                    source.SCALE = (byte) slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(0, new JSlider(-36, 36, 0), "Angular Velocity: ", -1, 3,
                (JSlider slider) -> {
                    source.StartingAngVel = slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(0, new JSlider(0, 40, 0), "Friction: ", 1, 1,
                (JSlider slider) -> {
                    source.FricTion = slider.getValue() / 10.0;
                }));
        SettingsPane.add(createLabelSliderPanel(0, new JSlider(0, 100, 0), "Air Resistance: ", 2, 0,
                (JSlider slider) -> {
                    source.AirRes = slider.getValue() / 100.0;
                }));

        //buttons
        JPanel buttonsPanel = new JPanel();
        Color c = WindowManager.GameObject.colorGen();
        close.setBackground(c);
        pause.setBackground(c);
        deleteAll.setBackground(c);
        isStatic.setBackground(c);

        buttonsPanel.setBackground(c);
        buttonsPanel.add(close);
        buttonsPanel.add(pause);
        buttonsPanel.add(deleteAll);
        buttonsPanel.add(isStatic);
        SettingsPane.add(buttonsPanel);

        for (Component comp : buttonsPanel.getComponents()) {
            comp.setFont(CurrentFont);
        }

        deleteAll.addActionListener(this);
        isStatic.addActionListener(this);
        pause.addActionListener(this);
        close.addActionListener(this);
        return SettingsPane;

    }

    public void setPauseButtonText(String text) {
        pause.setText(text);
    }

    public JPanel createLabelSliderPanel(double dv, JSlider s, String t, int div, int d, SliderImp r) {
        JPanel panel = new JPanel();
        JLabel l = new JLabel();
        Color c = WindowManager.GameObject.colorGen();
        panel.setBackground(c);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        s.setBackground(c);
        panel.add(l);
        panel.add(s);
        Font Manifesto;
s.setForeground(Color.BLACK);
BasicSliderUI sui =(BasicSliderUI) javax.swing.plaf.basic.BasicSliderUI.createUI(s);
        
        l.setFont(CurrentFont);

        s.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
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

            }
        });
        s.getChangeListeners()[0].stateChanged(null);
        return panel;
    }

    public LayoutManager(WindowManager sourceWindow) {
        this.source = sourceWindow;
        CurrentFont = new Font("verdana", 0, 15);
    }
}
