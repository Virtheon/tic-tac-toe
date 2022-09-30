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
        val string = StringBuilder()
        for (line in positions) {
            for (row in line) {
                string.append("| $row ")
            }
            string.append("|\n")
        }
        return string.toString()
    }
}

fun main() {
	print(board)
}