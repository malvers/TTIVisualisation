package TTIVisualiser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public TTIVisualizer() {

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
        }, 0, 1000);

        String dirStr = "/Users/malvers/Library/Mobile Documents/com~apple~CloudDocs/AI/images";
        dirStr = "G:\\Andere Computer\\My MacBook Pro\\AI\\TTIimages";

        Set<String> files = listFilesUsingJavaIO(dirStr);

        for (String fileStr : files) {

            System.out.println("fileStr: " + fileStr);

            if (!fileStr.endsWith("jpg") && !fileStr.endsWith("png")  ) {
                continue;
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

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(imgToDraw, 0, 0, imgToDraw.getWidth(), imgToDraw.getHeight(), this);
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
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
        });
    }
}
