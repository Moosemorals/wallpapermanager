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

    private enum Column {
        Path("Path", String.class, -1),
        Width("Width", Integer.class, 64),
        Height("Height", Integer.class, 64);

        private final String displayName;
        private final Class clazz;
        private final int width;

        Column(String displayName, Class clazz, int width) {
            this.displayName = displayName;
            this.clazz = clazz;
            this.width = width;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Class getClazz() {
            return clazz;
        }

        public int getWidth() {
            return width;
        }
    }

    private final Column[] columns = Column.values();


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
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col].getDisplayName();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columns[col].getClazz();
    }

    int getColumnWidth(int col) { return  columns[col].getWidth(); }

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
            case 1:
                return String.format("%d", entry.getWidth());
            case 2:
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
     //   SwingUtilities.invokeLater(() -> {
            synchronized (listeners) {
                for (TableModelListener l : listeners) {
                    l.tableChanged(e);
                }
            }
     //   });
    }

    void reset() throws IOException, InterruptedException {
        list.clear();

        Executor executor = Executors.newFixedThreadPool(4);

        Files.walkFileTree(target.toPath(), new ImageVisitor(executor, this));

        onComplete();
    }

    ImageData get(int index) {
        synchronized (list) {
            return list.get(index);
        }
    }

    /**
     * Tell us there's data coming
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
            outstanding -= 1;
        }
    }

    /**
     * Add an Image to the list
     */
    void addImage(Path path, BufferedImage image) {
        int size;
        synchronized (list) {
            list.add(new ImageData(path, image));
            outstanding -= 1;
            System.out.printf("Loaded %d images, %d oustanding\n", list.size(), outstanding);
            list.notifyAll();
            size = list.size() - 1;
        }
        notifyTableModelListeners(new TableModelEvent(this, size, size, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private void onComplete() throws InterruptedException {
        synchronized (list) {
            while (outstanding > 0) {
                list.wait();
            }
        }
    }

    /**
     * Do something with the list once it's completed.
     *
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
