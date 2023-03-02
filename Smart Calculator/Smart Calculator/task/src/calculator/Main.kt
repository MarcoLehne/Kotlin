package calculator

import java.lang.Exception
import java.math.BigInteger
import kotlin.math.pow

fun main() {

    // start the calculator app
    Calculator.run()
}

// the calculator Singleton, that holds all calculation functions and the function
// that parses the userInput into a for the calculator readable form
object Calculator{

    // this is the variable, that stores userInput variables
    private var variables = mutableMapOf<String, String>()
    private val validVariable = "[a-zA-Z]+".toRegex()

    // does basic formatting operations such as removing double whitespace
    // and adjacent operators
    private fun formatInput(userInput: String): String{

        return userInput

            // reduces adjacent operators to one operator only

            // combines every two adjacent minus symbols to one plus symbol
            .replace("--".toRegex(),"+")

            // transforms +- to -
            // resulting +- is a side effect of the method just above, that combines
            // -- into +
            .replace("\\+-".toRegex(), "-")

            // reduces all adjacent plus symbols to one plus symbol
            .replace("\\++".toRegex(),"+")

            // give all parenthesis a one space padding
            .replace("\\(".toRegex(), " ( ")
            .replace("\\)".toRegex(), " ) ")

            // 2 + n whitespace in user input gets reduced to 1 whitespace
            // preceding and trailing whitespace  in user input gets cut off entirely
            .replace(" {2,}".toRegex()," ").trim()
    }

    // creates the postfix notation that calculate will do
    // this also takes care of evaluating variables in incoming userInput
    private fun toPostfixNotation(userInput: String): MutableList<String>{

        // operator precedence
        var precedence = mapOf<String, Int>(
            "+" to 0, "-" to 0,
            "*" to 1, "/" to 1, "^" to 2)

        // this holds the operators
        var stack = mutableListOf<String>()
        
        // this is the result
        var postfixNotation = mutableListOf<String>()

        // loop through the input and apply rules for postfix building
        for (incoming in userInput.split(" ")) {

            // incoming is an operand
            if (incoming.matches("-?\\w+".toRegex())) {

                // if variable
                if (incoming.matches(validVariable)){
                    postfixNotation.add(variables[incoming]!!)
                } else {

                    // if integer
                    postfixNotation.add(incoming)
                }
            }

            // incoming is not an operand, i.e. incoming is an operator
            else {
                if (stack.isEmpty()) {
                    stack.add(incoming)
                }

                else if (stack.last() == "(" || incoming == "(") {
                    stack.add(incoming)
                }

                else if (incoming == ")") {
                    while (true) {
                        if (stack.last() != "(") {
                            postfixNotation.add(stack.removeLast())
                        } else {
                            stack.removeLast()
                            break
                        }
                    }
                }

                // this handles precedence hierarchy
                else {

                    while (stack.isNotEmpty()) {

                        /*                        if (userInput == "7 + 3 * ( ( 4 + 3 ) * 7 + 1 ) - 6 / ( 2 + 1 )") {
                                                    println()
                                                    println(incoming)
                                                    println(stack)
                                                    println(postfixNotation)
                                                }*/

                        // this skips any popping of stack and immediately goes to pushing incoming
                        // to stack (after the while loop)
                        if (stack.last() == "(") {
                            break
                        } else if (precedence[incoming]!! > precedence[stack.last()]!!) {
                            break
                        }
                        // if precedence of incoming operator is lower or equal to last of stack
                        else {
                            postfixNotation.add(stack.removeLast())
                        }
                    }
                    stack.add(incoming)
                }
            }
        }

        // add the remaining element in stack to postfixNotation
        while (stack.isNotEmpty()) {
            postfixNotation.add(stack.removeLast())
        }


        return postfixNotation
    }

