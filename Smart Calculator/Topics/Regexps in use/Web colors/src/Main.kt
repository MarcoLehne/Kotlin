fun main() {
    val text = readLine()!!
    val regexColors = "#[0-9a-fA-F]{6}\\b".toRegex()
    for (line in regexColors.findAll(text)) {
        println(line.value)
    }
}
