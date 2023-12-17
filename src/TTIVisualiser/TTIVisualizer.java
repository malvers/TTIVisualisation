package TTIVisualiser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TTIVisualizer extends JButton {

    private Timer fader;
    private float alpha = 0.0f;
    private Timer timer;
    ArrayList<BufferedImage> theImages = new ArrayList<>();
    private int imgToDrawIndex = 0;
    private BufferedImage imgToDraw = null;
    private boolean drawHelp = false;
    private int imgCount = 0;
    private int period = 10000;
    private boolean debugMode = false;
    private BufferedImage nextImgToDraw;
    private boolean fadingDone = true;

    public TTIVisualizer() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvents(e);
            }
        });

        handleMouseEvents();

        loadImages();

        createTimer();

//        createFader();
    }

    /// drawing functions //////////////////////////////////////////////////////////////////////////////////////////////+
    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (drawHelp) {
            drawHelpPage(g2d);
            return;
        }

        drawImage(g2d);

//        if (debugMode) {
            drawDebug(g2d);
//        }
    }

    private void drawImage(Graphics2D g2d) {

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

//        Composite originalComposite = g2d.getComposite();

//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - alpha));
        g2d.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);

//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(nextImgToDraw, 0, 0, getWidth(), getHeight(), this);

//        g2d.setComposite(originalComposite);
    }

    private void drawDebug(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.drawString("period: " + period, 10, 30);
    }

    private void drawHelpPage(Graphics2D g2d) {

        g2d.setColor(Color.lightGray);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int fs = 26;
        Font font = new Font("Arial", Font.PLAIN, fs);
        g2d.setFont(font);
        g2d.setColor(Color.DARK_GRAY);

        int xPos = 20;
        int dy = (int) (fs * 1.5);
        int yPos = dy;
        int tab = 350;

        g2d.drawString("D", xPos, yPos);
        g2d.drawString("Toggle debug mode", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("H", xPos, yPos);
        g2d.drawString("Show this help page", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("L", xPos, yPos);
        g2d.drawString("Set interval to 5 min", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("W", xPos, yPos);
        g2d.drawString("Quit the program", xPos + tab, yPos);
        yPos += dy;
    }

    /// helper function ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isDebug() {
        return debugMode;
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    private void loadImages() {

        String dirStr = System.getProperty("os.name").toLowerCase();

        if (dirStr.contains("win")) {
            dirStr = "G:\\Andere Computer\\My MacBook Pro\\AI\\TTIimages";
        } else {
            dirStr = "/Users/malvers/Library/Mobile Documents/com~apple~CloudDocs/AI/TTIimages";
        }

        Set<String> files = listFilesUsingJavaIO(dirStr);

        for (String fileStr : files) {

            if (!fileStr.endsWith("jpg") && !fileStr.endsWith("png")) {
                continue;
            }

            if (debugMode && imgCount++ > 3) {
                break;
            }

            BufferedImage img;
            try {
                File theFile = new File(dirStr + "/" + fileStr);

                System.out.println("theFile: " + theFile);

                img = ImageIO.read(theFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            theImages.add(img);
        }
        imgToDraw = theImages.get(imgToDrawIndex);
        nextImgToDraw = theImages.get(imgToDrawIndex + 1);
    }

    private void createTimer() {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (theImages.size() > 0 && fadingDone) {

                    imgToDraw = theImages.get(imgToDrawIndex);
                    nextImgToDraw = theImages.get(imgToDrawIndex + 1);

                    System.out.println("index: " + imgToDrawIndex);

//                    createFader();

                    imgToDrawIndex++;

                    if (imgToDrawIndex > theImages.size() - 1) {
                        imgToDrawIndex = 0;
                    }
                    repaint();
                }
            }
        }, 0, period);
    }

    private void createFader() {

        fadingDone = false;
        if (fader != null) {
            fader.cancel();
        }
        fader = new Timer();
        fader.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                alpha += 0.02f;

                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    fader.cancel();
                    fadingDone = true;
                }
                repaint();
            }
        }, 0, 100);
    }

    /// handle mouse events ////////////////////////////////////////////////////////////////////////////////////////////
    private void handleMouseEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocus();
            }
        });
    }

    /// handle key events //////////////////////////////////////////////////////////////////////////////////////////////
    private void handleKeyEvents(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_SPACE:
                break;
            case KeyEvent.VK_UP:
                period += 1000;
                if (period > 300000) { /// 5 min
                    period = 300000;
                }
                createTimer();
                break;
            case KeyEvent.VK_DOWN:
                period -= 1000; /// 1 second
                if (period < 1000) {
                    period = 1000;
                }
                createTimer();
                break;
            case KeyEvent.VK_LEFT:
                break;
            case KeyEvent.VK_RIGHT:
                imgToDraw = theImages.get(imgToDrawIndex);
                nextImgToDraw = theImages.get(imgToDrawIndex + 1);
                createFader();
                imgToDrawIndex++;
                break;

            /// number keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_4:
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                debugMode = !debugMode;
                break;
            case KeyEvent.VK_E:
                break;
            case KeyEvent.VK_H:
                drawHelp = !drawHelp;
                break;
            case KeyEvent.VK_L:
                period = 300000; /// 5 min
                createTimer();
                break;
            case KeyEvent.VK_M:
                break;
            case KeyEvent.VK_W:
                System.exit(0);
                break;
        }
        repaint();
    }

    /// finally the main function //////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame();
            f.setLocation(0, 0);

            TTIVisualizer v = new TTIVisualizer();
            f.add(v);

            if (!v.isDebug()) {
                f.setUndecorated(true);
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                device.setFullScreenWindow(f);
            } else {
                f.setSize(1200, 800);
            }

            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
            v.requestFocus();
        });
    }
}
