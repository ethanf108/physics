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

public class MainWindow extends JFrame implements ActionListener {

    static MainWindow MAIN = null;

    WindowManager window = null;
    SettingsWindow settingsWindow = null;
    static Preferences prefs = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(LayoutManager.settingsPhys)) {
            System.out.println("F");
            CardLayout c = ((CardLayout) getContentPane().getLayout());
            c.show(this.getContentPane(), SWID);
            repaint();
        }
    }


    public static String PWID = "0";
    public static String SWID = "1";
    public static String CCID = "2";

    public MainWindow() {
        super("Physics Project");
        MAIN = this;
        setLayout(new CardLayout());
        prefs = Preferences.userRoot().node(this.getClass().getName());
        if (!prefs.getBoolean("EXISTS", false)) {
            prefs.putBoolean("EXISTS", true);
            prefs.put("FONTNAME", "verdana");
            prefs.putInt("FONTSIZE", 15);
            prefs.putInt("FONTINDEX", 0);
        }
        settingsWindow = new SettingsWindow();
      //  add(settingsWindow, SWID);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window = new WindowManager();
        setUndecorated(true);
        add(window, PWID);
        CardLayout c = ((CardLayout) getContentPane().getLayout());
        c.show(this.getContentPane(), PWID);
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
