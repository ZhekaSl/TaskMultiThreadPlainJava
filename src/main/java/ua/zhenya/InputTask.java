package ua.zhenya;

class InputTask {
    final int index;
    final int number;

    InputTask(int index, int number) {
        this.index = index;
        this.number = number;
    }

    static final InputTask POISON_PILL = new InputTask(-1, -1);
}