package tictactoe

import java.util.Calendar
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

	val diagonals = listOf(
		Position(0, 0), Position(2, 0),
		Position(0, 2), Position(2, 2)
	).shuffled(random)
	val center = Position(1, 1)
	val edges = listOf(
		Position(1, 0),
		Position(0, 1), Position(2, 1),
		Position(1, 2)
	).shuffled(random)

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

	return throw Exception("Board full")
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

	val userSymbol = toSymbol(userLetter)
	val computerSymbol = oppositeOf(userSymbol)

	// TODO: add board string with numbers
	// TODO: improve sleep() length and timing
	while (playing) {
		println(board)
		println()

		var userMoveInt: Int?
		do {
			println("Please pick a number between 1 and 9:")
			userMoveInt = readln().toIntOrNull()
		} while (userMoveInt == null || userMoveInt !in 1..9)
		userMoveInt--

		Thread.sleep(1500)

		val userMove = toPosition(userMoveInt)
		if (board[userMove] == Symbol.EMPTY) {
			board[userMove] = userSymbol
		} else {
			println("Position already marked.")
			Thread.sleep(1500)
			println()
			continue
		}

		if (board.hasWon(userSymbol)) {
			println("You've won!")
			println(board.drawBoardWithWin(userSymbol))
			playing = false
		} else {
			println(board)
			Thread.sleep(1000)
			println()

			if (board.isFull) {
				println("The board is full! It's a tie.")
				playing = false
			} else {
				println("The computer's move:")
				board[computerMove(board, computerSymbol, Random(Calendar.getInstance().timeInMillis))] = computerSymbol
				if (board.hasWon(computerSymbol)) {
					Thread.sleep(1000)
					println("The computer has won!")
					println(board.drawBoardWithWin(computerSymbol))
					playing = false
				}
			}
		}
	}
}
