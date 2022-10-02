enum class Symbol {
	EMPTY {
		override fun toString(): String = " "
	},

	X {
		override fun toString(): String = "X"
	},

	O {
		override fun toString(): String = "O"
	}
}

class Grid(size: Int) {
	val size = size

	// size - 1 was used a lot in iterations, so having an index variable instead makes code less prone to mistakes
	private val maxIndex = size - 1
	private val squares = Array(size) { Array(size) { Symbol.EMPTY } }

	// Three-dimensional array that stores all axes in which a player can win
	// Each axis is an array of blocks, and each block is a 2D vector
	// Where X is the length of the grid, the first X axes are vertical,
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

	// TODO surely there's a better way to write this
	fun checkForWin(symbol: Symbol): Boolean {
		// Check each column top to bottom
		for (column in 0..maxIndex) {
			if (this[column, 0] != symbol) {
				continue
			}
			for (row in 0..maxIndex) {
				if (this[column, row] != symbol) {
					break
				} else if (row == maxIndex) {
					return true
				}
			}
		}

		// Check each row left to right
		for (row in 0..maxIndex) {
			if (this[0, row] != symbol) {
				continue
			}
			for (column in 0..maxIndex) {
				if (this[column, row] != symbol) {
					break
				} else if (column == maxIndex) {
					return true
				}
			}
		}

		// Check top left to bottom right
		for (square in 0..maxIndex) {
			if (this[square, square] != symbol) {
				break
			} else if (square == maxIndex) {
				return true
			}
		}

		// Check top right to bottom left
		for (square in 0..maxIndex) {
			if (this[maxIndex - square, square] != symbol) {
				break
			} else if (square == maxIndex) {
				return true
			}
		}

		return false
	}

	override fun toString(): String {
		val gridString = StringBuilder()
		// Uses indices to avoid printing underline after last row
		for (rowIndex in squares.indices) {
			val row = squares[rowIndex]
			val rowString = StringBuilder()

			for (column in row) {
				rowString.append("| $column ")
			}
			rowString.append("|")

			gridString.append(rowString)

			// TODO check if there's a more efficient way to do this
			// No newlines or underlines after the last row
			if (rowIndex < maxIndex) {
				gridString.append('\n')
				var underline = rowString.toString()
				underline = underline
					.replace(' ', '-').replace('X', '-').replace('O', '-')
					.replace('|', ' ')
				gridString.append(underline).append('\n')

			}
		}
		return gridString.toString()
	}
}

fun main() {
	val grid = Grid(3)
	grid[0, 0] = Symbol.X
	grid[1, 1] = Symbol.O
	grid[2, 2] = Symbol.X
	grid[0, 2] = Symbol.O
	grid[2, 0] = Symbol.O
	println(grid)
	println("Has O won: ${grid.checkForWin(Symbol.O)}")
	println("Has X won: ${grid.checkForWin(Symbol.X)}")
	println("Has EMPTY won: ${grid.checkForWin(Symbol.EMPTY)}")

	for (axis in grid.winAxes) {
		for (block in axis) {
			print("(${block[0]}, ${block[1]}) ")
		}
		println()
	}
}