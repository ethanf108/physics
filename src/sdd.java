
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;


public class sdd extends JFrame implements MouseListener, ChangeListener,ActionListener {

    /**
     * The serial version id
     */
    private static final long serialVersionUID = 5663760293144882635L;

    /**
     * The scale 45 pixels per meter
     */
    public static double SCALE = 45.0;

    /**
     * The conversion factor from nano to base
     */
    public static final double NANO_TO_BASE = 1.0e9;
    void addRandOb(int x, int y) {
        GameObject ObjectYo = new GameObject();
        Polygon polyShape = Geometry.createUnitCirclePolygon(Sides.getValue(), Size.getValue()/10.0);
        ObjectYo.addFixture(polyShape);
        ObjectYo.setMass(MassType.NORMAL);
        ObjectYo.translate((x - 400.0) / SCALE, -((y - 350.0) / SCALE));

        ObjectYo.setAngularVelocity(Math.toRadians(-20.0));
        this.world.addBody(ObjectYo);
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
for (Body b : this.world.getBodies()){
    if(b.contains(new Vector2((x - 400.0) / SCALE, -((y - 350.0) / SCALE)))){
        this.world.removeBody(b);
        return;
    }
}
        addRandOb(x, y);

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
    public void stateChanged(ChangeEvent e) {
        JSlider t = (JSlider) e.getSource();
        if (t.equals(grav)) {
            this.world.setGravity(new Vector2(0, t.getValue() / -10.0));
            this.GravLabel.setText("Gravity: " + t.getValue() / 10.0);
        } else if (t.equals(Sides)) {
            this.SidesLabel.setText("Sides: " + t.getValue());
        } else if (t.equals(Size)) {
            this.SizeLabel.setText("Size: " + t.getValue()/10.0);
        } else if (t.equals(Scale)) {
            this.ScaleLabel.setText("Scale: " + t.getValue());
            this.SCALE=t.getValue();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
this.world.removeAllBodies();
Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);
    }

    public static class GameObject extends Body {

        protected Color color;

        public GameObject() {
            this.color = new Color(
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);
        }

        public void render(Graphics2D g) {
            AffineTransform ot = g.getTransform();

            AffineTransform lt = new AffineTransform();
            lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
            lt.rotate(this.transform.getRotation());

            g.transform(lt);

            for (BodyFixture fixture : this.fixtures) {
                Convex convex = fixture.getShape();
                Graphics2DRenderer.render(g, convex, SCALE, color);
                g.rotate(-transform.getRotation());
                g.drawString("hey", ((float)SCALE)/100.0f, ((float)SCALE)/100.0f);

            }

            g.setTransform(ot);
        }
    }

    /**
     * The canvas to draw to
     */
    protected Canvas canvas;

    /**
     * The dynamics engine
     */
    protected World world;

    /**
     * Wether the example is stopped or not
     */
    protected boolean stopped;

    /**
     * The time stamp for the last iteration
     */
    protected long last;

    /**
     * Default constructor for the window
     */
    JSlider grav = new JSlider(0, 1000, 98);
    JLabel GravLabel = new JLabel("Gravity: 9.8");
    JSlider Sides = new JSlider(3, 25, 3);
    JLabel SidesLabel = new JLabel("Sides: 3");
    JSlider Size = new JSlider(1, 50, 10);
    JLabel SizeLabel = new JLabel("Size: 1.0");
    JSlider Scale = new JSlider(1, 45, 45);
    JLabel ScaleLabel = new JLabel("Scale: 45");
JButton deleteAll = new JButton("Delete all Objects");
    public sdd() {
        super("Physics project");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel SettingsPane = new JPanel();
        SettingsPane.setLayout(new BoxLayout(SettingsPane, BoxLayout.Y_AXIS));
        SettingsPane.add(GravLabel);
        SettingsPane.add(grav);
        SettingsPane.add(SidesLabel);
        SettingsPane.add(Sides);
        SettingsPane.add(SizeLabel);
        SettingsPane.add(Size);
        SettingsPane.add(ScaleLabel);
        SettingsPane.add(Scale);
        SettingsPane.add(deleteAll);

        this.add(SettingsPane);
        grav.addChangeListener(this);
        Sides.addChangeListener(this);
        Size.addChangeListener(this);
        Scale.addChangeListener(this);
        deleteAll.addActionListener(this);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add a window listener
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // before we stop the JVM stop the example
                stop();
                super.windowClosing(e);
            }
        });

        // create the size of the window
        Dimension size = new Dimension(800, 600);

        // create a canvas to paint to 
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);

        // add the canvas to the JFrame
        this.add(this.canvas);

        // make the JFrame not resizable
        // (this way I dont have to worry about resize events)
        this.setResizable(false);
        // size everything
        this.pack();

        // make sure we are not stopped
        this.stopped = false;

        // setup the world
        this.initializeWorld();

        world.setGravity(new Vector2(0, -9.8));
        this.pack();
    }

    /**
     * Creates game objects and adds them to the world.
     * <p>
     * Basically the same shapes from the Shapes test in the TestBed.
     */
    protected void initializeWorld() {
        // create the world
        this.world = new World();
        this.canvas.addMouseListener(this);
        // create all your bodies/joints

        // create the floor
        Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);

    }

    /**
     * Start active rendering the example.
     * <p>
     * This should be called after the JFrame has been shown.
     */
    public void start() {
        // initialize the last update time
        this.last = System.nanoTime();
        // don't allow AWT to paint the canvas since we are
        this.canvas.setIgnoreRepaint(true);
        // enable double buffering (the JFrame has to be
        // visible before this can be done)
        this.canvas.createBufferStrategy(2);
        // run a separate thread to do active rendering
        // because we don't want to do it on the EDT
        Thread thread = new Thread() {
            public void run() {
                // perform an infinite loop stopped
                // render as fast as possible
                while (!isStopped()) {
                    gameLoop();
                    // you could add a Thread.yield(); or
                    // Thread.sleep(long) here to give the
                    // CPU some breathing room
                }
            }
        };
        // set the game loop thread to a daemon thread so that
        // it cannot stop the JVM from exiting
        thread.setDaemon(true);
        // start the game loop
        thread.start();
    }

    /**
     * The method calling the necessary methods to update the game, graphics,
     * and poll for input.
     */
    protected void gameLoop() {
        // get the graphics object to render to
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();

        // before we render everything im going to flip the y axis and move the
        // origin to the center (instead of it being in the top left corner)
        AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
        AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
        g.transform(yFlip);
        g.transform(move);

        // now (0, 0) is in the center of the screen with the positive x axis
        // pointing right and the positive y axis pointing up
        // render anything about the Example (will render the World objects)
        this.render(g);

        // dispose of the graphics object
        g.dispose();

        // blit/flip the buffer
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }

        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();

        // update the World
        // get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
        // convert from nanoseconds to seconds
        double elapsedTime = diff / NANO_TO_BASE;
        // update the world with the elapsed time
        this.world.update(elapsedTime);
    }

    /**
     * Renders the example.
     *
     * @param g the graphics object to render to
     */
    protected void render(Graphics2D g) {
        // lets draw over everything with a white background
        g.setColor(Color.WHITE);
        g.fillRect(-400, -300, 800, 600);

        // lets move the view up some
        g.translate(0.0, -1.0 * SCALE);

        // draw all the objects in the world
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            // get the object
            if(this.world.getBody(i).getTransform().getTranslationY()<-7.0){
                this.world.removeBody(this.world.getBody(i));
                continue;
            }
            GameObject go = (GameObject) this.world.getBody(i);
            // draw the object
            if(this.world.getBodyCount()==0)break;
            go.render(g);
        }
    }

    /**
     * Stops the example.
     */
    public synchronized void stop() {
        this.stopped = true;
    }

    /**
     * Returns true if the example is stopped.
     *
     * @return boolean true if stopped
     */
    public synchronized boolean isStopped() {
        return this.stopped;
    }

    /**
     * Entry point for the example application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // create the example JFrame
        sdd window = new sdd();

        // show it
        window.setVisible(true);

        // start it
        window.start();
    }
}
