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
typealias Axis = List<Position>

enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

// TODO: change comments
class Board private constructor(
	val size: Int,
	private val grid: Map<Position, Symbol>,
	private val winAxes: List<Axis>
) {
	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1
	val isFull: Boolean
		get() = grid.none { it.value == Symbol.EMPTY }

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

	operator fun get(column: Int, row: Int): Symbol? = grid[column to row]
	operator fun get(position: Position): Symbol = this[position]


	constructor(size: Int = 3) :
			this(size,

				List(size) { List(size) { Symbol.EMPTY } }
					.flatMapIndexed { columnIndex: Int, row: List<Symbol> ->
						row.mapIndexed { rowIndex, symbol ->
							(columnIndex to rowIndex) to symbol
						}
					}
					.toMap()
					// Sorted by row value first so it can be printed row by row
					.toSortedMap(compareBy<Position> { it.second }.thenBy { it.first }),

				List(size * 2 + 2) { axisIndex ->
					List(size) { squareIndex ->
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

	fun withMove(symbol: Symbol, position: Position) = Board(
		size,
		grid.mapValues { if (it.key == position) symbol else it.value },
		winAxes
	)

	private fun getWinningAxis(symbol: Symbol): Axis? {
		val symbolMarksSquare = { potentialSquare: Position ->
			this[potentialSquare] == symbol
		}
		val symbolFillsAxis = { potentialAxis: Axis ->
			potentialAxis.count(symbolMarksSquare) == size
		}

		return winAxes.firstOrNull(symbolFillsAxis)
	}

	fun hasWon(symbol: Symbol): Boolean = getWinningAxis(symbol) != null

	fun drawWithWin(symbol: Symbol): String? {
		val winAxis = getWinningAxis(symbol)
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

	private var underline: String? = null

	private fun underlineOf(row: String): String = underline
			?: row.split("\n").last()
				.replace(' ', '-')
				.replace('X', '-')
				.replace('O', '-')
				.replace('|', ' ')
				.apply { underline = this }

	override fun toString(): String =
		grid.toList()
			.fold("| ") { str, (position, symbol) ->
				val (column, row) = position
				str + if (column == maxIndex) {
					if (row == maxIndex) {
						"$symbol |"
					} else {
						"$symbol |\n" + underlineOf("$str$symbol |") + "\n| "
					}
				} else {
					"$symbol | "
				}
			}

}