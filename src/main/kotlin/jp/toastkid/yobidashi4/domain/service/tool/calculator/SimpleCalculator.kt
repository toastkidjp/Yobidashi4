package jp.toastkid.yobidashi4.domain.service.tool.calculator

private data class Data(val rest: List<Char>, val value: Double)

class SimpleCalculator {

    operator fun invoke(expression: String): Double? {
        return try {
            parse(expression.toList())
        } catch (e: Exception) {
            null
        }
    }

    private fun parse(chars: List<Char>): Double {
        return getExpression(chars.filter { it != ' ' })
            .also { if (it.rest.isNotEmpty()) throw RuntimeException("Unexpected character: ${it.rest.first()}") }
            .value
    }

    private fun getExpression(chars: List<Char>): Data {
        var (rest, carry) = getTerm(chars)
        while (true) {
            when {
                rest.firstOrNull() == '+' -> rest = getTerm(rest.drop(1)).also { carry += it.value }.rest
                rest.firstOrNull() == '-' -> rest = getTerm(rest.drop(1)).also { carry -= it.value }.rest
                else                      -> return Data(rest, carry)
            }
        }
    }

    private fun getTerm(chars: List<Char>): Data {
        var (rest, carry) = getFactor(chars)
        while (true) {
            when {
                rest.firstOrNull() == '*' -> rest = getTerm(rest.drop(1)).also { carry *= it.value }.rest
                rest.firstOrNull() == '/' -> rest = getTerm(rest.drop(1)).also { carry /= it.value }.rest
                else                      -> return Data(rest, carry)
            }
        }
    }

    private fun getFactor(chars: List<Char>): Data {
        return when (val char = chars.firstOrNull()) {
            '+'              -> getFactor(chars.drop(1)).let { Data(it.rest, +it.value) }
            '-'              -> getFactor(chars.drop(1)).let { Data(it.rest, -it.value) }
            '('              -> getParenthesizedExpression(chars.drop(1))
            in '0'..'9', ',' -> getNumber(chars)
            else             -> throw RuntimeException("Unexpected character: $char")
        }
    }

    private fun getParenthesizedExpression(chars: List<Char>): Data {
        return getExpression(chars)
            .also { if (it.rest.firstOrNull() != ')') throw RuntimeException("Missing closing parenthesis") }
            .let { Data(it.rest.drop(1), it.value) }
    }

    private fun getNumber(chars: List<Char>): Data {
        val s = chars.takeWhile { it.isDigit() || it == '.' }.joinToString("")
        return Data(chars.drop(s.length), s.toDouble())
    }

}
