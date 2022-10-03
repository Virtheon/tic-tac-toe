enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

class Board(val size: Int) {
	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1
	private val squares = Array(size) { Array(size) { Symbol.EMPTY } }

	// Three-dimensional array that stores all axes in which a player can win.
	// Each axis is an array of blocks, and each block is a 2D vector
	// where N is the length of the board (ie. the number of columns/rows to go through), the first N axes are vertical,
	// the next N axes are horizontal, and the next two axes are top left to bottom right and top right to bottom left.
	// AxisType provides a function to simplify determining which type of axis it is based on the index
	val winAxes = Array(size * 2 + 2) { axisIndex ->
		Array(size) { blockIndex ->
			when (AxisType.getType(axisIndex, size)) {
				AxisType.VERTICAL ->
					intArrayOf(axisIndex % size, blockIndex)
				AxisType.HORIZONTAL ->
					intArrayOf(blockIndex, axisIndex % size)
				AxisType.DESCENDING ->
					intArrayOf(blockIndex, blockIndex)
				AxisType.ASCENDING ->
					intArrayOf(maxIndex - blockIndex, blockIndex)
			}
		}
	}

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

	// The function effectively checks the symbol against each winnable axis,
	// and switches out every symbol in that axis to a strikethrough in string form until a full axis is found.
	// then it returns the string it built. Alternatively, returns null if no win is found.
	fun checkForWin(symbol: Symbol): String? {
		val boardString = toString()
		for (axis in winAxes) {
			// Makes an easily editable 2D string to represent the board
			val editableBoard: MutableList<StringBuilder> = mutableListOf()
			for (line in boardString.split('\n')) {
				editableBoard.add(StringBuilder(line))
			}

			// Uses an index to know when to stop and return the final string
			for (blockIndex in axis.indices) {
				val block = axis[blockIndex]
				val column = block[0]
				val row = block[1]

				if (this[block[0], block[1]] == symbol) {
					// Two values to represent access to exact position in the editable board
					val lineIndex = row * 2
					val charIndex = column * 4 + 2

					// Changes strikethrough orientation based on whether it's a vertical, horizontal or diagonal axis
					val axisIndex = winAxes.indexOf(axis)
					val strikethrough: Char = AxisType.getType(axisIndex, size).strikethroughChar

					editableBoard[lineIndex][charIndex] = strikethrough

					if (blockIndex == maxIndex) {
						val finalBoard = StringBuilder()
						for (line in editableBoard) {
							finalBoard.append(line).append('\n')
						}
						finalBoard.deleteCharAt(finalBoard.length - 1)
						return finalBoard.toString()
					}
				} else {
					break
				}
			}
		}

		return null
	}

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
	println("Has O won: \n${board.checkForWin(Symbol.O)}")
	println("Has X won: \n${board.checkForWin(Symbol.X)}")
	println("Has EMPTY won: \n${board.checkForWin(Symbol.EMPTY)}")
}