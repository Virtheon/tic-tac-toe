/*
Copyright 2022 Henrique Aguiar

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tictactoe

typealias Position = Pair<Int, Int>

enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

// Board keeps all the squares in a two-dimensional array of Symbols. They're stored by line first (the opposite
// order from the bracket operators) to make it easier to print (see: toString())

// For optimisation purposes, the board contains a two-dimensional of all possible axes through which
// a player can win. Each axis is an array of squares, and each square is a 2D vector representing the coordinates.
// AxisOrientation provides a function to determine way the axis is oriented based on the index.
// TODO: reduce clone allocations for computer calculations (maybe add undo function?)
// TODO: reimplement winning axes into four types: horizontal, vertical, and the two diagonals, the former two having row/column properties so they can draw themselves on the board
class Board private constructor(
	val size: Int,
	private val squares: Array<Array<Symbol>>,
	private val winAxes: Array<Array<Position>>
) {
	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1
	val isFull: Boolean
		get() {
			for (column in 0 until size) {
				for (row in 0 until size) {
					if (this[column, row] == Symbol.EMPTY) {
						return false
					}
				}
			}
			return true
		}

	enum class AxisOrientation(val strikethroughChar: Char) {
		VERTICAL('|'), HORIZONTAL('-'),
		DESCENDING('\\'), ASCENDING('/');

		companion object {
			@JvmStatic
			fun getType(axisIndex: Int, boardSize: Int): AxisOrientation {
				return if (axisIndex < boardSize) VERTICAL
				else if (axisIndex < boardSize * 2) HORIZONTAL
				else if (axisIndex == boardSize * 2) DESCENDING
				else ASCENDING

			}
		}
	}

	operator fun get(column: Int, row: Int): Symbol = squares[row][column]
	operator fun set(column: Int, row: Int, value: Symbol) {
		squares[row][column] = value
	}

	operator fun get(position: Position): Symbol = this[position.first, position.second]
	operator fun set(position: Position, value: Symbol) {
		this[position.first, position.second] = value
	}

	// Normal constructor.
	// Based on the size, builds a clean 2D array for squares, and
	// generates a 3D array with each square's coordinates through each axis
	constructor(size: Int = 3) :
			this(size,

				Array(size) { Array(size) { Symbol.EMPTY } },

				Array(size * 2 + 2) { axisIndex ->
					Array(size) { squareIndex ->
						when (AxisOrientation.getType(axisIndex, size)) {
							AxisOrientation.VERTICAL ->
								Position(axisIndex % size, squareIndex)
							AxisOrientation.HORIZONTAL ->
								Position(squareIndex, axisIndex % size)
							AxisOrientation.DESCENDING ->
								Position(squareIndex, squareIndex)
							AxisOrientation.ASCENDING ->
								Position(size - 1 - squareIndex, squareIndex)
						}
					}
				})

	constructor(board: Board) :
			this(
				board.size,

				board.squares.map { row ->
					row.clone()
				}.toTypedArray(),

				board.winAxes
			)

	// Checks for the symbol on each square in each axis
	// Returns when all squares have been checked, and immediately tries a new axis if any square doesn't match
	// Returns null if there is no winning axis
	private fun getWinAxis(symbol: Symbol): Array<Position>? {
		for (axis in winAxes) {
			for (square in axis) {
				if (this[square] == symbol) {
					if (axis.indexOf(square) == maxIndex) {
						return axis
					}
				} else {
					break
				}
			}
		}
		return null
	}

	// TODO: cache the winAxis so it doesn't need to be generated twice when drawWithWin is called
	fun hasWon(symbol: Symbol): Boolean {
		return (getWinAxis(symbol) != null)
	}

	// The function looks for a winning axis. If there is one, it takes the string form of the board and
	// replaces the symbols in each square with a matching line.
	// If there is no winning axis, it returns null
	fun drawWithWin(symbol: Symbol): String? {
		val winAxis = getWinAxis(symbol)
		if (winAxis == null) {
			return null
		} else {
			// Builds a 2D table of editable strings based on the original string form
			val boardString = toString()
			val editableBoard = mutableListOf<StringBuilder>()
			for (line in boardString.split('\n')) {
				editableBoard.add(StringBuilder(line))
			}

			for (square in winAxis) {
				val (column, row) = square

				val lineIndex = row * 2
				val charIndex = column * 4 + 2

				// Changes strikethrough orientation based on whether it's a vertical, horizontal or diagonal axis
				val axisIndex = winAxes.indexOf(winAxis)
				val strikethrough: Char = AxisOrientation.getType(axisIndex, size).strikethroughChar

				editableBoard[lineIndex][charIndex] = strikethrough
			}

			val finalBoard = StringBuilder()
			for (line in editableBoard) {
				finalBoard.append(line).append('\n')
			}
			finalBoard.deleteCharAt(finalBoard.length - 1)
			return finalBoard.toString()
		}
	}

	fun clone(): Board = Board(this)

	// TODO: outsource string construction to another function which takes a Map<Position, Character>
	// TODO: add board string with numbers (so player knows which number will mark which position)
	// TODO: alter board to look more like traditional tic-tac-toe board without walls
	// The only function that directly accesses squares, seeing as it's optimised for printing line by line
	override fun toString(): String {
		val boardString = StringBuilder()
		// Uses indices to avoid printing underline after last row
		for (rowIndex in squares.indices) {
			val row = squares[rowIndex]
			val rowString = StringBuilder()

			for (column in row) {
				rowString.append("│ $column ")
			}
			rowString.append("│")

			boardString.append(rowString)

			// No newlines or underlines after the last row.
			// Underlines just replace space, X and O with dashes and | with spaces.
			if (rowIndex < maxIndex) {
				boardString.append('\n')
				val underline = rowString.toString()
					.replace(' ', '─')
					.replace('X', '─')
					.replace('O', '─')
					.replace('│', '┼')
				boardString.append(underline).append('\n')
			}
		}
		return boardString.toString()
	}

}