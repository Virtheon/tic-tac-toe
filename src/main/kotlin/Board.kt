package tictactoe

typealias Position = Pair<Int, Int>

enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

// Board keeps all the squares in a two-dimensional array of Symbols. They're stored by line first (the opposite
// order from the bracket operators) to make it easier to print (see: toString())

// For optimisation purposes, the board contains a three dimensional array of all possible axes through which
// a player can win. Each axis is an array of squares, and each square is a 2D vector representing the coordinates.
// AxisType provides a function to determine way the axis is oriented based on the index.
class Board private constructor(
	val size: Int,
	private val squares: Array<Array<Symbol>>,
	private val winAxes: Array<Array<IntArray>>
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
		VERTICAL('|'), HORIZONTAL('—'),
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
								intArrayOf(axisIndex % size, squareIndex)
							AxisOrientation.HORIZONTAL ->
								intArrayOf(squareIndex, axisIndex % size)
							AxisOrientation.DESCENDING ->
								intArrayOf(squareIndex, squareIndex)
							AxisOrientation.ASCENDING ->
								intArrayOf(size - 1 - squareIndex, squareIndex)
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
	private fun getWinAxis(symbol: Symbol): Array<IntArray>? {
		for (axis in winAxes) {
			for (square in axis) {
				if (this[square[0], square[1]] == symbol) {
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

	fun hasWon(symbol: Symbol): Boolean {
		return (getWinAxis(symbol) != null)
	}

	// The function looks for a winning axis. If there is one, it takes the string form of the board and
	// replaces the symbols in each square with a matching line.
	// If there is no winning axis, it returns null
	fun drawBoardWithWin(symbol: Symbol): String? {
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
				val column = square[0]
				val row = square[1]

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

	// The only function that directly accesses squares, seeing as it's optimised for printing line by line
	override fun toString(): String {
		val boardString = StringBuilder()
		// Uses indices to avoid printing underline after last row
		for (rowIndex in squares.indices) {
			val row = squares[rowIndex]
			val rowString = StringBuilder()

			for (column in row) {
				rowString.append("| $column ")
			}
			rowString.append("|")

			boardString.append(rowString)

			// No newlines or underlines after the last row.
			// Underlines just replace space, X and O with dashes and | with spaces.
			if (rowIndex < maxIndex) {
				boardString.append('\n')
				val underline = rowString.toString()
					.replace(' ', '—')
					.replace('X', '—')
					.replace('O', '—')
					.replace('|', ' ')
				boardString.append(underline).append('\n')
			}
		}
		return boardString.toString()
	}

}