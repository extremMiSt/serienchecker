package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import serienchecker.Main;
import tmdbapi.query.Series;
import tmdbapi.query.Episode;
import tmdbapi.query.Season;

public class SeriesGui extends JFrame {

  private JTabbedPane tabs;
  private Series series;

  public SeriesGui(Series s) throws SQLException {
    super(s.getName());
    series = s;
    this.setResizable(false);
    this.setBounds(Main.x, Main.y, 640, 400);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        try {
          Main.data.getCon().commit();
          Main.gui.reInit();
        } catch (SQLException ex) {
          Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main.x = getX();
        Main.y = getY();
        Main.gui.setVisible(true);
        dispose();
      }
    });
    tabs = new JTabbedPane();
    tabs.setTabPlacement(JTabbedPane.TOP);
    MetalTabbedPaneUI ui = new MetalTabbedPaneUI() {
      @Override
      protected Insets getContentBorderInsets(int tabPlacement) {
        return new Insets(0, 0, 0, 0);
      }
    };
    tabs.setUI(ui);
    this.add(tabs);
    init();
  }

  private void init() throws SQLException {
    Border b = new CustomLineBorder(true, true, false, false, Color.LIGHT_GRAY);
    JComponent last = null;
    Episode el = series.lastEpisode();
    List<Season> seasons = series.getSeasons();
    for (int i = 0; i < seasons.size(); i++) {
      final Season s = seasons.get(i);
      List<Episode> episodes = s.getEpisodes();
      JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setBounds(0, 0, 640, 400);
      scroll.getVerticalScrollBar().setUnitIncrement(20);
      scroll.getVerticalScrollBar().setBlockIncrement(1);
      tabs.addTab(s.getNum() + "", scroll);
      JPanel content = new JPanel();
      scroll.setViewportView(content);
      content.setSize(620, episodes.size() * 20 + 20);
      content.setPreferredSize(new Dimension(620, episodes.size() * 20 + 20));
      content.setLayout(null);
      int pos = 0;
      for (int j = 0; j < episodes.size(); j++) {
        final Episode e = episodes.get(j);
        JLabel id = new JLabel();
        id.setBounds(0, pos, 40, 20);
        id.setText(e.getNum() + "");
        id.setBorder(b);
        content.add(id);
        JLabel name = new JLabel();
        name.setBounds(60, pos, 570, 20);
        name.setText(e.getName());
        name.setBorder(b);
        content.add(name);
        final JCheckBox watched = new JCheckBox();
        watched.setBounds(40, pos, 20, 20);
        watched.setSelected(e.isWatched());
        watched.setBorder(b);
        watched.setBorderPainted(true);
        watched.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            try {
              e.setWatched(watched.isSelected());
            } catch (SQLException ex) {
              Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        content.add(watched);
        pos += 20;
      }
      JButton all = new JButton("Mark all");
      all.setBounds(0, pos, 620, 20);
      all.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          try {
            s.setWatched(true);
            reInit();
          } catch (SQLException ex) {
            Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
      content.add(all);
      if (el != null && s.equals(el.getSeason())) {
        last = scroll;
      }
    }
    if (last != null) {
      tabs.setSelectedComponent(last);
    }
  }

  public void reInit() throws SQLException {
    this.tabs.setVisible(false);
    tabs = new JTabbedPane();
    this.add(tabs);
    init();
  }

  @Override
  public void setVisible(boolean show) {
    if (show) {
      this.setLocation(Main.x, Main.y);
    }
    super.setVisible(show);
  }

}
