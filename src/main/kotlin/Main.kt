package tictactoe

import kotlin.random.Random

fun String.asSymbol(): Symbol =
	when (this) {
		"X" -> Symbol.X
		"O" -> Symbol.O
		else -> throw Exception("Invalid symbol: $this")
	}

fun Symbol.opposite(): Symbol =
	when (this) {
		Symbol.X -> Symbol.O
		Symbol.O -> Symbol.X
		else -> throw Exception("Invalid symbol: $this")
	}

fun Int.asPosition() = Position(this % 3, this / 3)

fun computerMove(board: Board, computerSymbol: Symbol, random: Random): Position {
	return Position(0, 0)
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

	val userSymbol = userLetter.asSymbol()
	val computerSymbol = userSymbol.opposite()

	while (playing) {
		println(board)
		var userPosition: Int?
		do {
			println("Please pick a number between 1 and 9:")
			userPosition = readln().toIntOrNull()
		} while (userPosition == null || userPosition !in 1..9)
		userPosition--
		val userMove = userPosition.asPosition()
		board[userMove] = userSymbol

		if (board.hasWon(userSymbol)) {
			println(board.drawBoardWithWin(userSymbol))
			playing = false
		}

		board[computerMove(board, computerSymbol, Random)] = computerSymbol
	}
}
