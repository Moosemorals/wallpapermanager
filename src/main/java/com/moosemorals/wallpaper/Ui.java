package com.moosemorals.wallpaper;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * User interface
 * Created by osric on 16/07/17.
 */
public final class Ui {

    private final ImageList imageList;

    public UI(ImageList imageList) {
        this.imageList = imageList;

        final JFrame frame = new JFrame("Wallpapers");

        final TableModel model = new ImageListTableModel(imageList);

        final JTable table = new JTable(model);

        frame.add(table);

        frame.pack();
        frame.setVisible(true);
    }


}
