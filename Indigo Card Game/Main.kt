package indigo

class Deck{
    private val ranks = listOf("A","2","3","4","5","6","7","8","9","10","J","Q","K")
    private val suits = listOf("♦","♥","♠","♣")

    var deck = mutableListOf<String>()

    fun reset() {
        this.deck.clear()
        for (s in suits) for (r in ranks) deck.add(r + s)
    }
    fun shuffle() {
        this.deck.shuffle()
    }
}

class Score{
    var playerScore = 0
    var computerScore = 0
    var playerWonCards = 0
    var computerWonCards = 0

    fun reset() {
        this.playerScore = 0
        this.computerScore = 0
        this.playerWonCards = 0
        this.computerWonCards = 0
    }
}

fun computerChooses(hand: MutableList<String>, tableCards: MutableList<String>): Int{

    var choiceIndex = 0

    var topCard = if (tableCards.size == 0) ""
    else tableCards.last()

    var allCards = mutableListOf<Int>()
    var doubleSuit = mutableListOf<Int>()
    var doubleRank = mutableListOf<Int>()

    var candidatesAll = mutableListOf<Int>()
    var candidatesSuit = mutableListOf<Int>()
    var candidatesRank = mutableListOf<Int>()

    var candidatesDoubleSuit = mutableListOf<Int>()
    var candidatesDoubleRank = mutableListOf<Int>()

    // create the materials for choosing later
    for (inx in 0 until hand.size){

        // all cards
        allCards.add(inx)

        // doubleSuit
        if (hand.joinToString("").count {it == hand[inx].last() } > 1) {
            doubleSuit.add(inx)
        }

        // doubleRank
        if (hand.joinToString("").count {it == hand[inx].first() } > 1) {
            doubleRank.add(inx)
        }

        if (topCard != "") {

            // all candidates
            if ((hand[inx][0] == topCard[0]) ||
                (hand[inx].last() == topCard.last())
            )
                candidatesAll.add(inx)


            // all suits candidates
            if (hand[inx].last() == topCard.last()) {
                candidatesSuit.add(inx)

                // all double suits candidates
                if (hand.joinToString("").count { it == hand[inx].last() } > 1) {
                    candidatesDoubleSuit.add(inx)
                }
            }

            // all ranks candidates
            if (hand[inx].first() == topCard.first()) {
                candidatesRank.add(inx)

                // all double rank candidates
                if (hand.joinToString("").count { it == hand[inx].first() } > 1) {
                    candidatesDoubleRank.add(inx)
                }
            }
        }
    }


    // 1) table is empty OR no candidates
    if (candidatesAll.size == 0) {

        // 1a) double suit available?
        choiceIndex = if (doubleSuit.size > 0) doubleSuit.random()

        // 1b) double rank available?
        else if (doubleRank.size > 0) doubleRank.random()

        // 1c) nothing double
        else allCards.random()
    }
    // 2) cards on the table
    else {

        // 2a) double suit candidate available?
        choiceIndex = if (candidatesDoubleSuit.size > 0) candidatesDoubleSuit.random()

        // 2b) double rank candidate available?
        else if (candidatesDoubleRank.size > 0) candidatesDoubleRank.random()

        // 2c) nothing double
        else candidatesAll.random()
    }

    println(hand.joinToString(" "))
    return choiceIndex
}

