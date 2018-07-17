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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel {
  private static final long serialVersionUID = 8205250247884603267L;
  private static final String SQUARE_SIZE_NAME = "cs";
  public static final int SQUARE_SIZE_MIN = 1;
  public static int SQUARE_SIZE_MAX = 16;

  private static final String INIT_SIZE_NAME = "i";
  public static final int INIT_SIZE_MIN = 1;
  public static final int INIT_SIZE_MAX = 16;

  private static final String TILE_SIZE_NAME = "ss";
  public static final int TILE_SIZE_MIN = 1;
  public static final int TILE_SIZE_MAX = 16;

  private static final String DEPTH_NAME = "d";
  public static final int DEPTH_MIN = 0;
  public static int DEPTH_MAX = 6;

  private static final String NUM_COLORS_NAME = "nc";
  public static final int NUM_COLORS_MIN = 2;
  public static final int NUM_COLORS_MAX = 16;

  private enum SliderID {
    SQUARE_SIZE, INIT_SIZE, TILE_SIZE, DEPTH, NUM_COLORS
  }

  private HashMap<String, SliderID> sliders = new HashMap<String, SliderID>();
  {
    sliders.put(SQUARE_SIZE_NAME, SliderID.SQUARE_SIZE);
    sliders.put(INIT_SIZE_NAME, SliderID.INIT_SIZE);
    sliders.put(TILE_SIZE_NAME, SliderID.TILE_SIZE);
    sliders.put(DEPTH_NAME, SliderID.DEPTH);
    sliders.put(NUM_COLORS_NAME, SliderID.NUM_COLORS);
  }

  private JSlider squareSizer;
  private JSlider depther;
  private JSlider initSizer;
  private JSlider tileSizer;
  private JSlider colorCounter;

  private JCheckBox slideCheckBox;
  private JCheckBox snapCheckBox;

  private Ascension asc;
  private TilePanel sp;
  public JMenuBar jmb;
  private boolean changeOverride = false;

  private class MenuManager implements ActionListener {
    final JFileChooser jfc;

    public MenuManager() {
      jfc = new JFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String action = e.getActionCommand();
      if ("x".equals(action)) {
        int returnVal = jfc.showSaveDialog(asc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          asc.noLoop();
          asc.waitWhileDrawing(null);
          asc.save(jfc.getSelectedFile().getAbsolutePath());
          asc.loop();
        }
      } else if ("l".equals(action)) {
        int returnVal = jfc.showOpenDialog(asc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          DataInputStream dis = null;
          try {
            dis = new DataInputStream(new FileInputStream(jfc.getSelectedFile()
                .getAbsolutePath()));
            asc.load(dis);
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e2) {
            e2.printStackTrace();
          } finally {
            try {
              if (dis != null)
                dis.close();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        }
        changeOverride = true;
        snapCheckBox.setSelected(asc.snapping);
        slideCheckBox.setSelected(asc.sliding);
        squareSizer.setValue(asc.getSquareSize());
        depther.setValue(asc.getDepth());
        initSizer.setValue(asc.getInitSize());
        tileSizer.setValue(asc.getTileSize());
        colorCounter.setValue(asc.getNumColors());
        changeOverride = false;
      } else if ("s".equals(action)) {
        int returnVal = jfc.showSaveDialog(asc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          DataOutputStream dos = null;
          try {
            dos = new DataOutputStream(new FileOutputStream(jfc
                .getSelectedFile().getAbsolutePath()));
            asc.save(dos);
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e2) {
            e2.printStackTrace();
          } finally {
            try {
              if (dos != null)
                dos.close();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        }
      } else if ("q".equals(action)) {
        Ascension.displayFrame.dispatchEvent(new WindowEvent(Window
            .getWindows()[0], WindowEvent.WINDOW_CLOSING));
      }
    }

  }

  private class TranslationManager implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (changeOverride)
        return;
      Object source = e.getItemSelectable();

      if (source == slideCheckBox) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          asc.sliding = true;
        } else {
          asc.sliding = false;
        }
      } else if (source == snapCheckBox) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          asc.snapping = true;
        } else {
          asc.snapping = false;
        }
      }
    }
  }

  private class SliderManager implements ChangeListener {
    int actualDepthMax;
    int actualSquareSizeMax;
    int newNumColors;

    @Override
    public void stateChanged(ChangeEvent e) {
      if (changeOverride)
        return;
      JSlider source = (JSlider) e.getSource();
      if (source.getValueIsAdjusting()
          || !sliders.containsKey(source.getName())) {
        return;
      }
      asc.noLoop();
      asc.waitWhileDrawing("waiting for drawing to finish before changing parameters");
      switch (sliders.get(source.getName())) {
      case SQUARE_SIZE:
        if (!asc.setSquareSize(squareSizer.getValue())) {
          String err = "Square size is too big for use with this depth";
          actualDepthMax = asc.estDepthMax(squareSizer.getValue());
          if (actualDepthMax >= DEPTH_MIN)
            err += ", try depth " + actualDepthMax;
          JOptionPane.showMessageDialog(null, err);
          squareSizer.setValue(asc.getSquareSize());
        }
        break;
      case DEPTH:
        if (!asc.setDepth(depther.getValue())) {
          String err = "Depth is too big for use with this square size";
          actualSquareSizeMax = asc.estSquareSizeMax(depther.getValue());
          if (actualSquareSizeMax >= SQUARE_SIZE_MIN)
            err += ", try square size " + actualSquareSizeMax;
          JOptionPane.showMessageDialog(null, err);
          depther.setValue(asc.getDepth());
        }
        break;
      case INIT_SIZE:
        asc.setInitSize(initSizer.getValue());
        sp.informSizeChange();
        break;
      case TILE_SIZE:
        asc.setTileSize(tileSizer.getValue());
        sp.informSizeChange();
        break;
      case NUM_COLORS:
        int oldNumColors = asc.getNumColors();
        newNumColors = colorCounter.getValue();
        if (newNumColors == oldNumColors)
          break;
        asc.setNumColors(newNumColors);
        sp.setNumColors(newNumColors);
        sp.informSizeChange();
        break;
      default:
        asc.loop();
        return;
      }
      asc.completeResize();
      asc.loop();
      ColorUtil.printColors(asc.colorArray);
    }
  }

  public static ControlPanel init(Ascension asc, TilePanel sp) {
    ControlPanel cp = new ControlPanel();
    cp.asc = asc;
    cp.sp = sp;
    JPanel controlPanel = new JPanel(new GridLayout(6, 1));
    SliderManager sl = cp.new SliderManager();
    controlPanel.add(createSliderPanel(
        cp.squareSizer = new JSlider(JSlider.HORIZONTAL, SQUARE_SIZE_MIN,
            SQUARE_SIZE_MAX, asc.getSquareSize()),
        (SQUARE_SIZE_MAX - SQUARE_SIZE_MIN) / 3, "Square Size", sl,
        SQUARE_SIZE_NAME));
    controlPanel.add(createSliderPanel(cp.depther = new JSlider(
        JSlider.HORIZONTAL, DEPTH_MIN, DEPTH_MAX, asc.getDepth()),
        (DEPTH_MAX - DEPTH_MIN) / 2, "Iterations", sl, DEPTH_NAME));
    controlPanel.add(createSliderPanel(cp.initSizer = new JSlider(
        JSlider.HORIZONTAL, INIT_SIZE_MIN, INIT_SIZE_MAX, asc.getInitSize()),
        (INIT_SIZE_MAX - INIT_SIZE_MIN) / 3, "Init Size", sl, INIT_SIZE_NAME));
    controlPanel.add(createSliderPanel(cp.tileSizer = new JSlider(
        JSlider.HORIZONTAL, TILE_SIZE_MIN, TILE_SIZE_MAX, asc.getTileSize()),
        (TILE_SIZE_MAX - TILE_SIZE_MIN) / 3, "Tile Size", sl, TILE_SIZE_NAME));
    controlPanel.add(createSliderPanel(
        cp.colorCounter = new JSlider(JSlider.HORIZONTAL, NUM_COLORS_MIN,
            NUM_COLORS_MAX, asc.getNumColors()),
        (NUM_COLORS_MAX - NUM_COLORS_MIN) / 3, "Colors", sl, NUM_COLORS_NAME));

    TranslationManager tm = cp.new TranslationManager();

    cp.slideCheckBox = new JCheckBox("Slide");
    cp.slideCheckBox.addItemListener(tm);

    cp.snapCheckBox = new JCheckBox("Snap to Squares");
    cp.snapCheckBox.addItemListener(tm);
    JPanel jp = new JPanel();
    jp.add(cp.slideCheckBox);
    jp.add(cp.snapCheckBox);
    controlPanel.add(jp);

    cp.jmb = new JMenuBar();
    JMenu jm = new JMenu("File");
    JMenuItem ljmi = new JMenuItem("Load");
    ljmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
        ActionEvent.CTRL_MASK));
    ljmi.setActionCommand("l");
    jm.add(ljmi);
    JMenuItem sjmi = new JMenuItem("Save");
    sjmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        ActionEvent.CTRL_MASK));
    sjmi.setActionCommand("s");
    jm.add(sjmi);
    JMenuItem xjmi = new JMenuItem("Export");
    xjmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        ActionEvent.CTRL_MASK));
    xjmi.setActionCommand("x");
    jm.add(xjmi);
    JMenuItem qjmi = new JMenuItem("Quit");
    qjmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        ActionEvent.CTRL_MASK));
    qjmi.setActionCommand("q");
    jm.add(qjmi);
    cp.jmb.add(jm);
    MenuManager mm = cp.new MenuManager();
    ljmi.addActionListener(mm);
    sjmi.addActionListener(mm);
    xjmi.addActionListener(mm);
    qjmi.addActionListener(mm);

    cp.add(controlPanel);
    return cp;
  }

  public static JPanel createSliderPanel(JSlider js, int majTick, String name,
      ChangeListener cl, String id) {
    js.setName(id);
    js.setPaintTicks(true);
    js.setPaintLabels(true);
    js.setMinorTickSpacing(1);
    js.setMajorTickSpacing(majTick);
    js.setSnapToTicks(true);
    js.addChangeListener(cl);

    JPanel jp = new JPanel();
    jp.add(new JLabel(name));
    jp.add(js);
    return jp;
  }

  public static JFrame initJFrame(String name, int x, int y, int width,
      int height) {
    JFrame jf = new JFrame(name);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.setVisible(true);
    jf.setResizable(true);
    jf.setLocation(x, y);
    jf.setSize(width, height);
    return jf;
  }

  public static JFrame setUpFrames(Ascension asc,
      Class<? extends TilePanel> spc, int dpX, int dpY, int dpW, int dpH,
      int cpX, int cpY, int cpW, int cpH, int spX, int spY, int spW, int spH)
      throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    Method spInit = spc.getMethod("init", new Class[] { Ascension.class });
    TilePanel sp = (TilePanel) spInit.invoke(null, asc);

    JFrame frame = ControlPanel.initJFrame("Prototiles", spX, spY, spW, spH);
    frame.add(sp);

    ControlPanel cp = ControlPanel.init(asc, sp);
    frame = ControlPanel.initJFrame("Control", cpX, cpY, cpW, cpH);
    frame.add(cp);
    frame.setJMenuBar(cp.jmb);

    return ControlPanel.initJFrame("Display", dpX, dpY, dpW, dpH);
  }
}
