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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TileAnimationPanel extends TilePanel {
  private static final long serialVersionUID = -1025379201568363828L;
  private JPanel tilePanel;
  private SpinnerNumberModel startX;
  private SpinnerNumberModel startY;
  private SpinnerNumberModel transX;
  private SpinnerNumberModel transY;
  private JButton stepBack;
  private JButton stepForw;

  private class SpinnerManager implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
      asc.setAnimationParameters(startX.getNumber().intValue(), startY
          .getNumber().intValue(), transX.getNumber().intValue(), transY
          .getNumber().intValue());
    }

  }

  private class FrameRateManager implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider) e.getSource();
      if (source.getValueIsAdjusting()) {
        return;
      }
      asc.frameRate(source.getValue());
    }
  }

  private class AnimationManager implements ActionListener, ChangeListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JButton jb = (JButton) e.getSource();
      String text = jb.getText();
      if ("Play".equals(text)) {
        jb.setText("Pause");
        stepBack.setEnabled(false);
        stepForw.setEnabled(false);
        asc.inMotion = true;
      } else if ("Pause".equals(text)) {
        jb.setText("Play");
        stepBack.setEnabled(true);
        stepForw.setEnabled(true);
        asc.inMotion = false;
      } else if ("Reset".equals(text)) {
        asc.resetAnimation();
      } else if ("< Step".equals(text)) {
        asc.stepBack();
      } else if ("Step >".equals(text)) {
        asc.stepForward();
      }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      if (jtp.getSelectedIndex() == 1) {
        asc.animating = true;
        asc.setAnimationParameters(startX.getNumber().intValue(), startY
            .getNumber().intValue(), transX.getNumber().intValue(), transY
            .getNumber().intValue());
      } else {
        asc.animating = false;
      }
    }
  }

  public static TileAnimationPanel init(Ascension asc) {
    TileAnimationPanel dsp = new TileAnimationPanel();
    dsp.asc = asc;
    dsp.cl = dsp.new ColorListener();
    dsp.jtp = new JTabbedPane();
    dsp.tilePanel = new JPanel();
    dsp.tilePanel.setLayout(new BoxLayout(dsp.tilePanel, BoxLayout.PAGE_AXIS));
    JPanel jp = new JPanel();
    jp.add(new JLabel("Init"));
    jp.add(initTilePanel(asc.init, 0, dsp.cl, false));
    dsp.tilePanel.add(jp);
    int i = 0;
    for (Tile t : asc.tiles) {
      dsp.tilePanel.add(initTilePanel(t, i++, dsp.cl, true));
    }
    dsp.numTiles = asc.tiles.size();
    dsp.jtp.addTab("Tile", dsp.tilePanel);

    dsp.startX = new SpinnerNumberModel(0, 0, 100, 1);
    dsp.startY = new SpinnerNumberModel(0, 0, 100, 1);
    dsp.transX = new SpinnerNumberModel(0, 0, 100, 1);
    dsp.transY = new SpinnerNumberModel(0, 0, 100, 1);
    SpinnerManager sm = dsp.new SpinnerManager();

    jp = new JPanel(new GridLayout(8, 1));

    JSlider js = new JSlider(JSlider.HORIZONTAL, 1, 60, 60);
    js.addChangeListener(dsp.new FrameRateManager());

    dsp.stepBack = new JButton("< Step");
    dsp.stepForw = new JButton("Step >");
    AnimationManager am = dsp.new AnimationManager();
    JPanel subJp = new JPanel();
    JButton jb = new JButton("Play");
    jb.addActionListener(am);
    subJp.add(jb);
    jb = new JButton("Reset");
    jb.addActionListener(am);
    subJp.add(jb);
    jp.add(subJp);

    subJp = new JPanel();
    dsp.stepBack.addActionListener(am);
    subJp.add(dsp.stepBack);
    dsp.stepForw.addActionListener(am);
    subJp.add(dsp.stepForw);
    jp.add(subJp);

    jp.add(new JLabel("Speed"));
    jp.add(js);

    addSliderSet(jp, "Starting Position", sm, dsp.startX, dsp.startY);
    addSliderSet(jp, "Translation", sm, dsp.transX, dsp.transY);

    dsp.jtp.addTab("Animate", jp);
    dsp.jtp.addChangeListener(am);

    dsp.add(dsp.jtp);
    return dsp;
  }

  public static void addSliderSet(JPanel jp, String name, SpinnerManager sm,
      SpinnerNumberModel x, SpinnerNumberModel y) {
    jp.add(new JLabel(name));
    JPanel subJp = new JPanel();
    x.addChangeListener(sm);
    subJp.add(new JSpinner(x));
    y.addChangeListener(sm);
    subJp.add(new JSpinner(y));
    jp.add(subJp);
  }

  @Override
  public void addTile() {
    numTiles++;
    tilePanel.add(initTilePanel(asc.tiles.get(numTiles - 1), numTiles - 1, cl,
        true));
  }

  @Override
  public void removeTile(int i) {
    tilePanel.remove(i);
    numTiles--;
  }
}
