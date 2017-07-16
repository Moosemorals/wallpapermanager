package com.moosemorals.wallpaper;

import com.moosemorals.wallpaper.ImageData;
import com.moosemorals.wallpaper.ImageList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps a JTable to an ImageList (or vice versa)
 * Created by osric on 16/07/17.
 */
public class ImageListTableModel implements TableModel {

    private final ImageList imageList;
    private static final String[] COLUMN_NAMES = {"Width", "Height", "Path"};
    private static Set<TableModelListener> listeners;

    public ImageListTableModel(ImageList imageList) {
        this.imageList = imageList;
        listeners = new HashSet<>();
    }

    @Override
    public int getRowCount() {
        return imageList.size();
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
        ImageData entry = imageList.get(row);
        switch (col) {
            case 0:
                return String.format("%d", entry.getWidth());
            case 1:
                return String.format("%d", entry.getHeight());
            case 2:
                return entry.getPath().toString();
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
}
