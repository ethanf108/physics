/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JSlider;

/**
 *
 * @author Vex
 */
public class Frame extends JFrame{
    JSlider gravSlider = new JSlider(JSlider.HORIZONTAL,0,10,5);
    public Frame(){
        super("Physics Demo");
        setPreferredSize(new Dimension(500,500));
        setLayout(new FlowLayout());
        getContentPane().add(gravSlider);
        getContentPane().add(new Panel());
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
