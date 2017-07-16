package com.moosemorals.wallpaper;

import javax.imageio.ImageIO;
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

        ImageList list = new ImageList();


        list.reset(target, entry -> {

            Path originalPath = entry.getPath();

            String originalName = originalPath.getFileName().toString();

            String extension = originalName.substring(originalName.lastIndexOf(".") + 1, originalName.length());
            String name = originalName.substring(0, originalName.lastIndexOf("."));

            String replacementName = String.format("%s - [%dx%d].%s", name, entry.getWidth(), entry.getHeight(), extension);

            Path replacementPath = originalPath.getParent().resolve(replacementName);

            System.out.printf("Moving %s to %s\n", originalPath.toString(), replacementPath.toString());
            //  if (!originalPath.toFile().renameTo(replacementPath.toFile())) {
            //      System.out.printf("  WARNING: Move failed");
            //  }

            //System.out.printf("[%d x %d] %s\n", entry.getWidth(), entry.getHeight(), entry.getPath().toString());
        });



    }

    static String join(String between, String... parts) {
        if (parts == null) {
            return null;
        }
        if (parts.length == 0) {
            return "";
        } else if (parts.length == 1) {
            return parts[0];
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < parts.length; i += 1) {
                if (i != 0) {
                    result.append(between);
                }
                result.append(parts[i]);
            }
            return result.toString();
        }

    }
}
