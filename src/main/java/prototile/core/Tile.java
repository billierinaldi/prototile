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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import processing.core.PApplet;

public class Tile extends PApplet {
	private static final long serialVersionUID = -7391420297212543884L;
	private int squareSize = 10;
	private int matrixSize = 4;
	private int numColors = 2;
	public int[][] matrix = new int[0][0];
	public String name;
	
	public ArrayList<Integer> colorArray;
	
	public Tile(ArrayList<Integer> colorArray, int matrixSize, int squareSize, String name) {
		this.colorArray = colorArray;
		this.numColors = colorArray.size();
		this.squareSize = squareSize;
		this.matrixSize = matrixSize;
		this.matrix = new int[matrixSize][matrixSize];
		for (int i = 0; i < matrixSize; i++)
			for (int j = 0; j < matrixSize; j++)
				this.matrix[i][j] = 0;
		this.name = name;
	}
	
	public void save(DataOutputStream dos) throws IOException {
		dos.writeInt(matrixSize);
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				dos.writeInt(matrix[i][j]);
			}
		}
	}
	
	public void load(DataInputStream dis) throws IOException {
		setSize(dis.readInt());
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				matrix[i][j] = dis.readInt();
			}
		}		
	}
	
	public void setup() {
		colorMode(RGB);
		setSize(matrixSize);
		size(matrixSize*squareSize, matrixSize*squareSize, P2D);
	}

	public void draw() {
		colorMode(RGB);
		background(0xffffff);
		resize(matrixSize*squareSize, matrixSize*squareSize);
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				drawSquare(i,j,matrix[i][j],squareSize);
			}
		}
		setPreferredSize(new Dimension(matrixSize*squareSize, matrixSize*squareSize));
	}
	
	public void setSize(int s) {
		if (matrixSize == s)
			return;
		int[][] newMatrix = new int[s][s];
		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				if (i < matrixSize && j < matrixSize)
					newMatrix[i][j] = matrix[i][j];
				else
					newMatrix[i][j] = 0;
			}
		}
		matrixSize = s;
		matrix = newMatrix;
	}

	public void setNumColors(int n) {
		if (n < numColors) {
			for (int i = 0; i < matrixSize; i++) {
				for (int j = 0; j < matrixSize; j++) {
					if (matrix[i][j] >= n)
						matrix[i][j] = matrix[i][j] % n;
				}
			}
		}
		numColors = n;
	}

	void drawSquare(int i, int j, int squareColor, int squareSize) {
		try {
			stroke(colorArray.get(squareColor));
			fill(colorArray.get(squareColor));
			rect(i*squareSize,j*squareSize,squareSize,squareSize);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("caught index out of bounds on prototile (ok if you just sized down)");
		}
	}
	
	public void mouseEvent(MouseEvent event) {
		int x = event.getX()/squareSize;
		int y = event.getY()/squareSize;

		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			// do something for the mouse being pressed
			break;
		case MouseEvent.MOUSE_RELEASED:
			matrix[x][y] = (1 + matrix[x][y]) % numColors;
			break;
		case MouseEvent.MOUSE_CLICKED:
			// do something for mouse clicked
			break;
		case MouseEvent.MOUSE_DRAGGED:
			// do something for mouse dragged
			break;
		case MouseEvent.MOUSE_MOVED:
			// umm...
			break;
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("two");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setLocation(0,300);
		f.setSize(300,300);
		
		ArrayList<Integer> ca = ColorUtil.initColorArray(2);
		
		JPanel control = new JPanel();
		PApplet pa = new Tile(ca,1,10,"test");
		pa.init();
		pa.registerMouseEvent(pa);
		JPanel jp = new JPanel();
		jp.add(pa);
		
		control.add(jp);

		PApplet pa2 = new Tile(ca,5,10,"test2");
		pa2.init();
		pa2.registerMouseEvent(pa2);
		JPanel jp2 = new JPanel();
		jp2.add(pa2);
		
		control.add(jp2);
		
		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("Tab 1", null, jp);
		jtp.setMnemonicAt(0, KeyEvent.VK_1);
		jtp.addTab("Tab 2", null, jp2);
		jtp.setMnemonicAt(0, KeyEvent.VK_2);
		
		JPanel jp3 = new JPanel(new GridLayout(1,1));
		jp3.add(jtp);
		f.add(jp3);
		f.pack();
		f.setResizable(true);
	}
}