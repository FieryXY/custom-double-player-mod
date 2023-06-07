package net.fabricmc.example;

public enum DoubleBodyPlayerType {
    NORMAL(0),
    ONE(1),
    TWO(2);

    private int index;
    DoubleBodyPlayerType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static DoubleBodyPlayerType getTypeByIndex(int index) {
        switch(index) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            default:
                return NORMAL;
        }
    }
}