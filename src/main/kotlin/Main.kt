enum class Mark {
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

class Board {
	private fun emptyRow() = arrayOf(Mark.EMPTY, Mark.X, Mark.O)
	private val positions = arrayOf(emptyRow(), emptyRow(), emptyRow())

	operator fun get(index: Int) : Mark {
        val row: Int = index / 3
        val column: Int = index % 3
        return positions[row][column]
    }

    operator fun set(index: Int, value: Mark) {
        val row: Int = index / 3
        val column: Int = index % 3
		println("$index  $row  $column")
        positions[row][column] = value
    }

	override fun toString(): String {
		val boardString = StringBuilder()
		// Uses indices to avoid printing underline after last row
		for (rowIndex in positions.indices) {
			val row = positions[rowIndex]
			val rowString = StringBuilder()

			for (column in row) {
				rowString.append("| $column ")
			}
			rowString.append("|")

			boardString.append(rowString)

			// TODO check if there's a more efficient way to do this
			// No newlines or underlines after the last row
			if (rowIndex < (positions.size - 1)) {
				boardString.append('\n')
				var underline = rowString.toString()
				underline = underline
					.replace(' ', '-').replace('X', '-').replace('O', '-')
					.replace('|', ' ')
				boardString.append(underline).append('\n')

			}
		}
		return boardString.toString()
	}
}

fun main() {
	val board = Board()
	println(board)
	println()
	board[7] = Mark.O
	println(board)
}