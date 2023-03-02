fun getCamelStyleString(inputString: String): String {

    var splitted = inputString.split("_")
    var capitalize = splitted.reduce {acc,i -> acc + i.replaceFirstChar { it.uppercase() }}
    
    return capitalize
    }

