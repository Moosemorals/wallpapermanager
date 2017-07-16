package com.moosemorals.wallpaper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Main
 * Created by osric on 15/07/17.
 */
public class Main {



    private static final String BASE_DIR = "/home/osric/Pictures/backdrop";

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Starting");

        File target = new File(BASE_DIR);

        final ImageList list = new ImageList(target);

        SwingUtilities.invokeLater(() -> {
            Ui ui = new Ui(list);

            try {
                list.reset();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
