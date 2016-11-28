package com.solunes.endeapp.control_code;

/**
 * El algoritmo de Base 64, se trata sencillamente de pasar un número en base 10 a base 64, manteniendo
 * una relación de equivalencia entre número y carácter,
 * por ejemplo A=10, B=11, ../=63
 */
public class Base64SIN {
    public static String convert(int value){
        String[] dictionary = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d",
                "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                "y", "z", "+", "/" };
        int quotient = 1;
        int remainder;
        String word = "";
        while (quotient > 0)
        {
            quotient = value / 64;
            remainder = value % 64;
            word = dictionary[remainder] + word;
            value = quotient;
        }
        return word;
    }
}
