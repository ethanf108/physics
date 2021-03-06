package launch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

public class SettingsWindow extends JPanel {

    JComboBox<Font> FontSelector = null;
    JPanel FontSelectPanel = new JPanel();
    JPanel FontSizePanel = new JPanel();
    JPanel FontDemoPanel = new JPanel();
    JLabel FontLabel = new JLabel("Font: ");
    JLabel FontSizeLabel = new JLabel("Size: 12");
    JSlider FontSize = new JSlider(1, 20, 12);
    JLabel FontDemo = new JLabel("The quick brown fox jumps over the lazy dog");
    JButton save = new JButton("Save");
    JPanel ButtonsPanel = new JPanel();

    public SettingsWindow() {
        super();
        setBackground(LayoutManager.RandColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        FontSizeLabel.setText("Size: " + MainWindow.prefs.get("FONTSIZE", "15"));
        FontSize.setValue(MainWindow.prefs.getInt("FONTSIZE", 15));
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Font Select = null;
        for (Font f : allFonts) {
            if (f.getName().equalsIgnoreCase(MainWindow.prefs.get("FONTNAME", "verdana"))) {
                Select = f;
            }
        }
        ButtonsPanel.setLayout(new FlowLayout());
        ButtonsPanel.add(save);
        ButtonsPanel.setMaximumSize(new Dimension(ButtonsPanel.getMaximumSize().height, ButtonsPanel.getPreferredSize().width));
        add(ButtonsPanel);
        save.addActionListener((ActionEvent e) -> {
            MainWindow.prefs.put("FONTNAME", ((Font) FontSelector.getSelectedItem()).getName());
            MainWindow.prefs.putInt("FONTSIZE", FontSize.getValue());
            MainWindow.prefs.putInt("FONTINDEX", FontSelector.getSelectedIndex());
            LayoutManager.setFont(
                    new Font(
                            ((Font) FontSelector.getSelectedItem()).getName(), 0, FontSize.getValue()));
        });

        FontSelector = new JComboBox<>(allFonts);
        FontSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    Font font = (Font) value;
                    value = font.getName();
                    list.setFont(font.deriveFont(25));
                }
                return super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
            }
        });
        FontSelector.setSelectedItem(Select);
        FontSelectPanel.setLayout(new FlowLayout());
        ButtonsPanel.setBackground(LayoutManager.RandColor);
        FontDemoPanel.setBackground(LayoutManager.RandColor);
        FontSizePanel.setBackground(LayoutManager.RandColor);
        FontSelectPanel.setBackground(LayoutManager.RandColor);
        FontSelectPanel.add(FontLabel);
        FontSelectPanel.add(FontSelector);
        FontSizePanel.setLayout(new FlowLayout());
        FontSizePanel.add(FontSizeLabel);
        FontSizePanel.add(FontSize);
        FontSelectPanel.setMaximumSize(new Dimension(FontSelectPanel.getMaximumSize().height, FontSelectPanel.getPreferredSize().width));
        FontSizePanel.setMaximumSize(new Dimension(FontSizePanel.getMaximumSize().height, FontSizePanel.getPreferredSize().width));
        add(FontSelectPanel);
        add(FontSizePanel);
        FontSelector.addActionListener((ActionEvent e) -> {
            FontDemo.setFont(new Font(((Font) FontSelector.getSelectedItem()).getName(), 0, FontSize.getValue()));
            FontDemo.repaint();
        });
        FontSelector.setSelectedIndex(MainWindow.prefs.getInt("FONTINDEX", 0));
        FontDemo.setFont(new Font(MainWindow.prefs.get("FONTNAME", "verdana"), 0, MainWindow.prefs.getInt("FONTSIZE", 15)));
        FontDemoPanel.add(FontDemo);
        add(FontDemoPanel);

        FontSize.addChangeListener((ChangeEvent e) -> {
            FontSizeLabel.setText("Size: " + FontSize.getValue());
            FontDemo.setFont(new Font(((Font) FontSelector.getSelectedItem()).getName(), 0, ((JSlider) e.getSource()).getValue()));
            FontDemo.repaint();
        });
    }
}
