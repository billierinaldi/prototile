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

import java.awt.Color;
import java.util.ArrayList;

public final class ColorUtil {
	public static int defaultColor(int i, int n) {
		int shade = 255 * (n-1-i) / (n-1);
		return new Color(shade, shade, shade).getRGB();
	}
	
	public static ArrayList<Integer> initColorArray(int numColors) {
		ArrayList<Integer> colorArray = new ArrayList<Integer>(numColors);
		
		for (int i = 0; i < numColors; i++) {
			colorArray.add(defaultColor(i,numColors));
		}
		return colorArray;
	}
	
	public static void resetColorArray(ArrayList<Integer> colorArray, int newNumColors) {
		int numColors = colorArray.size();
		System.out.println("resetting with "+newNumColors);
		if (newNumColors < numColors) {
			for (int i = numColors-1; i >= newNumColors; i--) {
				colorArray.remove(i);
			}
		}
		for (int i = 0; i < Math.min(numColors, newNumColors); i++) {
			if (colorArray.get(i)==defaultColor(i,numColors))
				colorArray.set(i, defaultColor(i,newNumColors));			
		}
		for (int i = numColors; i < newNumColors; i++) {
			colorArray.add(defaultColor(i,newNumColors));
		}
	}
	
	public static void printColors(ArrayList<Integer> colorArray) {
		System.out.print(colorArray.size()+" colors:");
		for (int i = 0; i < colorArray.size(); i++)
			System.out.print(String.format(" %d %08x",i,colorArray.get(i)));
		System.out.println();
	}
}
