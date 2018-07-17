/*
 * Copyright (C) 2009-2018 Billie Rinaldi
 *
 * This file is part of Prototile.
 *
 *  Prototile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Prototile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Prototile.  If not, see <http://www.gnu.org/licenses/>.
 */
package prototile.core;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TilePanel extends JPanel {
  private static final long serialVersionUID = -3914575469182549875L;
  protected Ascension asc;
  protected ColorListener cl;
  protected JTabbedPane jtp;
  protected int numTiles;

  protected class ColorListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      ColorUtil.printColors(asc.colorArray);
      JButton jb = (JButton) e.getSource();
      int buttonNum = Integer.parseInt(jb.getName());

      Color newColor = JColorChooser.showDialog(asc, "Choose Color "
          + buttonNum, new Color(asc.colorArray.get(buttonNum)));
      if (newColor != null)
        asc.colorArray.set(buttonNum, newColor.getRGB());

      ColorUtil.printColors(asc.colorArray);
    }
  }

  public TilePanel() {
    super(new GridLayout(1, 1));
  }

  public static TilePanel init(Ascension asc) {
    TilePanel sp = new TilePanel();
    sp.cl = sp.new ColorListener();
    sp.jtp = new JTabbedPane();
    sp.jtp.addTab("Init", initTilePanel(asc.init, 0, sp.cl, false));
    int i = 0;
    for (Tile t : asc.tiles) {
      sp.jtp.addTab("Tile " + i, initTilePanel(t, i++, sp.cl, true));
    }
    sp.numTiles = asc.tiles.size();
    sp.add(sp.jtp);

    return sp;
  }

  public void addTile() {
    numTiles++;
    jtp.addTab("Tile " + (numTiles - 1),
        initTilePanel(asc.tiles.get(numTiles - 1), numTiles - 1, cl, true));
  }

  public void removeTile(int i) {
    jtp.removeTabAt(i);
    numTiles--;
  }

  public void setNumColors(int newNumColors) {
    while (newNumColors != numTiles) {
      if (newNumColors < numTiles) {
        removeTile(numTiles);
      } else if (newNumColors > numTiles) {
        addTile();
      }
    }
  }

  protected static JPanel initTilePanel(Tile tile, int i, ActionListener al,
      boolean withButton) {
    JPanel sJp = new JPanel();
    if (withButton) {
      JButton jb = new JButton("Color " + i);
      jb.setName("" + i);
      jb.addActionListener(al);
      sJp.add(jb);
    }
    sJp.add(tile);
    tile.init();
    tile.registerMouseEvent(tile);
    return sJp;
  }

  public void informSizeChange() {
    jtp.requestFocus();
    jtp.repaint();
  }
}
