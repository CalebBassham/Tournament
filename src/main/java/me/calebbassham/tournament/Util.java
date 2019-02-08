package me.calebbassham.tournament;

import java.util.concurrent.ThreadLocalRandom;

class Util {

    static double log2(int num) {
        return Math.log(num) / Math.log(2);
    }

    static int randInt(int num1, int num2) {
        int min = Math.min(num1, num2);
        int max = Math.max(num1, num2);

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    static <T> T randElement(T[] array) {
        if (array.length == 0) return null;
        return array[randInt(0, array.length - 1)];
    }

}
