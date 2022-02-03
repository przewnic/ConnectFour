package connectfour

fun main() {
    display_msg("Connect Four")
    val playerFirst = get_name("First")
    val playerSecond = get_name("Second")
    val size = get_board_size()
    val gamesNumber = get_number_of_games()
    display_msg("$playerFirst VS $playerSecond")
    display_msg("${size[0]} X ${size[1]} board")
    if (gamesNumber == 1) display_msg("Single game")
    else display_msg("Total $gamesNumber games")
    val board = prepare_board(size, gamesNumber)
    play(playerFirst, playerSecond, board, size, gamesNumber)
}

/**
 * Prepare board and display information
 */
fun prepare_board(size: MutableList<Int>, gamesNumber: Int): MutableList<MutableList<String>> {
    val board = MutableList(size[0]){ MutableList(size[1]){" "} }
    if (gamesNumber > 1) display_msg("Game #1")
    draw_board(size, board)
    return board
}

/**
 * Display given message
 */
fun display_msg(msg: String){
    println(msg)
}

/**
 * Get player's name
 */
fun get_name(player: String) :String {
    display_msg("$player player's name:")
    return readLine()!!
}

/**
 * Get board size (rows, colums) from user
 */
fun get_board_size(rows: Int = 6, columns: Int = 7) :MutableList<Int> {
    display_msg("Set the board dimensions (Rows x Columns)")
    display_msg("Press Enter for default (6 x 7)")
    var readLine = readLine()!!
    val match = Regex("\\s*[0-9]*\\s*[xX]\\s*[0-9]*\\s*")
    val separator = Regex("\\s*[xX]\\s*")
    var readValues = mutableListOf<Int>()

    // Remove all whitespaces
    readLine = readLine.replace("\\s".toRegex(), "")
    // Return default if Enter pressed
    if (readLine == "") return mutableListOf(6,7)
    // Map read values to Int
    readValues = if (readLine.matches(match)) {
        try {
            separator.split(readLine).map{it.toInt()}.toMutableList()
        } catch (e: NumberFormatException) {
            display_msg("Invalid input")
            get_board_size()
        }
    } else {
        println("Invalid input")
        get_board_size()
    }
    // Check if size is in limits
    if (!check_size(readValues)) {
        readValues = get_board_size()
    }
    return readValues
}

/**
 * Get number of games from user
 */
fun get_number_of_games(): Int {
    display_msg("Do you want to play single or multiple games?")
    display_msg("For a single game, input 1 or press Enter")
    display_msg("Input a number of games:")
    var readNumber:Int = 1
    try {
        val read = readLine()!!
        if (read == "") {
            return 1
        }
        readNumber = read.toInt()
    } catch (e: NumberFormatException) {
        display_msg("Invalid input")
        get_number_of_games()
    }
    if (readNumber < 1) {
        display_msg("Invalid input")
        get_number_of_games()
    }
    return readNumber
}

/**
 * Check if the input board size within limits
 */
fun check_size(size: MutableList<Int>): Boolean {
    if (size[0] > 9 || size[0] < 5) {
        display_msg("Board rows should be from 5 to 9")
        return false
    }
    if (size[1] > 9 || size[1] < 5) {
        display_msg("Board columns should be from 5 to 9")
        return false
    }
    return true
}

/**
 * Draw board
 */
fun draw_board(size: MutableList<Int>, board: MutableList<MutableList<String>>){
    for (i in 1 .. size[1]){
        print(" $i")
    }
    println(" ")
    for (y in 0 until size[0]) {
        for (x in 0 until size[1]) {
            print("|"+board[y][x])
        }
        println("|")
    }
    repeat(2*size[1]+1) {
        print("=")
    }
    println("")
}

