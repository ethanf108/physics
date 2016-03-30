import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

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

public class sdd extends JFrame implements MouseListener, ActionListener, KeyEventDispatcher {
//SETTINGS

    public static final byte VelocityDecimals = 2;
    public static final byte TimeSlow = 1;
    public static double SCALE = 45.0;
    public static byte Sides = 3;
    public static double Size = 1.0;
    public static double StartingAngVel = 0.0;
    public static double FricTion = 0.0;
    public static double AirRes = 0.00;
//USERVARS
    public static boolean isNextStaticObject = false;

    public static boolean isPaused = false;
    /**
     * The conversion factor from nano to base
     */
    public static final double NANO_TO_BASE = 1.0e9;

    void addRandOb(int x, int y) {
        GameObject ObjectYo = new GameObject();
        Polygon polyShape = Geometry.createUnitCirclePolygon(Sides, Size);

        ObjectYo.setLinearDamping(AirRes);
        ObjectYo.addFixture(polyShape).setFriction(FricTion);
        ObjectYo.setMass(isNextStaticObject ? MassType.INFINITE : MassType.NORMAL);
        ObjectYo.translate((x - 400.0) / SCALE, -((y - 350.0) / SCALE));
        ObjectYo.setAngularVelocity(Math.toRadians(-StartingAngVel * 10.0));
        this.world.addBody(ObjectYo);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for (Body b : this.world.getBodies()) {
            if (b.contains(new Vector2((x - 400.0) / SCALE, -((y - 350.0) / SCALE)))) {
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.deleteAll)) {
            this.world.removeAllBodies();
            Rectangle floorRect = new Rectangle(15.0, 1.0);
            GameObject floor = new GameObject();
            floor.addFixture(new BodyFixture(floorRect));
            floor.setMass(MassType.INFINITE);
            floor.translate(0.0, -4.0);
            floor.color = Color.BLACK;
            this.world.addBody(floor);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (e.getKeyChar() == ' ') {
                isPaused = !isPaused;
                if (isPaused) {
                    pause.setText("Play");
                } else {
                    pause.setText("Pause");
                }
            }
            return true;
        }
        return false;
    }

    public static class GameObject extends Body {

        protected Color color;

        public static Color colorGen() {
            return new Color(
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);

        }

        public GameObject() {
            this.color = colorGen();
        }

        public void render(Graphics2D g) {
            AffineTransform ot = g.getTransform();

            AffineTransform lt = new AffineTransform();
            lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
            lt.rotate(this.transform.getRotation());

            g.transform(lt);
            final double dectemp = Math.pow(10.0, VelocityDecimals);
            for (BodyFixture fixture : this.fixtures) {

                Convex convex = fixture.getShape();
                Graphics2DRenderer.render(g, convex, SCALE, color);
                g.rotate(0 - transform.getRotation());

                g.setColor(Color.BLACK);
                AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
                g.transform(yFlip);
                if (this.mass.getType().equals(MassType.NORMAL)) {

                    g.drawString(Double.toString(Math.round(this.velocity.x * dectemp) / dectemp), -5, -7);
                    g.drawString(Double.toString(Math.round(this.velocity.y * dectemp) / dectemp), -5, 2);

                }
                g.drawString(Double.toString(Math.round(Math.toDegrees(this.angularVelocity) * dectemp) / dectemp), -5, this.mass.getType().equals(MassType.INFINITE) ? 2 : 11);
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
    JButton deleteAll = new JButton("Delete all Objects");
    JButton close = new JButton("Close");
    JButton pause = new JButton("Pause");
    JCheckBox isStatic = new JCheckBox("Static Object?");

    public interface SliderImp {

        public void run(JSlider slider);
    }

    public JPanel createLabelSliderPanel(JLabel l, JSlider s, String t, double div, boolean d) {
        return createLabelSliderPanel(l, s, t, div, d, (JSlider slider) -> {
        });
    }

    public JPanel createLabelSliderPanel(JLabel l, JSlider s, String t, double div, boolean d, SliderImp r) {
        JPanel panel = new JPanel();
        Color c = GameObject.colorGen();
        panel.setBackground(c);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        s.setBackground(c);
        panel.add(l);
        panel.add(s);
        s.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (!d) {
                    l.setText(t + (int) (s.getValue() / div));
                } else {
                    l.setText(t + s.getValue() / div);
                }
                r.run(s);

            }
        });
        return panel;
    }

