package com.moosemorals.wallpaper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;

/**
 * Have a look at image files
 * Created by osric on 15/07/17.
 */
public class ImageVisitor extends SimpleFileVisitor<Path> {

    private final Executor executor;
    private final ImageList list;

    ImageVisitor(Executor executor, ImageList list) {
        this.executor = executor;
        this.list = list;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {

        list.register();

        executor.execute(() -> {
            try {
                BufferedImage img = ImageIO.read(path.toFile());

                if (img == null) {
                    throw new IOException("Can't read image from [" + path.toString() + "]");
                }

                list.addImage(new ImageData(path, img));

            } catch (IOException ex) {
                list.registerError();
            }
        });

        return FileVisitResult.CONTINUE;
    }
}
