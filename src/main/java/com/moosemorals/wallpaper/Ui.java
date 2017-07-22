package com.moosemorals.wallpaper;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * User interface
 * Created by osric on 16/07/17.
 */
public final class Ui {

    private static final Consumer<ImageData> REANAMER = entry -> {

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
    };

    Ui(ImageList imageList) {

        final JFrame frame = new JFrame("Wallpapers");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final ImageDisplay imageDisplay = new ImageDisplay();

        final JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setModel(imageList);

        for (int i = 0; i < imageList.getColumnCount(); i += 1) {
            TableColumn column = table.getColumnModel().getColumn(i);

            column.setPreferredWidth(imageList.getColumnWidth(i));
            column.setMaxWidth(imageList.getColumnWidth(i));
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int row = table.rowAtPoint(mouseEvent.getPoint());
                if (row >= 0) {
                    imageDisplay.setImageData(imageList.get(row));
                }
            }
        });



        frame.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imageDisplay));

        frame.pack();
        frame.setVisible(true);
    }

}
