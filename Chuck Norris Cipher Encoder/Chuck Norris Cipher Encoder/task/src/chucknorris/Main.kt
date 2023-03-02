package chucknorris

fun encode(input: String): String{
    var r = ""

    for (i in input){
        var l = Integer.toBinaryString(i.code)
        if(l.length == 6) l = "0" + l
        r += l
    }

    var curr = r[0]
    if(curr == '0') print("00 ")
    else print("0 ")
    for(i in r){
        if(i == curr) print('0')
        else{
            print(" ")
            if(i == '1') print("0 ")
            if(i == '0') print("00 ")
            print('0')
            curr = i
        }
    }
    return r
}

fun decode(input: String): String{

    var longString = ""
    var isOne = true
    var spaceCounter = 2
    var wait = false

    // this deciphers the string into long string
    for (i in input.indices) {

        if(input[i] !in listOf('0',' ')) throw Exception()

        if(spaceCounter == 2){
            if (input[i + 1] == '0' && input[i + 2] == '0') throw Exception()
            isOne = input[i + 1] != '0'
            wait = true
            spaceCounter = 0
        }

        if(input[i] == ' ') spaceCounter += 1
        if((spaceCounter < 2) && (input[i] != ' ') && !wait) longString += if(isOne) "1" else "0"
        if(wait && input[i] == ' ') wait = false
    }

    // this splits up the long string and converts to ascii
    var asciiString = ""
    for (i in longString){
        asciiString += i
        if(asciiString.length == 7){
            asciiString = "0$asciiString"
            print(Integer.parseInt(asciiString, 2).toChar())
            asciiString = ""
        }
    }
    if (asciiString.length / 7 * 7 != asciiString.length) throw Exception()

    return asciiString
}

// all normal strings are valid
// condition only needed for encoded strings

fun main() {

    var inputOperation = "encode"
    var inputString = ""

    while(inputOperation != "exit") {
        println("Please input operation (encode/decode/exit):")
        inputOperation = readln()

        if (inputOperation !in listOf("encode", "decode", "exit")) println("There is no '${inputOperation}' operation")
        else {
            if (inputOperation == "exit") println("Bye!")
            else {
                if (inputOperation == "encode"){
                    println("Input string:")
                    inputString = readln()
                    println("Encoded string:")
                    encode(inputString)
                    print("\n")
                }
                else {
                    println("Input encoded string:")
                    inputString = readln()
                    try {
                        decode(inputString)
                        println("${inputOperation.capitalize()}d string:")
                        println(decode(inputString))
                    } catch (e: Exception) {
                        println("${inputOperation.capitalize()}d string is not valid.")
                    }

                }
            }
        }
        println()
    }
}