    public sdd() {
        super("Physics Project");
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel SettingsPane = new JPanel();
        SettingsPane.setLayout(new BoxLayout(SettingsPane, BoxLayout.Y_AXIS));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Gravity: 9.8"), new JSlider(0, 1000, 98), "Gravity: ", 10, true,
                (JSlider slider) -> {
                    this.world.setGravity(new Vector2(0, slider.getValue() / -10.0));
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Sides: 3"), new JSlider(3, 25, 3), "Sides: ", 1, false,
                (JSlider slider) -> {
                    this.Sides = (byte) slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Size: 1.0"), new JSlider(1, 50, 10), "Size: ", 10, true,
                (JSlider slider) -> {
                    this.Size = slider.getValue() / 10.0;
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Scale: 45"), new JSlider(1, 45, 45), "Scale: ", 1, false,
                (JSlider slider) -> {
                    this.SCALE = (byte) slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Angular Velocity: 0"), new JSlider(-36, 36, 0), "Angular Velocity: ", 1.0 / 10.0, false,
                (JSlider slider) -> {
                    this.StartingAngVel = slider.getValue();
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Friction: 0.0"), new JSlider(0, 40, 0), "Friction: ", 10.0, true,
                (JSlider slider) -> {
                    this.FricTion = slider.getValue() / 10.0;
                }));
        SettingsPane.add(createLabelSliderPanel(new JLabel("Air Resistance: 0.00"), new JSlider(0, 100, 0), "Air Resistance: ", 100.0, true,
                (JSlider slider) -> {
                    this.AirRes = slider.getValue() / 100.0;
                }));

        //buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        Color c = GameObject.colorGen();
        close.setBackground(c);
        pause.setBackground(c);
        deleteAll.setBackground(c);
        isStatic.setBackground(c);
        buttonsPanel.add(close);
        buttonsPanel.add(pause);
        buttonsPanel.add(deleteAll);
        buttonsPanel.add(isStatic);
        SettingsPane.add(buttonsPanel);
        add(SettingsPane);

        deleteAll.addActionListener(this);
        isStatic.addActionListener(this);
        pause.addActionListener(this);
        close.addActionListener(this);

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
        setUndecorated(true);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        // create a canvas to paint to 
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);
        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height);        // add the canvas to the JFrame
        this.add(this.canvas);

        // make the JFrame not resizable
        // (this way I dont have to worry about resize events)
        this.setResizable(false);
        // size everything
        // this.pack();

        // make sure we are not stopped
        this.stopped = false;

        // setup the world
        initializeWorld();

        world.setGravity(new Vector2(0, -9.8));
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
        floor.color = Color.BLACK;
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
            @Override
            public void run() {
                // perform an infinite loop stopped
                // render as fast as possible
                while (!isStopped()) {
                    gameLoop();

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
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
        // convert from nanoseconds to seconds
        double elapsedTime = diff / NANO_TO_BASE;
        if (!isPaused) {

            // update the world with the elapsed time
            this.world.update(elapsedTime / TimeSlow);
        }
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
            if (this.world.getBody(i).getTransform().getTranslationY() < -7.0) {
                this.world.removeBody(this.world.getBody(i));
                continue;
            }
            GameObject go = (GameObject) this.world.getBody(i);
            // draw the object
            if (this.world.getBodyCount() == 0) {
                break;
            }
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        // create the example JFrame
        sdd window = new sdd();

        // show it
        window.setVisible(true);

        // start it
        window.start();
    }
}
