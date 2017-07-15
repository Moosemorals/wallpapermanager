package com.moosemorals.wallpaper;

import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Keep track of all the images, in a thread safe way.
 * Created by osric on 15/07/17.
 */
public class ImageList {

    private final List<ImageData> list;
    private int outstanding = 0;

    ImageList() {
        list = new LinkedList<ImageData>();
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

    void addImage(ImageData img) {
        synchronized (list) {
            list.add(img);
            outstanding -= 1;

            System.out.printf("Got %d images, %d outstanding\n", list.size(), outstanding);

            list.notifyAll();
        }
    }

    void processList(Consumer<ImageData> x) throws InterruptedException {
        synchronized (list) {
            while (outstanding > 0) {
                list.wait();
            }

            list.forEach(x);
        }
    }
}
