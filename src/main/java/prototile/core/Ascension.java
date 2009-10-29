/*
 * Copyright (C) 2009 Billie Rinaldi
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import processing.core.PApplet;
import processing.core.PImage;

public class Ascension extends PApplet implements Serializable {

	private static final long serialVersionUID = 3415207506759823983L;
	
	private static final int EST_MAX_PIX = 268435456/4;

	private int squareSize = 5;
	private int depth = 1;
	private int numColors = 2;
	public ArrayList<Integer> colorArray;
	
	private float angle = 0.0f;
	
	public boolean sliding = false;
	public boolean slidingPaused = false;
	public boolean snapping = false;
	public boolean inMotion = false;
	public boolean animating = false;

	private static final int PROTOTILE_SQUARE_SIZE = 10;
	private int initSize = 1;
	private int tileSize = 4;
	
	public Tile init;
	public ArrayList<Tile> tiles;
		
	private int imgWidthSquares;
	private int imgWidthPix;
	private PImage img;
	
	public Ascension(int squareSize, int depth, int numColors, int initSize, int tileSize) {
		this.squareSize = squareSize;
		this.depth = depth;
		this.numColors = numColors;
		this.initSize = initSize;
		this.tileSize = tileSize;
		colorArray = ColorUtil.initColorArray(numColors);
		init = new Tile(colorArray,initSize,PROTOTILE_SQUARE_SIZE,"init");
		tiles = new ArrayList<Tile>(numColors);
		for (int i = 0; i < numColors; i++) {
			tiles.add(new Tile(colorArray,tileSize,PROTOTILE_SQUARE_SIZE,"tile "+i));
		}
	}
	
	public void save(DataOutputStream dos) throws IOException {
		dos.writeBoolean(sliding);
		dos.writeBoolean(snapping);
		dos.writeInt(slideX);
		dos.writeInt(slideY);
		dos.writeFloat(angle);
		dos.writeInt(squareSize);
		dos.writeInt(depth);
		dos.writeInt(numColors);
		for (int i = 0; i < numColors; i++) {
			dos.writeInt(colorArray.get(i));
		}
		init.save(dos);
		for (int i = 0; i < numColors; i++) {
			tiles.get(i).save(dos);
		}
	}
	
	public void load(DataInputStream dis) throws IOException {
		noLoop();
		waitWhileDrawing("waiting to load until drawing completes");
		sliding = dis.readBoolean();
		slidingPaused = true;
		snapping = dis.readBoolean();
		slideX = dis.readInt();
		slideY = dis.readInt();
		mouseX = slideX+imgWidthPix/2;
		mouseY = slideY+imgWidthPix/2;
		angle = dis.readFloat();
		squareSize = dis.readInt();
		depth = dis.readInt();
		numColors = dis.readInt();
		setNumColors(numColors);
		for (int i = 0; i < numColors; i++) {
			colorArray.set(i, dis.readInt());
		}
		init.load(dis);
		initSize = init.matrix.length;
		for (int i = 0; i < numColors; i++) {
			tiles.get(i).load(dis);
		}
		tileSize = tiles.get(0).matrix.length;
		completeResize();
		loop();
		displayFrame.validate();
	}
	
	public void setNumColors(int newNumColors) {
		System.out.println("set num colors to "+newNumColors);
		ColorUtil.resetColorArray(colorArray,newNumColors);
		if (newNumColors < numColors) {
			for (int i = numColors-1; i >= newNumColors; i--) {
				removeTile(i);
			}
		}
		else if (newNumColors > numColors) {
			for (int i = numColors; i < newNumColors; i++) {
				addTile();
			}
		}
		numColors = newNumColors;
		init.setNumColors(numColors);
		for (Tile t : tiles) {
			t.setNumColors(numColors);
		}
	}
	
	public int getNumColors() {
		return numColors;
	}
	
	public void addTile() {
		Tile tile = new Tile(colorArray,tileSize,PROTOTILE_SQUARE_SIZE,"tile "+tiles.size());
		tiles.add(tile);
	}
	
	public void removeTile(int i) {
		tiles.remove(i);
	}
	
	public void setInitSize(int initSize) {
		System.out.println("set init size to "+initSize);
		this.initSize = initSize;
		this.init.setSize(initSize);
	}
	
	public int getInitSize() {
		return initSize;
	}
	
	public void setTileSize(int tileSize) {
		System.out.println("set tile size to "+tileSize);
		this.tileSize = tileSize;
		for (int i = 0; i < numColors; i++) {
			tiles.get(i).setSize(tileSize);
		}
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	public boolean setDepth(int depth) {
		if (depth > estDepthMax(squareSize))
			return false;
		System.out.println("set depth to "+depth);
		this.depth = depth;
		return true;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public boolean setSquareSize(int squareSize) {
		if (squareSize > estSquareSizeMax(depth))
			return false;
		System.out.println("set square size "+squareSize);
		this.squareSize = squareSize;
		return true;
	}
	
	public int getSquareSize() {
		return squareSize;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	/*
	 * Given a number of iterations, estimate the maximum square size
	 * that can be used with that number of iterations.
	 */
	public int estSquareSizeMax(int depth) {
		return (int)Math.floor(Math.sqrt(EST_MAX_PIX)/(calculateImgWidthSquares(initSize, tileSize, depth)));
	}
	
	/*
	 * Given a square size, estimate the maximum number of iterations
	 * that can be completed with that square size.
	 */
	public int estDepthMax(int squareSize) {
		return (int)Math.floor(Math.log(Math.sqrt(EST_MAX_PIX)/(squareSize*initSize))/Math.log(tileSize));
	}
	
	/*
	 * After any changes have been made to Ascension parameters,
	 * completeResize() must be called to recalculate image sizes.
	 */
	public void completeResize() {
		imgWidthSquares = calculateImgWidthSquares(initSize, tileSize, depth);
		imgWidthPix = squareSize*imgWidthSquares;
		System.out.println("width in squares "+imgWidthSquares+" width in pix "+imgWidthPix);
		resize(imgWidthPix, imgWidthPix);
		img.resize(imgWidthPix, imgWidthPix);
		System.out.println("completed resize to "+img.pixels.length);
		this.validate();
	}
	
	public static int calculateImgWidthSquares(int initSize, int tileSize, int depth) {
		return initSize*(int)Math.pow(tileSize, depth);
	}
	
	public int getImgWidthPix() {
		return imgWidthPix;
	}
	
	private int startX = 0;
	private int startY = 0;
	private int transX = 0;
	private int transY = 0;
	
	public void setAnimationParameters(int startX, int startY, int transX, int transY) {
		slideX = startX;
		slideY = startY;
		this.startX = startX;
		this.startY = startY;
		this.transX = transX;
		this.transY = transY;
	}
	
	public void resetAnimationParameters() {
		slideX = 0;
		slideY = 0;
		transX = 0;
		transY = 0;
	}
	
	public void stepBack() {
		slideX = (slideX - transX) % imgWidthPix;
		if (slideX < 0)
			slideX += imgWidthPix;
		slideY = (slideY - transY) % imgWidthPix;
		if (slideY < 0)
			slideY += imgWidthPix;
	}
	
	public void stepForward() {
		slideX = (slideX + transX) % imgWidthPix;
		slideY = (slideY + transY) % imgWidthPix;
	}
	
	public void resetAnimation() {
		slideX = startX;
		slideY = startY;
	}
	
	private boolean dragLock = false;
	private int pressX = 0;
	private int pressY = 0;
	
	public void mouseDragged() {
		if (dragLock) {
			grabAngle();
		}
		else {
			dragLock = true;
			pressX = mouseX;
			pressY = mouseY;
		}
	}
	
	public void mouseReleased() {
		if (mouseEvent.getClickCount()<=1) {
			if (!dragLock) {
				if (slidingPaused)
					slidingPaused = false;
				else
					slidingPaused = true;
			}
			else {
				slidingPaused = true;
			}
		} else {
			if (dragLock==false) {
				pressX = mouseX;
				pressY = mouseY;				
			}
			grabAngle();
			slidingPaused = false;
		}
		dragLock = false;
	}
	
	private void grabAngle() {
		int y = mouseY-pressY;
		int x = mouseX-pressX;
		double vecLen = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
		if (vecLen==0)
			angle = 0;
		else {
			angle = (float)Math.acos(x/vecLen);
			if (y<0)
				angle = (float)Math.PI*2 - angle;
		}
	}
	
	public void setup() {
		imgWidthSquares = calculateImgWidthSquares(initSize,tileSize,depth);
		imgWidthPix = imgWidthSquares*squareSize;
		size(imgWidthPix, imgWidthPix, P2D);
		img = createImage(imgWidthPix, imgWidthPix, RGB);
		System.out.println("width in squares "+imgWidthSquares+" width in pix "+imgWidthPix);
	}
	
	private int slideX = 0;
	private int slideY = 0;
	private boolean isDrawing = false;
	public void draw() {
		isDrawing = true;
		background(0xffffff);
		img.loadPixels();
		startDrawPix(depth);
		img.updatePixels();
		if (sliding && !slidingPaused && !dragLock && !animating) {
			slideX = mouseX-imgWidthPix/2;
			slideY = mouseY-imgWidthPix/2;
		}
		if (sliding || animating) {
			tint(255,77);
			image(img, 0, 0);
			translate(pressX,pressY);
			rotate(angle);
			if (snapping) {
				image(img, snapOffset(slideX-pressX), snapOffset(slideY-pressY));
			} else {
				image(img, slideX-pressX, slideY-pressY);
			}
			if (inMotion)
				stepForward();
		}
		else {
			noTint();
			image(img, 0, 0);
		}
		isDrawing = false;
	}
	
	/*
	 * Round integer i to a multiple of the square size.
	 */
	public int snapOffset(int i) {
		return Math.round(i/(float)squareSize)*squareSize;
	}
	
	/*
	 * Start iterating with the initMatrix.  For each square in 
	 * the initial matrix, execute drawPixRecurse with depth.
	 */
	private void startDrawPix(int depth) {
		for (int i = 0; i < initSize; i++) {
			for (int j = 0; j < initSize; j++) {
				drawPixRecurse(i, j, init.matrix[i][j], depth);
			}
		}
	}
	
	/*
	 * If depth equals 0, draw square of color v in position i, j.
	 * Otherwise, for each square in the matrix corresponding to 
	 * color v, execute drawPixRecurse with depth-1.
	 */
	private void drawPixRecurse(int i, int j, int v, int depth) {
		if (depth==0) {
			for (int k = 0; k < squareSize; k++) {
				for (int l = 0; l < squareSize; l++) {
					try {
						img.pixels[i*squareSize+k+(j*squareSize+l)*imgWidthPix] = colorArray.get(v);
					}
					catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("ERROR array out of bounds on "+i+" "+j+" "+k+" "+l+" "+(i*squareSize+k+(j*squareSize+l)*imgWidthPix)+" "+img.pixels.length);
						System.exit(1);
					}
				}
			}
			return;
		}
		int[][] b = tiles.get(v).matrix;
		for (int k = 0; k < b.length; k++) {
			for (int l = 0; l < b.length; l++) {
				drawPixRecurse(i*b.length+k, j*b.length+l, b[k][l], depth-1);
			}
		}
	}
	
	/*
	 * Returns when the current drawing cycle is complete.
	 * To be used before changing Ascension parameters.  For example:
	 *    ascension.noLoop();
	 *    ascension.waitWhileDrawing("waiting to complete task");
	 *    ... change ascension's parameters ...
	 *    ascension.loop();
	 */
	public void waitWhileDrawing(String msg) {
		while (isDrawing) {
			try {
				if (msg!=null && "".equals(msg))
					System.out.println(msg);
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// don't need to do anything here
			}
		}
	}
	
	protected static JFrame displayFrame = null;
	public static void main(String[] args) {
		Ascension asc = new Ascension(10,1,2,1,4);
		int dpH = 550;
		int dpW = 550;
		int spH = 150;
		int spW = 210;
		int cpH = 400;
		int cpW = 300;
		
		if (args.length>0) {
			if (args[0].equals("-split")) {
				displayFrame = ControlPanel.initJFrame("Ascension", 
						0, 0, 1000, 500);
				TileAnimationPanel dsp = TileAnimationPanel.init(asc);
				ControlPanel cp = ControlPanel.init(asc, dsp);
				JSplitPane jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						cp,dsp);
				jSplit.setOneTouchExpandable(true);
				jSplit.setDividerLocation(0.5);
				JSplitPane jSplit2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						jSplit,asc);
				jSplit2.setOneTouchExpandable(true);
				jSplit2.setDividerLocation(0.5);
				displayFrame.add(jSplit2);
				displayFrame.setJMenuBar(cp.jmb);
				displayFrame.validate();
			} else if (args[0].equals("-compact")){
				
				try {
					displayFrame = 
						ControlPanel.setUpFrames(asc, TilePanel.class,
								cpW, 0, dpW-50, dpH,
								0, 0, cpW, cpH,
								0, cpH+22, cpW, spH);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				displayFrame.getContentPane().add(asc);
			}
		} else {
			try {
				displayFrame = 
					ControlPanel.setUpFrames(asc, TileAnimationPanel.class,
							cpW+spW, 0, dpW, dpH, 
							0, 0, cpW, dpH,
							cpW, 0, spW, dpH);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			displayFrame.getContentPane().add(asc);
		}
		if (displayFrame==null) {
			System.out.println("Usage: Ascension\n" +
					"	additional argument of -compact or -split\n" +
					"	provides alternate window layout");
			System.exit(0);
		}
		asc.init();
	}
}