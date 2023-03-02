fun main() {
    val list = mutableListOf<Int>()
    val length = readln().toInt()
    repeat(length) {
        list += readln().toInt()
    }
    var howMany = 0
    for (i in 0 until length - 2) {
        if (list[i] + 1 == list[i + 1] && list[i] + 2 == list[i + 2]) howMany += 1
    }
    print(howMany)
}
