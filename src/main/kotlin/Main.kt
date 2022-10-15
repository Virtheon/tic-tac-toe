package tictactoe

import kotlin.random.Random

fun toSymbol(symbol: String): Symbol =
	when (symbol) {
		"X" -> Symbol.X
		"O" -> Symbol.O
		else -> throw Exception("Invalid symbol: $symbol")
	}

fun oppositeOf(symbol: Symbol): Symbol =
	when (symbol) {
		Symbol.X -> Symbol.O
		Symbol.O -> Symbol.X
		else -> throw Exception("Invalid symbol: $symbol")
	}

fun toPosition(position: Int) = Position(position % 3, position / 3)

fun computerMove(board: Board, computerSymbol: Symbol, random: Random): Position {
	val enemySymbol = oppositeOf(computerSymbol)

	var tempBoard: Board
	for (column in 0 until 3) {
		for (row in 0 until 3) {
			tempBoard = board.clone()

			if (tempBoard[column, row] == Symbol.EMPTY) {
				tempBoard[column, row] = computerSymbol
				if (tempBoard.hasWon(computerSymbol)) {
					return Position(column, row)
				}

				tempBoard[column, row] = enemySymbol
				if (tempBoard.hasWon(enemySymbol)) {
					return Position(column, row)
				}
			}
		}
	}

	val diagonals = arrayOf(
		Position(0, 0), Position(2, 0),
		Position(0, 2), Position(2, 2)
	).shuffle(random)
	val center = Position(1, 1)
	val edges = arrayOf(
		Position(1, 0),
		Position(0, 1), Position(2, 1),
		Position(1, 2)
	).shuffle(random)

	tempBoard = board.clone()
	for (pos in diagonals) {
		if (tempBoard[pos] == Symbol.EMPTY) {
			return pos
		}
	}

	if (tempBoard[center] == Symbol.EMPTY) {
		return center
	}

	for (pos in edges) {
		if (tempBoard[pos] == Symbol.EMPTY) {
			return pos
		}
	}

	return Position(0, 0)
}

// TODO: position vs move
fun main() {
	val board = Board()
	var playing = true

	println("Please pick a letter (either X or O)")
	var userLetter = readln().uppercase()
	while (userLetter != "X" && userLetter != "O") {
		println("Please enter either X or O.")
		userLetter = readln().uppercase()
	}

	val userSymbol = toSymbol(userLetter)
	val computerSymbol = oppositeOf(userSymbol)

	while (playing) {
		println(board)
		var userPosition: Int?
		do {
			println("Please pick a number between 1 and 9:")
			userPosition = readln().toIntOrNull()
		} while (userPosition == null || userPosition !in 1..9)
		userPosition--
		val userMove = toPosition(userPosition)
		board[userMove] = userSymbol

		if (board.hasWon(userSymbol)) {
			println(board.drawBoardWithWin(userSymbol))
			playing = false
		}
		// TODO: show player's move first, then pause
		// TODO: say who won
		board[computerMove(board, computerSymbol, Random)] = computerSymbol
	}
}
