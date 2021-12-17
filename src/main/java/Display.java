import com.sun.javafx.binding.StringFormatter;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Display implements Runnable {

    public static final int GRAPHIC_HIGH = 50;
    public static final int GRAPHIC_WIDTH = 100;


    public void run() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Top10 crypto coins");


        String[] columnNames = {"Icon", "Name", "Price", "Graph"};
        DefaultTableModel tm = new DefaultTableModel(initCrypto(), columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 3:
                        return ImageIcon.class;
                    case 1:
                    case 2:
                        return String.class;
                    default: return Object.class;
                }
            }
        };
        JTable table = new JTable(tm);
        table.getColumnModel().getColumn(3).setPreferredWidth(((ImageIcon) tm.getValueAt(0, 3)).getIconWidth());
        table.setRowHeight(((ImageIcon) tm.getValueAt(0, 0)).getIconHeight());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton button = new JButton("Reload");
        button.addActionListener(e -> {
            tm.setDataVector(initCrypto(), columnNames);
            table.getColumnModel().getColumn(3).setPreferredWidth(((ImageIcon) tm.getValueAt(0, 3)).getIconWidth());
            table.setRowHeight(((ImageIcon) tm.getValueAt(0, 0)).getIconHeight());
            table.repaint();

        });
        frame.add(button, BorderLayout.NORTH);

        frame.pack();
        frame.setDefaultCloseOperation(3);

        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static Object[][] initCrypto() {
        Elements prices = CoinParser.getCoinPrice(CoinParser.getPager());
        Elements names = CoinParser.getCoinName(CoinParser.getPager());
        Elements icons = CoinParser.getCoinLinkImages(CoinParser.getPager());
        Elements graphs = CoinParser.getCoinWeeklyGraphic(CoinParser.getPager());
        System.out.println(" "+ prices.size() +" "+ names.size() +" "+ icons.size() +" "+ graphs.size() );
        if(prices.size() != names.size() || prices.size() != icons.size() ||prices.size() != graphs.size()){
            return new Object[][] {{}};
        }
        Object[][] data = new Object[prices.size()][4];
        for (int i = 0; i < prices.size(); i++) {
            data[i][0] = Downloader.downloadImage(icons.get(i).attr("src"));
            data[i][1] = names.get(i).text();
            data[i][2] = prices.get(i).text();
            data[i][3] = Downloader.downloadImage(graphs.get(i).attr("src"));
            System.out.println(data[i][0].getClass() + " " + data[i][3].getClass());
        }
        return data;
    }

}
