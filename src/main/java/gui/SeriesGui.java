package gui;

import tmdb.Data;
import tmdb.Episode;
import tmdb.Season;
import tmdb.Series;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class SeriesGui extends JFrame{
    
    private JTabbedPane tabs;
    private Series series;

    public SeriesGui(Series s) {
        super(s.getName());
        series = s;
        this.setResizable(false);
        this.setBounds(Main.x, Main.y, 640, 400);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                Main.x = getX();
                Main.y = getY();
                Main.gui.setVisible(true);
                dispose();
            }
        });
        tabs = new JTabbedPane();
        tabs.setTabPlacement(JTabbedPane.TOP);
        MetalTabbedPaneUI ui = new MetalTabbedPaneUI(){
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(0, 0, 0, 0);
            }
        };
        tabs.setUI(ui);
        this.add(tabs);
        init();
    }

    private void init() {
        Border b = new CustomLineBorder(true, true, false, false, Color.LIGHT_GRAY);
        JComponent last = null;
        Episode el = series.lastEpisode();
        for (int i = 0; i < series.numSeasons(); i++) {
            final Season s = series.getSeason(i);
            JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBounds(0, 0, 640, 400);
            scroll.getVerticalScrollBar().setUnitIncrement(20);
            scroll.getVerticalScrollBar().setBlockIncrement(1);
            tabs.addTab(s.getNum()+"", scroll);
            JPanel content = new JPanel();
            scroll.setViewportView(content);
            content.setSize(620, s.getNumEpisodes()*20 + 20);
            content.setPreferredSize(new Dimension(620, s.getNumEpisodes()*20+ 20));
            content.setLayout(null);
            int pos = 0;
            for (int j = 0; j < s.getNumEpisodes(); j++) {
                final Episode e = s.getEpisode(j);
                JLabel id = new JLabel();
                id.setBounds(0, pos, 40, 20);
                id.setText(e.getNum()+"");
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
                watched.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        try {
                            e.setWatched(watched.isSelected());
                            Data.serialize(Main.data);
                            Main.gui.reInit();
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
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
                        Data.serialize(Main.data);
                        Main.gui.reInit();
                        reInit();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(SeriesGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            content.add(all);
            if(el != null && s.equals(el.getSeason())){
                last = scroll;
            }
        }
        if(last != null){
            tabs.setSelectedComponent(last);
        }
    }
    
    public void reInit(){
        this.tabs.setVisible(false);
        tabs = new JTabbedPane();
        this.add(tabs);
        init();
    }
    
    @Override
    public void setVisible(boolean show) {
        if(show) this.setLocation(Main.x, Main.y);
        super.setVisible(show);
    }
    
}
