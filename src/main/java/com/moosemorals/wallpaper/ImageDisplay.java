package com.moosemorals.wallpaper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageDisplay extends JPanel {

    private ImageData imageData;
    private BufferedImage image;

    private enum State {NO_IMAGE, LOADING, READY, ERROR};
    private State state = State.NO_IMAGE;
    ImageDisplay() {
        super();
    }

    void setImageData(ImageData imageData) {
        this.imageData = imageData;

        // Load the image off thread
        state = State.LOADING;
        new Thread(() -> {
            try {
                image = imageData.getImage();
                state = State.READY;
            } catch (IOException e) {
                state = State.ERROR;
            }
            repaint();
        }).start();
    }

    public Dimension getPreferredSize() {
        return new Dimension(1920,1080);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (state) {
            case READY:
                Dimension size = getSize();
                g.drawImage(image, 0, 0, null);
                break;
            case NO_IMAGE:
                g.drawString("Waiting for image", 20, 20);
                break;
            case LOADING:
                g.drawString("Loading image", 20, 20);
                break;
            case ERROR:
                g.drawString("There was a problme loading the last image", 20, 20);
                break;
        }
    }
}
