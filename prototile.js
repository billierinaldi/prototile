/**
 * Copyright 2018 Billie Rinaldi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Prototile creates substitution tilings consisting of a grid of squares based on values
 * giving in an initial tiling and a set of subsitution tiles.
 *
 * Initial tiling and substitution tiles consist of elements between 0 and numColors - 1.
 * The substitution tiles will be recursively substituted into the initial tiling for the
 * given number of iterations. For example, a 0 in the tiling will be replaced by
 * the matrix given in substitutionTiles[0], a 1 in the tiling will be replaced by the
 * matrix substitutionTiles[1], etc.
 */
function Prototile(initialTiling=[[]], substitutionTiles=[[[]]]) {
  this.initialTiling = initialTiling;
  this.substitutionTiles = substitutionTiles;

  // Recursively create a substitution tiling matrix.
  this.iterate = function(maxIteration, iteration=0, previousTiling=this.initialTiling) {
    if (iteration == maxIteration) {
      // return if max iterations have been reached
      return previousTiling;
    }

    var substSize = this.substitutionTiles[0].length;
    var nextIteration = [];

    // start with an initial tiling
    for (var i = 0; i < previousTiling.length; i++) {
      for (var k = 0; k < substSize; k++) {
        // for each row of the initial tiling, create substSize rows in the next iteration
        nextIteration[i*substSize + k] = [];
      }
      for (var j = 0; j < previousTiling[i].length; j++) {
        // for each element of the initial tiling, substitute the element with the corresponding substitution tile
        var currentTile = this.substitutionTiles[previousTiling[i][j]];
        for (var k = 0; k < currentTile.length; k++) {
          nextIteration[i*substSize + k].push.apply(nextIteration[i*substSize + k], currentTile[k]);
        }
      }
    }
    // repeat until max iterations have been reached
    return this.iterate(maxIteration, iteration + 1, nextIteration);
  };

  // Create the substitution tiling matrix and return the value as a string
  // with the given row and column separators.
  this.getTilingAsString = function(maxIteration, colSep=' ', rowSep='\n') {
    var matrix = this.iterate(iterations);
    for (var i = 0; i < matrix.length; i++) {
      matrix[i] = matrix[i].join(colSep);
    }
    return matrix.join(rowSep);
  }

  // Resize the initial tiling.
  this.resizeInitialTiling = function(x, y) {
    resize(this.initialTiling, x, y);
  }

  // Resize the substitution tiles.
  this.resizeSubstitutionTiles = function(x, y, z) {
    resize(this.substitutionTiles, x, y, z);
  }

  // Resize a 1, 2, or 3 dimensional matrix to the specified dimensions,
  // setting any new entries to zero.
  var resize = function(matrix, x, y=null, z=null) {
    if (x == null) {
      // recursive resize is complete
      return;
    }
    while (matrix.length > x) {
      // matrix is too big, remove elements
      matrix.pop();
    }
    while (matrix.length < x) {
      // matrix is too small, add elements
      if (y == null) {
        // last iteration, add zero
        matrix.push(0);
      } else {
        // not the last iteration, add a new array
        matrix.push([]);
      }
    }
    matrix.forEach(function(d) {
      resize(d, y, z);
    })
  }
}
