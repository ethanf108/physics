package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.BoxLayout;

import javax.swing.JPanel;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class WindowManager extends JPanel implements MouseListener, KeyEventDispatcher {
//SETTINGS

    public static final byte VelocityDecimals = 2;
    public final byte TimeSlow = 1;
    public static double SCALE = 45.0;
    public byte Sides = 3;
    public double Size = 1.0;
    public double StartingAngVel = 0.0;
    public double FricTion = 0.0;
    public double AirRes = 0.00;
    public static boolean NameShowing = false;
    public LayoutManager customLayoutManager = null;
//USERVARS
    public static boolean isNextStaticObject = false;
    public static boolean isPaused = false;
    public static final double NANO_TO_BASE = 1.0e9;

    void addRandOb(int x, int y) {
        GameObject ObjectYo = new GameObject();
        int tsides = Sides;
        double tsize = Size;
        double tfric = FricTion;
        boolean tisstatic = isNextStaticObject;
        double tangvel = -StartingAngVel;
        String tname = LayoutManager.nameField.getText();
        Polygon polyShape = Geometry.createUnitCirclePolygon(tsides, tsize);
        ObjectYo.setUserData(tname);
        LayoutManager.nameField.setText("");
        ObjectYo.addFixture(polyShape).setFriction(tfric);
        ObjectYo.setMass(tisstatic ? MassType.INFINITE : MassType.NORMAL);
        ObjectYo.translate((x - 400.0) / SCALE, -((y - 350.0) / SCALE));
        ObjectYo.setAngularVelocity(Math.toRadians(tangvel * 10.0));
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
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (e.getKeyChar() == ' ') {
                isPaused = !isPaused;
                if (isPaused) {
                    customLayoutManager.setPauseButtonText("Play");
                } else {
                    customLayoutManager.setPauseButtonText("Pause");
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
                if (!NameShowing) {
                    if (this.mass.getType().equals(MassType.NORMAL)) {

                        g.drawString(Double.toString(Math.round(this.velocity.x * dectemp) / dectemp), -5, -7);
                        g.drawString(Double.toString(Math.round(this.velocity.y * dectemp) / dectemp), -5, 2);

                    }
                    if (!getUserData().equals("Floor")) {
                        g.drawString(Double.toString(Math.round(Math.toDegrees(this.angularVelocity) * dectemp) / dectemp), -5, this.mass.getType().equals(MassType.INFINITE) ? 2 : 11);
                    }
                } else {
                    if (this.userData != null) {
                        g.drawString((String) this.userData, -5, 2);
                    }
                }

                g.setTransform(ot);
            }
        }
    }
    protected Canvas canvas;
    protected World world;
    protected boolean stopped;
    protected long last;
    public WindowManager() {
        super();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        customLayoutManager = new LayoutManager(this);
        JPanel before = new JPanel();
        before.setLayout(new BoxLayout(before, BoxLayout.Y_AXIS));
        add(before);
        Dimension size = new Dimension(800, 600);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);

        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height); 
        this.add(canvas);
        initializeWorld();
        this.stopped = false;
        world.setGravity(new Vector2(0, -9.8));
        before.add(customLayoutManager.layoutSettings());
    }
    protected final void initializeWorld() {
        this.world = new World();
        this.canvas.addMouseListener(this);
        Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        floor.setUserData("Floor");
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);
    }

    public void start() {
        this.last = System.nanoTime();
        this.canvas.setIgnoreRepaint(true);
        this.canvas.createBufferStrategy(2);
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!isStopped()) {
                    gameLoop();

                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    protected void gameLoop() {
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();
        AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
        AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
        g.transform(yFlip);
        g.transform(move);
        this.render(g);
        g.dispose();
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }
        Toolkit.getDefaultToolkit().sync();
        long time = System.nanoTime();
        long diff = time - this.last;
        this.last = time;
        double elapsedTime = diff / NANO_TO_BASE;
        if (!isPaused) {
            this.world.update(elapsedTime / TimeSlow);
        }
    }
    protected void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(-400, -300, 800, 600);
        g.translate(0.0, -1.0 * SCALE);
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            if (this.world.getBodyCount() == 0) {
                break;
            }
            if (this.world.getBody(i).getTransform().getTranslationY() < -7.0) {
                this.world.removeBody(this.world.getBody(i));
                break;
            }
            GameObject go = (GameObject) this.world.getBody(i);
            go.setLinearDamping(AirRes);
            go.render(g);
        }
    }

    public synchronized void stop() {
        this.stopped = true;
    }

    public synchronized boolean isStopped() {
        return this.stopped;
    }

}