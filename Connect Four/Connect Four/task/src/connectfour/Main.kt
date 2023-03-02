package connectfour

fun checkRegex(size: String): Boolean {
    val regex = " *?[0-9]+ *?x *?[0-9]+ *?"
    return Regex(regex, RegexOption.IGNORE_CASE).matches(size)
}
fun drawBoard(sizeRows: Int, sizeColumns: Int): String {

    var returnString = ""
    returnString += " ${(1..sizeColumns).toList().joinToString(" ")} \n"
    returnString += "${"| ".repeat(sizeColumns )}|\n".repeat(sizeRows)
    returnString += "${"=".repeat(sizeColumns * 2)}="

    return returnString
}

fun streak(singleList: MutableList<Char>): Boolean{
    for (i in 0 until singleList.size - 3){
        if (singleList[i] == ' ') continue
        if (singleList[i + 1] == singleList[i] &&
            singleList[i + 2] == singleList[i] &&
            singleList[i + 3] == singleList[i])
            return true
    }
    return false
}

fun rowVictory(storage: MutableList<MutableList<Char>>): Boolean{
    for (singleList in storage){
        if (streak(singleList)) return true
    }
    return false
}

fun columnVictory(storage: MutableList<MutableList<Char>>): Boolean{
    var transposedStorage = MutableList(storage[0].size) {MutableList(storage.size) {' '} }
    for (r in 0 until storage.size) {
        for (c in 0 until storage[0].size) {
            transposedStorage[c][r] = storage[r][c]
        }
    }
    for (singleList in transposedStorage){
        if (streak(singleList)) return true
    }
    return false
}

fun diagonalVictory(storage: MutableList<MutableList<Char>>): Boolean {

    val rows = storage.size
    val cols = storage[0].size

    // left to right
    for (r in 0 until rows - 3) {
        for (c in 0 until cols) {

            if (storage[r][c] != ' ') {
                if (c - 3 > -1)
                    if (storage[r][c] == storage[r + 1][c - 1] &&
                        storage[r][c] == storage[r + 2][c - 2] &&
                        storage[r][c] == storage[r + 3][c - 3])
                        return true
                if (c + 3 < cols)
                    if (storage[r][c] == storage[r + 1][c + 1] &&
                        storage[r][c] == storage[r + 2][c + 2] &&
                        storage[r][c] == storage[r + 3][c + 3])
                        return true
            }
        }
    }
    return false
}

fun draw(storage: MutableList<MutableList<Char>>): Boolean {
    for (list in storage) {
        if (list.indexOf(' ') != -1) return false
    }
    return true
}

fun playGame(sizeRows: Int, sizeColumns: Int, p1: String, p2:String, cp:Int): List<Int> {

    val storage = MutableList(sizeColumns) { MutableList(sizeRows) { ' ' } }

    var turn = " "
    var currPlayer = if (cp == 0) p1 else p2
    var scoreP1 = 0
    var scoreP2 = 0

    while(turn != "end"){

        println("$currPlayer's turn:")
        turn = readln()
        if (turn != "end") {

            // here needs to go game fail logic
            try {
                if (turn.toInt() !in 1..sizeColumns) println("The column number is out of range (1 - $sizeColumns)")
                else if (storage[turn.toInt() - 1].lastIndexOf(' ') < 0) println("Column $turn is full")
                else {
                    storage[turn.toInt() - 1][storage[turn.toInt() - 1].lastIndexOf(' ')] = if (currPlayer == p1) 'o' else '*'
                    print(" ${(1..sizeColumns).toList().joinToString(" ")} \n")
                    for (r in 0 until sizeRows) {
                        for (s in storage) {
                            print("|${s[r]}")
                        }
                        print("|\n")
                    }
                    print("${"=".repeat(sizeColumns * 2)}=\n")

                    // this is where the winning conditions are being tested!
                    if (rowVictory(storage) || columnVictory(storage) || diagonalVictory(storage)) {
                        println("Player $currPlayer won")
                        if (currPlayer == p1) scoreP1 += 2
                        else scoreP2 += 2
                        turn = "end"
                    }

                    if (draw(storage)){
                        println("It is a draw")
                        scoreP1++
                        scoreP2++
                        turn = "end"
                    }


                    currPlayer = if (currPlayer == p1) p2
                    else p1
                }
            }
            catch (e: Exception) { println("Incorrect column number") }
        }
    }
    return listOf(scoreP1,scoreP2)
}

fun main() {
    println("Connect Four")

    println("First player's name:")
    val player1 = readln()
    println("Second player's name:")
    val player2 = readln()

    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
    var size = readln().replace("\t"," ").ifEmpty { "6 X 7" }
    var sizeRows = 0
    var sizeColumns = 0
    if (checkRegex(size)){
        sizeRows = size.split(Regex(" ?x ?", RegexOption.IGNORE_CASE))[0].trim().toInt()
        sizeColumns = size.split(Regex(" ?x ?", RegexOption.IGNORE_CASE))[1].trim().toInt()
    }
    while (! checkRegex(size)|| ! (sizeColumns in 5..9 && sizeRows in 5..9)){
        if (! checkRegex(size)) println("Invalid input")
        else {
            if(sizeColumns !in 5..9) println("Board columns should be from 5 to 9")
            else println("Board rows should be from 5 to 9")
        }
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        size = readln().replace("\t"," ").ifEmpty { "6 X 7" }
        if (checkRegex(size)){
            sizeRows = size.split(Regex(" ?x ?", RegexOption.IGNORE_CASE))[0].trim().toInt()
            sizeColumns = size.split(Regex(" ?x ?", RegexOption.IGNORE_CASE))[1].trim().toInt()
        }
    }
    println("Do you want to play single or multiple games?")
    println("For a single game, input 1 or press Enter")
    println("Input a number of games:")
    var amount = readln().replace("\t"," ").ifEmpty { "1" }
    while (! amount.matches("[1-9]+".toRegex())){
        println("Invalid input")
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        amount = readln().replace("\t"," ").ifEmpty { "1" }
    }

    println("$player1 VS $player2")
    println("$sizeRows X $sizeColumns board")
    if (amount == "1") {
        println("Single game")
        println(drawBoard(sizeRows,sizeColumns))
    }
    else println("Total $amount games")

    var gameNr = 0
    var whoBegins = 0
    var scoreP1 = 0
    var scoreP2 = 0
    var tempScore = listOf(0,0)
    while (gameNr.toString() < amount) {
        if (amount != "1") {
            println("Game #${gameNr + 1}")
            println(drawBoard(sizeRows,sizeColumns))
        }
        tempScore = playGame(sizeRows,sizeColumns,player1,player2,whoBegins)    // this needs who begins as well
        scoreP1 += tempScore[0]
        scoreP2 += tempScore[1]
        if (amount != "1") {
            println("Score")
            println("$player1: $scoreP1 $player2: $scoreP2")
        }
        gameNr ++
        whoBegins = if (whoBegins == 0) 1 else 0
    }
    print("Game over!")

}