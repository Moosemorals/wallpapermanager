package com.moosemorals.wallpaper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Keep track of all the images, in a thread safe way.
 * Created by osric on 15/07/17.
 */
public class ImageList {

    private final List<ImageData> list;
    private int outstanding = 0;

    ImageList() {
        list = new LinkedList<>();
    }

    void reset(File target, Consumer<ImageData> andThen) throws IOException, InterruptedException {

        list.clear();

        Executor executor = Executors.newFixedThreadPool(4);

        Files.walkFileTree(target.toPath(), new ImageVisitor(executor, this));

        processList(andThen);
    }

    public int size() {
        synchronized (list) {
            return list.size();
        }
    }

    public ImageData get(int index) {
        synchronized (list) {
            return list.get(index);
        }
    }

    /** Tell us there's data coming
     *
     */
    void register() {
        synchronized (list) {
            outstanding += 1;
        }
    }

    /**
     * Tell us the data isn't coming.
     */
    void registerError() {
        synchronized (list) {
            outstanding -=1;
        }
    }

    /**
     * Add an Image to the list
     * @param img
     */
    void addImage(Path path, BufferedImage image) {
        synchronized (list) {
            list.add(new ImageData(path, image));
            outstanding -= 1;

            System.out.printf("Got %d images, %d outstanding\n", list.size(), outstanding);

            list.notifyAll();
        }
    }

    /**
     * Do something with the list once it's completed.
     * @param andThen Consumer&lt;ImageData&gt; called for every item in the list
     * @throws InterruptedException if we get bored waiting.
     */
    void processList(Consumer<ImageData> andThen) throws InterruptedException {
        synchronized (list) {
            while (outstanding > 0) {
                list.wait();
            }

            list.forEach(andThen);
        }
    }
}
