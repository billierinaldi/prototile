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
  this.numColors = substitutionTiles.length;

  // Recursively create a substitution tiling matrix.
  this.iterate = function(maxIteration, iteration=0, previousTiling=this.initialTiling) {
    if (iteration == maxIteration) {
      // return if max iterations have been reached
      return previousTiling;
    }

    var nextIteration = [];

    // start with an initial tiling
    for (var j = 0; j < previousTiling[0].length; j++) {
      // iterate over columns
      var currentRow = 0;
      for (var i = 0; i < previousTiling.length; i++) {
        // iterate over rows
        // for each element of the initial tiling, substitute the element with the corresponding substitution tile
        var currentTile = this.substitutionTiles[previousTiling[i][j]];
        for (var k = 0; k < currentTile.length; k++) {
          if (currentRow >= nextIteration.length) {
            // if the row doesn't exist yet, create an array for it
            nextIteration.push([]);
          }
          // append to current row and increment current row
          nextIteration[currentRow].push.apply(nextIteration[currentRow], currentTile[k]);
          currentRow++;
        }
      }
    }
    // repeat until max iterations have been reached
    return this.iterate(maxIteration, iteration + 1, nextIteration);
  };

  // Get tiling as points.
  this.getTilingAsPoints = function(maxIteration) {
    var matrix = this.iterate(maxIteration);
    var points = [];
    matrix.forEach(function(row, i) {
      row.forEach(function(cell, j) {
        if (cell > 0) {
          points.push([i, j]);
        }
      });
    });
    return points;
  }

  // Create the substitution tiling matrix and return the value as a string
  // with the given row and column separators.
  this.getTilingAsString = function(maxIteration, colSep=' ', rowSep='\n') {
    var matrix = this.iterate(maxIteration);
    return formatString(matrix, colSep, rowSep);
  }

  // Convert the initial tiling to a string with the given row and column separators.
  this.getInitialTilingAsString = function(colSep='', rowSep='') {
    return formatString(this.initialTiling, colSep, rowSep);
  }

  // Convert the substitutions to a string with the given separators.
  this.getSubstitutionsAsString = function(colSep='', rowSep='', tileSep=',') {
    return formatString(this.substitutionTiles, colSep, rowSep, tileSep);
  }

  // Convert a matrix to a string with the given row and column separators.
  var formatString = function(matrix, colSep=' ', rowSep='\n', tileSep=null) {
    var strArr = [];
    if (tileSep) {
      matrix.forEach(function(tile) {
        strArr.push(formatString(tile, colSep, rowSep));
      });
      return strArr.join(tileSep);
    } else {
      matrix.forEach(function(row) {
        strArr.push(row.join(colSep));
      });
      return strArr.join(rowSep);
    }
  }

  // Set initial tiling from a string.
  this.setInitialFromString = function(s) {
    var matrix = this.initialTiling;
    var k = 0;
    for (var i = 0; i < matrix.length; i++) {
      for (var j = 0; j < matrix[i].length; j++) {
        if (k == s.length || s[k] == ',') {
          matrix[i][j] = 0;
          continue;
        }
        matrix[i][j] = parseInt(s[k]);
        if (matrix[i][j] >= numColors) {
          matrix[i][j] = 0;
        }
        k++;
      }
      if (k < s.length && s[k] == ',') {
        k++;
      }
    }
  }

  // Set substitutions from a string.
  this.setSubstitutionsFromString = function(s) {
    var matrix = this.substitutionTiles;
    var l = 0;
    for (var i = 0; i < matrix.length; i++) {
      for (var j = 0; j < matrix[i].length; j++) {
        for (var k = 0; k < matrix[i][j].length; k++) {
          if (l == s.length || s[l] == ',') {
            matrix[i][j][k] = 0;
            continue;
          }
          matrix[i][j][k] = parseInt(s[l]);
          if (matrix[i][j][k] >= numColors) {
            matrix[i][j][k] = 0;
          }
          l++;
        }
        if (l < s.length && s[l] == ',') {
          l++;
        }
      }
    }
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
