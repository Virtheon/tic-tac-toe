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

// TODO: perhaps make into a value class? with column and row vals
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
	operator fun get(position: Position): Symbol? = grid[position]


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

	// TODO: pass underline through constructor?
	private var underline: String =
		grid.rowStringAt(0)
			.replace(' ', '─')
			.replace('X', '─')
			.replace('O', '─')
			.replace('|', '┼')

	private fun Map<Position, String>.rowStringAt(row: Int): String =
		filterKeys { it.second == row }
			.toList().fold(" ") { str, (position, symbol) ->
				str + symbol + if (position.first == maxIndex) " " else " │ "
			}

	@JvmName("rowStringAtWithSymbol")
	private fun Map<Position, Symbol>.rowStringAt(row: Int): String =
		mapValues { it.value.toString() }.rowStringAt(row)

	private tailrec fun concatenateRows(
		string: String = "",
		row: Int = 0,
		grid: Map<Position, String> = this.grid.mapValues { it.value.toString() }
	): String =
		when (row) {
			0 -> concatenateRows("${grid.rowStringAt(row)}\n$underline", 1, grid)
			maxIndex -> concatenateRows("$string\n${grid.rowStringAt(row)}", row + 1, grid)
			size -> string
			else -> concatenateRows("$string\n${grid.rowStringAt(row)}\n$underline", row + 1, grid)
		}

	fun drawWithWin(symbol: Symbol): String? {
		val winAxis = getWinningAxis(symbol)
		val char = AxisOrientation.getType(winAxes.indexOf(winAxis), size).strikethroughChar.toString()
		return if (winAxis != null) {
			val updatedGrid = grid.mapValues { if (winAxis.contains(it.key)) char else it.value.toString() }
			concatenateRows(grid = updatedGrid)
		} else null
	}

	fun drawWithNumbers(): String =
		concatenateRows(grid = grid.mapValues { (it.key.second * 3 + it.key.first + 1).toString() })

	override fun toString(): String {
		return concatenateRows()
	}

}