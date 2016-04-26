/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import static launch.LayoutManager.AllPanel;

/**
 *
 * @author o
 */
public class Popup extends JFrame {

    public Popup(Throwable e) {
        super("Error has occurred");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        JPanel Reg = new JPanel();
        Reg.setLayout(new BoxLayout(Reg, BoxLayout.Y_AXIS));
        JPanel Reg2 = new JPanel();
        Reg2.setLayout(new BoxLayout(Reg2, BoxLayout.Y_AXIS));
        Reg2.setAlignmentX(LEFT_ALIGNMENT);
        Reg2.add(new JLabel("An error has occurred with the physics software. "));
        Reg2.add(new JLabel("To view the detailed error message, please click the button below. "));
        Reg2.add(new JLabel("If not, you can choose to close the program, or Re-Launch"));
        Reg.add(Reg2);
        JPanel RegSub = new JPanel();
        RegSub.setLayout(new BoxLayout(RegSub, BoxLayout.X_AXIS));
        RegSub.setAlignmentX(LEFT_ALIGNMENT);
        JButton C = new JButton("Close Program");
        JButton R = new JButton("Re-Launch");
        JButton D = new JButton("View Detailed Error");
        C.addActionListener((ActionEvent ae)->{System.exit(1);});
        D.addActionListener((ActionEvent ae)->{
            CardLayout c = ((CardLayout) this.getContentPane().getLayout());
        c.show(this.getContentPane(), "1");});
        R.addActionListener((ActionEvent ae)->{dispose();RestartClass.restartApplication();});
        RegSub.add(C);
        RegSub.add(R);
        RegSub.add(D);
        Reg.add(RegSub);
        this.getContentPane().add(Reg, "0");
        JPanel P2 = new JPanel();
        P2.setLayout(new BoxLayout(P2,BoxLayout.Y_AXIS));
        P2.add(new JLabel(e.getClass().toString().replaceAll("class ", "").replaceAll("interface ", "")));
        for(StackTraceElement s : e.getStackTrace()){
            P2.add(new JLabel("at " + s.toString()));
        }
        JScrollPane p = new JScrollPane(P2);
        p.setAutoscrolls(true);
        add(p,"1");
        CardLayout c = ((CardLayout) this.getContentPane().getLayout());
        c.show(this.getContentPane(), "0");
        pack();
        setVisible(true);

    }

}
