package gui;

import imdb.Data;
import imdb.Episode;
import imdb.Series;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import serienchecker.Main;

public class MainGui extends JFrame {

  private JScrollPane scroll;
  private JPanel content;
  private static final Color[] color = {Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED};

  public MainGui() {
    super("Serienchecker");
    this.setBounds(0, 0, 640, 400);
    scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setBounds(0, 0, 640, 400);
    scroll.getVerticalScrollBar().setUnitIncrement(20);
    scroll.getVerticalScrollBar().setBlockIncrement(1);
    scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        e.getAdjustable().setValue((e.getAdjustable().getValue() / 10) * 10);
      }
    });
    this.add(scroll);
    init();
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  private void init() {
    int size = 0;
    Border b = new CustomLineBorder(true, true, false, false, Color.LIGHT_GRAY);
    content = new JPanel();
    content.setLayout(null);
    content.setBounds(0, 0, 620, 0);
    for (int i = 0; i < Main.data.size(); i++) {
      final Series s = Main.data.get(i);
      JLabel name = new JLabel();
      name.setBounds(120, size, 250, 20);
      name.setText(s.getName());
      name.setBorder(b);
      content.add(name);
      final JTextField desc = new JTextField();
      desc.setBounds(370, size, 250, 20);
      desc.setText(s.getText());
      desc.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          try {
            s.setText(desc.getText());
            Data.serialize(Main.data);
          } catch (FileNotFoundException ex) {
            Logger.getLogger(MainGui.class.getName()).log(Level.SEVERE, null, ex);
          } catch (IOException ex) {
            Logger.getLogger(MainGui.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
      content.add(desc);
      JLabel last = new JLabel();
      last.setBounds(20, size, 50, 20);
      Episode e = s.lastEpisode();
      if (e != null) {
        last.setText(e.getSeason().getNum() + ":" + e.getNum());
      } else {
        last.setText("0:0");
      }
      last.setHorizontalAlignment(SwingConstants.RIGHT);
      last.setBorder(b);
      content.add(last);

      JLabel count = new JLabel();
      count.setBounds(70, size, 50, 20);
      count.setText(" - " + s.watchedEpisodesCount());
      count.setHorizontalAlignment(SwingConstants.LEFT);
      count.setBorder(b);
      content.add(count);

      JButton edit = new JButton("X");
      edit.setBackground(color[s.status()]);
      final int seriesnum = i;
      edit.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          Main.x = getX();
          Main.y = getY();
          new SeriesGui(Main.data.get(seriesnum)).setVisible(true);
          setVisible(false);
        }
      });
      edit.setBounds(0, size, 20, 20);
      edit.setMargin(new Insets(0, 0, 0, 0));
      content.add(edit);
      size += 20;
    }
    JButton add = new JButton("ADD");
    add.setBounds(0, size, 620, 20);
    add.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String serie = JOptionPane.showInputDialog(content, "Gib den IMDB-Key der Serie ein", "Neue Serie", JOptionPane.INFORMATION_MESSAGE);
        if (serie != null && (serie.length() == 9 || serie.length() == 10)) {
          try {
            setTitle("!!Serienchecker!!");
            Main.data.add(new Series(serie));
            Data.serialize(Main.data);
            reInit();
            setTitle("Serienchecker");
          } catch (IOException ex) {
            Logger.getLogger(MainGui.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    });
    content.add(add);
    JButton update = new JButton("UPDATE");
    update.setBounds(0, size + 20, 620, 20);
    update.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
          Main.data.update();
        } catch (IOException ex) {
          Logger.getLogger(MainGui.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
    content.add(update);
    content.setPreferredSize(new Dimension(620, size + 40));
    scroll.setViewportView(content);
  }

  public void reInit() {
    content.setVisible(false);
    scroll.setViewportView(content);
    init();
  }

  @Override
  public void setVisible(boolean show) {
    if(show) this.setLocation(Main.x, Main.y);
    super.setVisible(show);
  }

}
