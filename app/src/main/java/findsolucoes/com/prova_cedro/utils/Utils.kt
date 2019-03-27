package findsolucoes.com.prova_cedro.utils

class Utils{

    companion object {


        fun isLegalPassword(pass: String): Int {

            if (!pass.matches(".*[A-Z].*".toRegex())) return 1

            if (!pass.matches(".*[a-z].*".toRegex())) return 2

            if (!pass.matches(".*\\d.*".toRegex())) return 3

            if (!pass.matches(".*[~!.......].*".toRegex())) return 4

            if (pass.length <= 9) return 5

            return 0
        }
    }

}