    // this function carries the whole calculator logic
    private fun calculate(postfixedUserInput: MutableList<String>): Unit{

        var calculationStack = mutableListOf<BigInteger>()

        // these two variables are the numbers that will be popped from calculation stack
        var n1: BigInteger;
        var n2: BigInteger;

        // run calculation
        while (postfixedUserInput.isNotEmpty()) {

            // is element on top of incoming stack a number?
            if (postfixedUserInput.first().matches("-?[0-9]+".toRegex())) {
                calculationStack.add(postfixedUserInput.removeFirst().toBigInteger())

            } else {

                n1 = calculationStack.removeLast()
                n2 = calculationStack.removeLast()


                // incoming element on top is operator
                when (postfixedUserInput.removeFirst()) {
                    "+" -> calculationStack.add(n2 + n1)
                    "-" -> calculationStack.add(n2 - n1)
                    "/" -> calculationStack.add(n2 / n1)
                    "*" -> calculationStack.add(n2 * n1)
                    //"^" -> calculationStack.add(n2.toBigInteger().pow(n1.toBigInteger).toInt())
                    // to implement "by the power of" calulation, a check is needed
                    // to see if input is actually BigInteger. If yes, the computer should give
                    // an error and say: number too large to calculate
                }
            }
        }

        // this prints the result
        println(calculationStack.last())
    }

    // this is the only public function.
    // It runs the calculator
    fun run() {

        // define valid input regular expressions
        val validCommands = listOf("/help","/exit")
        //val validExpression = "([-|\\+]?\\w+\\s[+|-]\\s)*([-|\\+]?\\w+)".toRegex()
        val validIdentifier = "[a-zA-Z]+".toRegex()
        val validAssignment = "[a-zA-Z]+|-?\\d+".toRegex()

        // this is the variable, that will always hold the user input
        var userInput = ""

        // the app keeps running, until user types /exit
        while (true) {

            // update userInput at the beginning of each circle
            // gets formatted right away
            userInput = formatInput(readln())

            // handles empty input
            if (userInput.isEmpty()) {
                continue
            }

            // handles user input commands
            if (userInput.first() == '/') {

                if (userInput !in validCommands) { // --> is not valid command
                    println("Unknown command")
                    continue
                } else if (userInput == "/help") { // --> is valid command
                    println("The program calculates the sum of numbers")
                    println("-- will be transformed into +")
                    println("You can type as many numbers and operations, as you want.")
                    continue
                } else if (userInput == "/exit") { // --> is valid command

                    // final message when program stops running
                    println("Bye!")
                    break
                }
            }

            // handles variable declaration
            if (userInput.matches(".*=.*".toRegex())) {

                // unknown variable will be handled twice!! one time here in assignment
                // one time in calculation

                val identifier = userInput.substring(0, userInput.indexOf('=')).trim()
                val assignment = userInput.substring(userInput.indexOf('=') + 1).trim()

                // is the variable declaration valid?
                if (! identifier.matches(validIdentifier)) {
                    println("Invalid identifier")
                    continue
                } else if (! assignment.matches(validAssignment)) {
                    println("Invalid assignment")
                    continue
                } else if (assignment.matches(validVariable) && assignment !in variables.keys) {
                    println("Unknown variable")
                    continue
                } else if (assignment in variables.keys) {
                    variables[identifier] = variables[assignment]!!  // --> known variable in assignment
                } else {
                    variables[identifier] = assignment // --> number in assignment
                }
            }

            // handles user input expressions
            if (userInput.isNotEmpty() && userInput.first() != '/' && ! userInput.matches(".*=.*".toRegex())){

                // unknown variable
                if (userInput.split(" ").any {it.matches(validVariable) && it !in variables.keys}) {
                    println("Unknown variable")

                } else {

                    // everything is fine and calculation can start
                    try {
                        this.calculate(toPostfixNotation(userInput))
                    } catch (e: Exception) {
                        println("Invalid expression")
                    }
                }
            }
        }
    }
}

