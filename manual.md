# prototile-js

Prototile generates self-similar substitution tilings based on squares of different colors.
At each iteration, each square of the existing tiling is replaced by a set of squares
(here called a substitution tile) based on the color of the original square.
For iteration 0, an initial set of squares must be specified.

Additional information about substitution tilings can be found on
[Wikipedia][1] and on Dr. Natalie Priebe Frank's [tiling page][2], which also
features a Matlab-based relative of Prototile.

[1]: http://en.wikipedia.org/wiki/Substitution_tiling
[2]: https://pages.vassar.edu/nafrank/tiling-art/

## Instructions

* After adjusting parameters, click Update to generate a new tiling.
See Tiling Parameters for descriptions of the parameters.
* Click on the Initial or Substitutions tiles to change the color of individual squares.
Each click will cycle a square's color to the next color in the list of colors between 0 and # colors - 1.
* Click on the main tiling to shift a copy of the tiling over the main tiling.
The upper left square of the copy tiling will be placed over the square that is clicked in the main tiling.
This will produce new patterns in the overlapping region of the two tilings.
Half-step shifts can be achieved by editing the Offset Row and Offset Column parameters in the form.
* After clicking on Update the first time, the URL will be filled in with form parameters.
This URL can be bookmarked to retrieve the altered tiling.
The form parameter names and formats are described in the Tiling Parameters section.

## Tiling Parameters

| Form Parameter  | URL Parameter   | Description     |
| --------------- | --------------- | --------------- |
| \# Colors       | numColors       | The number of colors. For more than two colors, additional colors will be interpolated between the min color value and the max color value. |
| Initial Size    | initialSize     | The number of rows and columns in the initial tiling. |
| Square Size     | squareSize      | The width and height of each square in pixels. |
| \# Iterations   | iterations      | The number of substitution iterations. At each iteration, each square will be replaced by a substitution tile defined in the Substitutions section. Beware that increasing the number of iterations increases the size of the tiling exponentially. |
| Substition Size | substSize       | The number of rows and columns in each substitution tile. |
| Transparency    | transparency    | The transparency of the colors used in the main tiling. This is used when a copy of the main tiling is shifted over the main tiling to produce interesting patterns. Set to 1 for no transparency. |
| Min Color       | minColor        | The minimum color (default white). As a URL parameter (and in Safari), this is represented as a # (encoded as %23) followed by 6 hexadecimal digits. |
| Max Color       | maxColor        | The maximum color (default black). As a URL parameter (and in Safari), this is represented as a # (encoded as %23) followed by 6 hexadecimal digits. |
| Offset Row      | offsetRow       | The row offset of the copy tiling displayed over the main tiling. The upper left square of the copy tiling will be placed on this row of the main tiling. Half steps between 0 and the tiling height are supported. |
| Offset Column   | offsetColumn    | The column offset of the copy tiling displayed over the main tiling. The upper left square of the copy tiling will be placed on this column of the main tiling. Half steps between 0 and the tiling width are supported. |
| Initial         | initial         | The matrix to be used for the initial tiling. As a URL parameter, this is represented by a string of digits between 0 (min color) and # colors - 1. Commas (encoded as %2C) may be used to separate the rows of the matrix, but are not required. |
| Substitutions   | substitutions   | The matrices to be used for the substitution tiles. As a URL parameter, this is represented by a string of digits between 0 (min color) and # colors - 1. Commas (encoded as %2C) may be used to separate the rows of each substitution tile and to separate substitution tiles from each other, but they are not required. |
