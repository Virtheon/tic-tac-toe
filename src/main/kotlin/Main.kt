enum class Position {
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

val board = object {
	private fun emptyRow() = arrayOf(Position.EMPTY, Position.X, Position.O)
	val positions = arrayOf(emptyRow(), emptyRow(), emptyRow())

    override fun toString(): String {
        val boardString = StringBuilder()
        // Uses indices to avoid printing underline after last row
        for (lineIndex in positions.indices) {
            val line = positions[lineIndex]
            val rowString = StringBuilder()

            for (row in line) {
                rowString.append("| $row ")
            }
            rowString.append("|")

            boardString.append(rowString)

            // TODO check if there's a more efficient way to do this
            // No newlines or underlines after the last row
            if (lineIndex < (positions.size - 1)) {
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
	print(board)
}