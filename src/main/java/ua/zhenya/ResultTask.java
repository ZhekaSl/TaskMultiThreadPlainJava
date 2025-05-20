package ua.zhenya;

import java.math.BigInteger;

class ResultTask {
    final int index;
    final int number;
    final BigInteger factorial;

    ResultTask(int index, int number, BigInteger factorial) {
        this.index = index;
        this.number = number;
        this.factorial = factorial;
    }

    static final ResultTask POISON_PILL = new ResultTask(-1, -1, BigInteger.ZERO);
}