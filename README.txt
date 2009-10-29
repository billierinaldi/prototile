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

Prototile generates self-similar substitution tilings based on squares of 
different colors.  At each iteration, each square of the existing tiling is 
replaced by a set of squares based on the color of the original square.  For
iteration 0, an initial set of squares must be specified.

Additional information about substitution tilings can be found on Wikipedia and on
Dr. Natalie Priebe Frank's home page, which also features a Matlab-based relative 
of Prototile.
http://en.wikipedia.org/wiki/Substitution_tiling
http://math.vassar.edu/Faculty/Frank/default.html

Prototile has been tested with Java 1.6, Mac OS X Snow Leopard, Ubuntu 9.0.4, and 
Windows Vista.

1. Usage (single command line)

java -Xmx1024m -Xms1024m 
     -cp prototile-core-0.0.1-SNAPSHOT.jar:processing-core-1.0.7.jar 
     prototile.core.Ascension [-compact|-split]

An optional additional argument of -compact or -split provides an alternate
window layout.  For smaller screens, -compact or -split is recommended.

Note that when running this command in MS-DOS, the ':' between the two jars must
be replaced with ';'.

2. Tiling Settings

2a. Control Panel

The following settings appear on the Control Panel:
Square Size (in pixels)
Iterations (number of substitution iterations)
Init Size (in squares)
Tile Size (in squares)
Colors (number of colors)

Slide (on / off)
Snap to Squares (on / off)

When Slide is turned on, moving the mouse over the Display frame will move one 
copy of the tiling over another.  If Snap to Squares is also turned on, the 
sliding copy of the tiling will only appear at offsets that are even multiples of 
the Square Size.  The sliding copy can be frozen in place with a single click, 
then restarted with a single click.  The sliding copy can also be rotated by 
clicking and dragging the mouse in the Display frame.  Rotating freezes sliding,
but sliding can be restarted with a single click.  The angle rotated can be reset 
to zero by double clicking in the Display frame.

2b. Prototiles

The Prototiles frame allows you to set the initial (iteration 0) state of the
tiling and to specify what tile to substitute for each color under an iteration.  
It also allows you to choose the colors used by clicking on the "Color #" buttons.

2c. Animation

When in default or -split mode, the Prototiles frame also contains an Animate 
tab.  In the Animate tab, you can enter a starting position for the top copy of 
the tiling and a translation direction.  Both are measured in pixels.  To start 
the animation, click on Play.  The top tiling will move in the direction of the 
translation, looping back to the starting position when it no longer overlaps the 
bottom copy of the tiling.  To stop the animation, click Pause.  To move the top 
tiling back to the starting position, click Reset.  While paused, the top tiling 
can be stepped forward or backward.  The animation can be slowed down by adjusting
the Speed slider.

During animation, the display no longer responds to the mouse position, even if
sliding is enabled.  However, the top tiling can still be rotated by clicking and
dragging the mouse.  Rotation can be undone with a double click.

4. Loading, Saving, and Exporting

Prototile settings can be saved to files and loaded from files through the File 
Menu Load and Save options.  The Control Panel may need to be in focus for the 
accelerator keys to work (Ctrl-l and Ctrl-s).  Currently slide offsets are 
effectively not saved due to the sensitivity of the sliding to the mouse position.

The File Menu also contains an Export option (Ctrl-x).  Displays can be exported 
in .tif, .tga, .jpg, and .png format by specifying the appropriate file extension.
The default is .tif when the file extension is not specified, in which case .tif 
is appended to the file name. 