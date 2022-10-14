package tictactoe

fun asSymbol(symbol: String): Symbol =
	when (symbol) {
		"X" -> Symbol.X
		"O" -> Symbol.O
		else -> throw Exception("Invalid symbol: $symbol")
	}

fun oppositeSymbol(symbol: Symbol): Symbol =
	when (symbol) {
		Symbol.X -> Symbol.O
		Symbol.O -> Symbol.X
		else -> throw Exception("Invalid symbol: $symbol")
	}

fun asRowAndColumn(position: Int) = Pair(position % 3, position / 3)

fun computerMove(computerSymbol: Symbol): Pair<Int, Int> {
	return Pair(0, 0)
}

fun main() {
	val board = Board()
	var playing = true

	println("Please pick a letter (either X or O)")
	var userLetter = readln().uppercase()
	while (userLetter != "X" && userLetter != "O") {
		println("Please enter either X or O.")
		userLetter = readln().uppercase()
	}

	val userSymbol = asSymbol(userLetter)
	val computerSymbol = oppositeSymbol(userSymbol)

	while (playing) {
		println(board)
		var userPosition: Int?
		do {
			println("Please pick a number between 1 and 9:")
			userPosition = readln().toIntOrNull()
		} while (userPosition == null || userPosition !in 1..9)
		userPosition--
		val userMove = asRowAndColumn(userPosition)
		board[userMove] = userSymbol

		if (board.hasWon(userSymbol)) {
			println(board.drawBoardWithWin(userSymbol))
			playing = false
		}

		board[computerMove(computerSymbol)] = computerSymbol
	}
}
