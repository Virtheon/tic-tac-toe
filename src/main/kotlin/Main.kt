package tictactoe

fun main() {
	val board = Board(3)
	board[0, 0] = Symbol.X
	board[2, 2] = Symbol.X
	board[1, 1] = Symbol.X

	println(board)
	println("Has O won: \n${board.drawBoardWithWin(Symbol.O)}")
	println("Has X won: \n${board.drawBoardWithWin(Symbol.X)}")
	println("Has EMPTY won: \n${board.drawBoardWithWin(Symbol.EMPTY)}")

	val b2 = Board(board)
	b2[0, 0] = Symbol.O
	println("First board:\n$board")
	println("Second board\n$b2")
}