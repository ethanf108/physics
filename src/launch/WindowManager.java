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
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.BoxLayout;

import javax.swing.JPanel;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class WindowManager extends JPanel implements MouseListener, MouseMotionListener, KeyEventDispatcher {

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
    public static boolean MouseDown = false;
    public static double oldX = 0;
    public static double oldY = 0;
    public static double X = 0;
    public static double Y = 0;
    public static boolean isNextStaticObject = false;
    public static boolean isPaused = false;
    public static double Bounce = 0;
    public static final double NANO_TO_BASE = 1.0e9;

    public double convertToPosX(double x) {
        return (x - 400.0) / SCALE;
    }

    public double convertToPosY(double y) {
        return -((y - 350.0) / SCALE);
    }

    public Body getBodyByPos(double x, double y) {
        for (Body b : this.world.getBodies()) {
            if(this.world.getBodyCount()<1)return null;
            if (b.contains(new Vector2(x, y))) {
                return b;
            }
        }
        return null;
    }
    static double XChangeVelocity = 0;
    static double YChangeVelocity = 0;
    static double XMoveVelocity = 0;
    static double YMoveVelocity = 0;
    public static double currentAngle = 0.0;
    boolean hasSelectedBody = false;
    Body currentSelectedBody = null;

    public static double getAngle(Vector2 p) {
        double angle = Math.toDegrees(Math.atan2(p.y, p.x));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public void ApplyForceThread() throws InterruptedException {
        Thread.sleep(50);
        Body selectedBody = getBodyByPos(convertToPosX(X), convertToPosY(Y));
        if (MouseDown && selectedBody != null && !hasSelectedBody) {
            hasSelectedBody = true;
            currentSelectedBody = selectedBody;
        }
        if (!MouseDown) {
            hasSelectedBody = false;
            currentSelectedBody = null;
        }
        if (hasSelectedBody) {
            currentSelectedBody.setLinearVelocity(new Vector2((XMoveVelocity + XChangeVelocity) / 2, (YChangeVelocity + YMoveVelocity) / 2));
            XChangeVelocity = 8.0 * (convertToPosX(X) - convertToPosX(oldX));
            YChangeVelocity = 8.0 * (convertToPosY(Y) - convertToPosY(oldY));
            XMoveVelocity = 8.0 * (convertToPosX(X) - currentSelectedBody.getTransform().getTranslationX());
            YMoveVelocity = 8.0 * (convertToPosY(Y) - currentSelectedBody.getTransform().getTranslationY());
        }else{
            XMoveVelocity=YMoveVelocity=XChangeVelocity=YChangeVelocity=0;
        }
        oldX = X;
        oldY = Y;
    }

    public void CreateFloor() {
        Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        UserData.Generate(floor, "Floor", true);
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);
    }

    public void CreateBox() {
        Rectangle Floor = new Rectangle(20.0, 1.0);
        GameObject FloorO = new GameObject();
        UserData.Generate(FloorO, "Floor", true);
        FloorO.addFixture(new BodyFixture(Floor));
        FloorO.setMass(MassType.INFINITE);
        FloorO.translate(0, -5.25);
        this.world.addBody(FloorO);
        Rectangle Ceil = new Rectangle(20.0, 1.0);
        GameObject CeilO = new GameObject();
        UserData.Generate(CeilO, "Floor", true);
        CeilO.addFixture(new BodyFixture(Ceil));
        CeilO.setMass(MassType.INFINITE);
        CeilO.translate(0, 8.05);
        this.world.addBody(CeilO);
        Rectangle Left = new Rectangle(1.0, 15.0);
        GameObject LeftO = new GameObject();
        UserData.Generate(LeftO, "Floor", true);
        LeftO.addFixture(new BodyFixture(Left));
        LeftO.setMass(MassType.INFINITE);
        LeftO.translate(-9.3, 1.1);
        this.world.addBody(LeftO);
        Rectangle Right = new Rectangle(1.0, 15.0);
        GameObject RightO = new GameObject();
        UserData.Generate(RightO, "Floor", true);
        RightO.addFixture(new BodyFixture(Right));
        RightO.setMass(MassType.INFINITE);
        RightO.translate(9.3, 1.1);
        this.world.addBody(RightO);
    }
    public static String Env = "Floor Only";

    public void changeEnv(String t) {
        Env = t;
        world.removeAllBodies();
        switch (Env) {
            case "Floor Only":
                CreateFloor();
                break;
            case "Boxed":
                CreateBox();
                break;

        }
    }

    Body addRandOb(int x, int y) {
        GameObject ObjectYo = new GameObject();
        int tsides = Sides;
        double tsize = Size;
        double tfric = FricTion;
        boolean tisstatic = isNextStaticObject;
        double tangvel = -StartingAngVel;
        String tname = LayoutManager.nameField.getText();
        Polygon polyShape = Geometry.createUnitCirclePolygon(tsides, tsize);
        LayoutManager.nameField.setText("");
        BodyFixture Fix = ObjectYo.addFixture(polyShape);
        Fix.setFriction(tfric);
        Fix.setRestitution(Bounce);
        ObjectYo.setMass(tisstatic ? MassType.INFINITE : MassType.NORMAL);
        ObjectYo.translate(convertToPosX(x), -((y - 350.0) / SCALE));
        ObjectYo.setAngularVelocity(Math.toRadians(tangvel * 10.0));
        UserData.Generate(ObjectYo, tname, false);
        this.world.addBody(ObjectYo);
        return ObjectYo;
    }
    long ClickDelay = 0;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        MouseDown = true;
        if (getBodyByPos(convertToPosX(e.getX()), convertToPosY(e.getY())) == null) {
            addRandOb(e.getX(), e.getY());
            ClickDelay = 0;
            return;
        }
        ClickDelay = System.currentTimeMillis();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MouseDown = false;
        int x = e.getX();
        int y = e.getY();
        if (System.currentTimeMillis() - ClickDelay <= 100) {
            for (Body b : this.world.getBodies()) {
                if (b.contains(new Vector2((x - 400.0) / SCALE, convertToPosY(y)))) {
                    this.world.removeBody(b);
                    return;
                }
            }
            currentSelectedBody = addRandOb(x, y);
            hasSelectedBody = true;
        }
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

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
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
                    if (!((UserData) getUserData()).isFix()) {
                        g.drawString(Double.toString(Math.round(Math.toDegrees(-this.angularVelocity) * dectemp) / dectemp), -5, this.mass.getType().equals(MassType.INFINITE) ? 2 : 11);
                    }
                } else {
                    g.drawString(((UserData) this.userData).getName(), -5, 2);
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
        this.canvas.addMouseMotionListener(this);
        CreateFloor();
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
        Thread ApplyForceThread = new Thread() {
            @Override
            public void run() {
                while (!isStopped()) {
                    try {
                        ApplyForceThread();
                    } catch (InterruptedException ex) {
                        new Popup(ex);
                        MainWindow.MAIN.dispose();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        ApplyForceThread.setDaemon(true);
        ApplyForceThread.start();
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
            if (this.world.getBodyCount() != 0) {
                this.world.update(elapsedTime / TimeSlow);
            }
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
            go.setAngularDamping(AirRes);
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
