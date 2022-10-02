enum class Symbol(private val string: String) {
	EMPTY(" "), X("X"), O("O");

	override fun toString(): String = string
}

class Board(val size: Int) {
	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1
	private val squares = Array(size) { Array(size) { Symbol.EMPTY } }

	// Three-dimensional array that stores all axes in which a player can win
	// Each axis is an array of blocks, and each block is a 2D vector
	// Where X is the length of the board, the first X axes are vertical,
	// the next X axes are horizontal, and the next two axes are top left to bottom right and top right to bottom left
	val winAxes = Array(size * 2 + 2) { axis ->
		Array(size) { block ->
			if (axis < size) {
				intArrayOf(axis % size, block)
			} else if (axis < size * 2) {
				intArrayOf(block, axis % size)
			} else if (axis % (size * 2) == 0) {
				intArrayOf(block, block)
			} else {
				intArrayOf(maxIndex - block, block)
			}
		}
	}

	operator fun get(column: Int, row: Int): Symbol = squares[row][column]
	operator fun set(column: Int, row: Int, value: Symbol) {
		squares[row][column] = value
	}

	// The function effectively checks the symbol against each winnable axis,
	// and switches out every symbol in that axis to a line in string form until a full axis is found.
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
					// Two functions to represent the relationship between the block indexes and their position on
					// the editable board
					fun lineIndex(row: Int) = row * 2
					fun characterIndex(column: Int) = column * 4 + 2

					// Changes line orientation based on whether it's a vertical, horizontal or diagonal axis
					val axisIndex = winAxes.indexOf(axis)
					if (axisIndex < size) {
						editableBoard[lineIndex(row)][characterIndex(column)] = '|'
					} else if (axisIndex < size * 2) {
						editableBoard[lineIndex(row)][characterIndex(column)] = '—'
					} else if (axisIndex % (size * 2) == 0) {
						editableBoard[lineIndex(row)][characterIndex(column)] = '\\'
					} else {
						editableBoard[lineIndex(row)][characterIndex(column)] = '/'
					}

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
	val board = Board(4)
	board[0, 0] = Symbol.X
	board[2, 2] = Symbol.X
	board[3, 3] = Symbol.X
	board[1, 1] = Symbol.X

	println(board)
	println("Has O won: \n${board.checkForWin(Symbol.O)}")
	println("Has X won: \n${board.checkForWin(Symbol.X)}")
	println("Has EMPTY won: \n${board.checkForWin(Symbol.EMPTY)}")
}