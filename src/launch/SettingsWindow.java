package launch;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class SettingsWindow extends JPanel {

    JComboBox<Font> FontSelector = null;
    JPanel FontSelectPanel = new JPanel();
    JPanel FontSizePanel = new JPanel();
    JPanel FontDemoPanel = new JPanel();
    JLabel FontLabel = new JLabel("Font: ");
    JLabel FontSizeLabel = null;
    JSlider FontSize = null;
    JLabel FontDemo = null;
    public SettingsWindow() {
        super();
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        FontSelector = new JComboBox<>(allFonts);
        FontSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    Font font = (Font) value;
                    value = font.getName();
                }
                return super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
            }
        });
        FontSelectPanel.setLayout(new FlowLayout());
        FontSelectPanel.add(FontLabel);
        FontSelectPanel.add(FontSelector);
        add(FontSelectPanel);
    }
}
