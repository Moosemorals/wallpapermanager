package com.moosemorals.wallpaper;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Keep track of all the images, in a thread safe way.
 * Created by osric on 15/07/17.
 */
public final class ImageList implements TableModel {

    private static final String[] COLUMN_NAMES = {"Path", "Width", "Height"};

    private final File target;
    private final List<ImageData> list;
    private final Set<TableModelListener> listeners;

    private int outstanding = 0;

    ImageList(File target) {
        this.target = target;
        list = new LinkedList<>();
        listeners = new HashSet<>();
    }

    @Override
    public int getRowCount() {
        synchronized (list) {
            return list.size();
        }
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {
        ImageData entry;
        synchronized (list) {
            entry = list.get(row);
        }
        switch (col) {
            case 0:
                return entry.getPath().toString();
            case 2:
                return String.format("%d", entry.getWidth());
            case 1:
                return String.format("%d", entry.getHeight());
        }
        return null;
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {

    }

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {
        synchronized (listeners) {
            listeners.add(tableModelListener);
        }
    }

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {
        synchronized (listeners) {
            listeners.remove(tableModelListener);
        }
    }

    private void notifyTableModelListeners(TableModelEvent e) {
        SwingUtilities.invokeLater(() -> {
            synchronized (listeners) {
                for (TableModelListener l : listeners) {
                    l.tableChanged(e);
                }
            }
        });
    }

    void reset() throws IOException, InterruptedException {
        list.clear();

        Executor executor = Executors.newFixedThreadPool(4);

        Files.walkFileTree(target.toPath(), new ImageVisitor(executor, this));
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
        int row;
        synchronized (list) {
            list.add(new ImageData(path, image));
            row = list.size();
            outstanding -= 1;
            list.notifyAll();
        }

        notifyTableModelListeners(new TableModelEvent(this, row, row, TableModelEvent.INSERT));
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
