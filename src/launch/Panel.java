/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Panel extends JPanel implements MouseListener,MouseMotionListener{
    ArrayList<Particle> obs = new ArrayList<>();
    public Panel(){
        setPreferredSize(new Dimension(500,500));
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    @Override
    public void paint(Graphics g2){
        Graphics2D g = (Graphics2D)g2;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
        g.setColor(Color.RED);
        for(Particle p : obs){
            g.fillRect(p.x, p.y, 10,10);
        }
    }
void autoUpdate(){
    Thread autoUT = new Thread(new Runnable(){
        public void run(){
            while(true){
                
            }
        }
    });
    autoUT.start();
}
    @Override
    public void mouseClicked(MouseEvent e) {
obs.add(new Particle(e.getX(),e.getY()));
repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
