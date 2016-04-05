package launch;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static launch.MainWindow.WindowMode.*;

public class MainWindow extends JFrame implements ActionListener {
    
    static MainWindow MAIN = null;
    
    enum WindowMode {
        
        PHYSDEMO,
        SETTINGS;
    };
    WindowMode mode = PHYSDEMO;
    WindowManager window = null;
    SettingsWindow settingsWindow = null;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(LayoutManager.settingsPhys)) {
            mode = SETTINGS;
            System.out.println("F");
            window.setVisible(false);
            //settingsWindow.setVisible(true);
            add(settingsWindow);
            repaint();
        }
    }
    
    public MainWindow() {
        super("Physics Project");
        MAIN = this;
        settingsWindow = new SettingsWindow();
        add(settingsWindow);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window = new WindowManager();
        setUndecorated(true);
        add(window);
        setVisible(true);
        window.start();
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // before we stop the JVM stop the example
                window.stop();
                super.windowClosing(e);
            }
        });
    }
    
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        MainWindow mw = new MainWindow();
    }
    
}
