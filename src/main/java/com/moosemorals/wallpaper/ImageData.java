package com.moosemorals.wallpaper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 *
 * Created by osric on 15/07/17.
 */
public class ImageData  {

    private final int width;
    private final int height;
    private final Path path;

    ImageData(Path path, BufferedImage image) {
        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    Path getPath() {
        return path;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageData imageData = (ImageData) o;

        return path.equals(imageData.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

}
