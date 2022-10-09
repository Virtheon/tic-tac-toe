enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

class Board private constructor(
	val size: Int,
	private val winAxes: Array<Array<IntArray>>,
	private val squares: Array<Array<Symbol>>
) {
	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1

	// TODO clean up comments (again)
	// Three-dimensional array that stores all axes in which a player can win.
	// Each axis is an array of squares, and each square is a 2D vector
	// where N is the length of the board (ie. the number of columns/rows to go through), the first N axes are vertical,
	// the next N axes are horizontal, and the next two axes are top left to bottom right and top right to bottom left.
	// AxisType provides a function to simplify determining which type of axis it is based on the index

	enum class AxisType(val strikethroughChar: Char) {
		VERTICAL('|'), HORIZONTAL('—'),
		DESCENDING('\\'), ASCENDING('/');

		companion object {
			@JvmStatic
			fun getType(axisIndex: Int, boardSize: Int): AxisType {
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

	constructor(size: Int) :
			this(size,

				Array(size * 2 + 2) { axisIndex ->
					Array(size) { squareIndex ->
						when (AxisType.getType(axisIndex, size)) {
							AxisType.VERTICAL ->
								intArrayOf(axisIndex % size, squareIndex)
							AxisType.HORIZONTAL ->
								intArrayOf(squareIndex, axisIndex % size)
							AxisType.DESCENDING ->
								intArrayOf(squareIndex, squareIndex)
							AxisType.ASCENDING ->
								intArrayOf(size - 1 - squareIndex, squareIndex)
						}
					}
				},

				Array(size) { Array(size) { Symbol.EMPTY } })

	constructor(board: Board) :
			this(
				board.size,
				board.winAxes,
				board.squares.map { row ->
					row.clone()
				}.toTypedArray()
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
				val strikethrough: Char = AxisType.getType(axisIndex, size).strikethroughChar

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

	// TODO: perhaps avoid using squares val directly
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

			// TODO check if there's a more efficient way to do this
			// No newlines or underlines after the last row
			if (rowIndex < maxIndex) {
				boardString.append('\n')
				var underline = rowString.toString()
				underline = underline
					.replace(' ', '—').replace('X', '—').replace('O', '—')
					.replace('|', ' ')
				boardString.append(underline).append('\n')
			}
		}
		return boardString.toString()
	}

}

fun main() {
	val board = Board(3)
	board[0, 0] = Symbol.X
	board[2, 2] = Symbol.X
	board[1, 1] = Symbol.X

	println(board)
	println("Has O won: \n${board.drawBoardWithWin(Symbol.O)}")
	println("Has X won: \n${board.drawBoardWithWin(Symbol.X)}")
	println("Has EMPTY won: \n${board.drawBoardWithWin(Symbol.EMPTY)}")

	val b2 = Board(board)
	b2[0, 0] = Symbol.O
	println("First board:\n$board")
	println("Second board\n$b2")
}