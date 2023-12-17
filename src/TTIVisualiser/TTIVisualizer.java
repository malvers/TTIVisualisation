package TTIVisualiser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TTIVisualizer extends JButton {

    ArrayList<BufferedImage> theImages = new ArrayList<>();
    private int imgToDrawIndex = 0;
    private BufferedImage imgToDraw = null;
    private boolean drawHelp = false;
    private int imgCount = 0;

    public TTIVisualizer() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvents(e);
            }
        });

        loadImages();

        createTimer();
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

            if(imgCount++ > 2 ) {
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
    }

    private void createTimer() {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (theImages.size() > 0) {
                    imgToDraw = theImages.get(imgToDrawIndex);
                    System.out.println("index: " + imgToDrawIndex);
                    imgToDrawIndex++;
                    if (imgToDrawIndex > theImages.size() - 1) {
                        imgToDrawIndex = 0;
                    }
                    repaint();
                }
            }
        }, 0, 5000);
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        if (drawHelp) {
            drawHelpPage(g2d);
            return;
        }

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
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

        g2d.drawString("H", xPos, yPos);
        g2d.drawString("Show this help page", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("I", xPos, yPos);
        g2d.drawString("Init actual task", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Cmd W", xPos, yPos);
        g2d.drawString("Quit the program", xPos + tab, yPos);
        yPos += dy;
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    /// handle key events //////////////////////////////////////////////////////////////////////////////////////////////
    private void handleKeyEvents(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                break;
            case KeyEvent.VK_SPACE:
                break;
            case KeyEvent.VK_UP:
                break;
            case KeyEvent.VK_DOWN:
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_LEFT:
                break;
            case KeyEvent.VK_RIGHT:
                break;

            /// number keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_4:
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                break;
            case KeyEvent.VK_E:
                break;
            case KeyEvent.VK_H:
                drawHelp = !drawHelp;
                break;
            case KeyEvent.VK_M:
                break;
            case KeyEvent.VK_W:
                System.exit(0);
                break;
        }
        repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

            JFrame f = new JFrame();
            TTIVisualizer v = new TTIVisualizer();
            f.add(v);
            f.setUndecorated(true);
            device.setFullScreenWindow(f);
            f.setLocation(0, 0);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
            f.requestFocus();
        });
    }
}
