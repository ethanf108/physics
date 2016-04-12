package launch;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainWindow extends JFrame {

    static MainWindow MAIN = null;

    static WindowManager window = null;
    static SettingsWindow settingsWindow = null;
    static Preferences prefs = null;
    public static String PWID = "0";
    public static String SWID = "1";
    public static String CCID = "2";

    public static void handleExceptionPopup(Throwable e) {
        System.out.print("ERROR");
        MAIN.dispose();
        MAIN = null;
        settingsWindow = null;
        window = null;
        new Popup(e);
    }



    public MainWindow() {
        super("Physics Project");
        MAIN = this;
            prefs = Preferences.userRoot().node("FergusonPhysicsProject");

            if (!prefs.getBoolean("EXISTS", false)) {
                prefs.putBoolean("EXISTS", true);
                prefs.put("FONTNAME", "verdana");
                prefs.putInt("FONTSIZE", 15);
                prefs.putInt("FONTINDEX", 0);
            }
            Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread th, Throwable e) {
                    handleExceptionPopup(e);
                }
            };
            Thread.setDefaultUncaughtExceptionHandler(h);
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
        new MainWindow();
    }

}