fun main() {

    // create the deck and score
    var SharedDeck = Deck()
    var TableDeck = Deck()
    var PlayerDeck = Deck()
    var ComputerDeck = Deck()
    var score = Score()

    // start the game process
    println("Indigo Card Game")

    // player chooses who starts
    var playersTurn = ""
    while (playersTurn !in listOf("yes", "no")) {
        println("Play first?")
        playersTurn = readln().lowercase()
    }

    // who is the last winner? also implements for case no one ever wins cards
    var didPlayerWin = playersTurn == "yes"

    // prompt the deck building
    var suddenExit = ""
    var chooseCardToPlay = 0
    SharedDeck.reset()
    SharedDeck.shuffle()

    // create the initial cards
    repeat(4) { TableDeck.deck.add(SharedDeck.deck.removeLast()) }
    println("Initial cards on the table: ${TableDeck.deck.joinToString(" ")}")

    // start the game loop
    while (TableDeck.deck.size < 53) {

        // if cards 52 reached go to game over
        if (TableDeck.deck.size == 52) break

        // announce the situation on the table
        println()
        if (TableDeck.deck.size == 0) println("No cards on the table")
        else println("${TableDeck.deck.size} cards on the table, and the top card is ${TableDeck.deck.last()}")

        // distribute new cards if necessary
        if (ComputerDeck.deck.size == 0 && PlayerDeck.deck.size == 0) {
            if (SharedDeck.deck.size == 0) break
            repeat(6) { PlayerDeck.deck.add(SharedDeck.deck.removeLast()) }
            repeat(6) { ComputerDeck.deck.add(SharedDeck.deck.removeLast()) }
        }

        // player / computer makes his moves
        if (playersTurn == "yes") {
            println(
                "Cards in hand: ${
                    PlayerDeck.deck.mapIndexed { idx, value -> "${idx + 1})$value" }.joinToString(" ")
                }"
            )
            while (chooseCardToPlay !in (1..PlayerDeck.deck.size).toList()) {
                println("Choose a card to play (1-${PlayerDeck.deck.size}):")
                try {
                    suddenExit = readln()
                    if (suddenExit == "exit") break
                    chooseCardToPlay = suddenExit.toInt()
                } catch (e: Exception) {
                    continue
                }
            }
            if (suddenExit == "exit") break
            TableDeck.deck.add(PlayerDeck.deck.removeAt(chooseCardToPlay - 1))
            chooseCardToPlay = 0
        } else {

            TableDeck.deck.add(ComputerDeck.deck.removeAt(computerChooses(ComputerDeck.deck, TableDeck.deck)))
            println("Computer plays ${TableDeck.deck.last()}")
        }

        // check if computer or player wins the table cards
        if (TableDeck.deck.size > 1) {
            // does one player win the cards?
            if ((TableDeck.deck.last()[0] == TableDeck.deck[TableDeck.deck.size - 2][0]) ||
                (TableDeck.deck.last()[1] == TableDeck.deck[TableDeck.deck.size - 2][1]) ||
                (TableDeck.deck.last().last() == TableDeck.deck[TableDeck.deck.size - 2].last())
            ) {

                for (card in TableDeck.deck) {
                    if ((card[0] in listOf('A', 'J', 'K', 'Q')) || (card.length == 3)) {
                        if (playersTurn == "yes") score.playerScore++
                        else score.computerScore++
                    }
                    if (playersTurn == "yes") score.playerWonCards++
                    else score.computerWonCards++
                }

                if (playersTurn == "yes") println("Player wins cards")
                else println("Computer wins cards")
                didPlayerWin = playersTurn == "yes"
                println("Score: Player ${score.playerScore} - Computer ${score.computerScore}")
                println("Cards: Player ${score.playerWonCards} - Computer ${score.computerWonCards}")
                TableDeck.deck.clear()
            }
        }

        playersTurn = if (playersTurn == "yes") "no" else "yes"
    }

    // do the final score counting
    for (card in TableDeck.deck) {
        if ((card[0] in listOf('A', 'J', 'K', 'Q')) || (card.length == 3)) {
            if (didPlayerWin) score.playerScore++
            else score.computerScore++
        }
        if (didPlayerWin) score.playerWonCards++
        else score.computerWonCards++
    }
    if (score.computerWonCards > score.playerWonCards) score.computerScore += 3
    else score.playerScore += 3

    // announce the final score
    if (suddenExit != "exit") {
        println("Score: Player ${score.playerScore} - Computer ${score.computerScore}")
        println("Cards: Player ${score.playerWonCards} - Computer ${score.computerWonCards}")
    }
    println("Game Over")
}