fun play(playerFirst: String, playerSecond: String, board: MutableList<MutableList<String>>, size: MutableList<Int>, gamesNumber: Int) {
    val first = "o"
    val second = "*"
    var scoreFirst = 0
    var scoreSecond = 0
    var turnGame = playerFirst
    for (i in 1..gamesNumber) {
        var turn = turnGame
        if (gamesNumber > 1) {
            if (i > 1) {
                display_msg("Game #$i")
                clear_board(board)
                draw_board(size, board)
            }
        }
        while (true) {
            display_msg("$turn's turn:")
            val read: String = readLine()!!
            val readColumn: Int
            if (check_end(read)) return
            try {
                readColumn = read.toInt()
            } catch (e: NumberFormatException) {
                display_msg("Incorrect column number")
                continue
            }
            if (check_wrong_size(readColumn, size)) continue
            if (check_full_column(readColumn, board)) continue

            for (i in size[0] - 1 downTo 0) {
                if (board[i][readColumn - 1] == " ") {
                    board[i][readColumn - 1] = if (turn == playerFirst) first
                    else second
                    break
                }
            }
            draw_board(size, board)
            if (check_win(board, size)) {
                if (turn == playerFirst) scoreFirst += 2
                else scoreSecond += 2
                display_msg("Player $turn won")
                break
            }
            if (check_full(board, size)) {
                scoreFirst += 1
                scoreSecond += 1
                display_msg("It is a draw")
                break
            }
            turn = change_turn(turn, playerFirst, playerSecond)
        }
        display_msg("Score")
        display_msg("$playerFirst: $scoreFirst $playerSecond: $scoreSecond")
        turnGame = change_turn(turnGame, playerFirst, playerSecond)
    }
    display_msg("Game over!")
}

/**
 * Clearing the board with " " for the next game
 */
fun clear_board(board: MutableList<MutableList<String>>) {
    for (i in board.indices) {
        for (k in board[i].indices) {
            board[i][k] = " "
        }
    }
}

/**
 * Check if user requested end of game
 */
fun check_end(read: String): Boolean {
    if (read == "end") {
        display_msg("Game over!")
        return true
    }
    return false
}

/**
 * Check if board input board size is within limits
 */
fun check_wrong_size(readColumn: Int, size: MutableList<Int>): Boolean {
    if (readColumn > size[1] || readColumn < 1) {
        display_msg("The column number is out of range (1 - ${size[1]})")
        return true
    }
    return false
}

/**
 * Check if chosen column is full of discs
 */
fun check_full_column(readColumn:Int, board: MutableList<MutableList<String>>): Boolean {
    if (board[0][readColumn-1] != " ") {
        display_msg("Column $readColumn is full")
        return true
    }
    return false
}

/**
 * Change turn
 */
fun change_turn(turn: String, playerFirst: String, playerSecond: String): String {
    return if (turn == playerFirst) playerSecond
    else playerFirst
}

/**
 * Check if the board is full
 */
fun check_full(board: MutableList<MutableList<String>>, size: MutableList<Int>): Boolean {
    for (row in board) {
        for (place in row) {
            if (place == " ")
                return false
        }
    }
    return true
}

/**
 * Check rows, columns and diagonals for 4 the same discs
 */
fun check_win(board: MutableList<MutableList<String>>, size: MutableList<Int>): Boolean {
    // Check rows for win
    for (row in board) {
        val checked = row.joinToString(separator = "")
        if (check_contains(checked)) return true
    }
    // Check colums for win
    for (column in 0 until size[1]) {
        var checked = ""
        for (i in 0 until size[0]) {
                checked += board[i][column]
        }
        if (check_contains(checked)) return true
    }
    // Check diagonal win
    for (column in 3 until size[1]) {
        for (i in size[0]-4 downTo 0) {
            var checked = ""
            for (k in 0..3) {
                checked += board[i+k][column-k]
            }
            if (check_contains(checked)) return true
        }
    }
    for (column in size[1] - 4 downTo 0) {
        for (i in size[0]-4 downTo 0) {
            var checked = ""
            for (k in 0..3) {
                checked += board[i+k][column+k]
            }
            if (check_contains(checked)) return true
        }
    }
    return false
}

/**
 * Check if given argument contains 4 the same  discs
 */
fun check_contains(checked: String): Boolean {
    if ("****" in checked || "oooo" in checked) return true
    return false
}
