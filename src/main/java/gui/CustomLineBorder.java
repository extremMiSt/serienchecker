package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class CustomLineBorder extends AbstractBorder{
    
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private Color color;

    public CustomLineBorder(boolean up, boolean down, boolean left, boolean right, Color c) {
        super();
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        color = c;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        g.setColor(color);
        if (up) {
            g.drawLine(0, 0, width, 0);
        }
        if(down){
            g.drawLine(0, height-1, width, height-1);
        }
        if(left){
            g.drawLine(0, 0, 0, height);
        }
        if(right){
            g.drawLine(width-1, 0, width-1, height);
        }
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.bottom = down?1:0;
        insets.top = up?1:0;
        insets.left = left?1:0;
        insets.right = right?1:0;
        return insets;
    }
    
}
