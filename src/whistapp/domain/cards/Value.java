package whistapp.domain.cards;

public enum Value {

    ACE(14),
    KING(13),
    QUEEN(12),
    JACK(11),
    TEN(10),
    NINE(9),
    EIGHT(8),
    SEVEN(7),
    SIX(6),
    FIVE(5),
    FOUR(4),
    THREE(3),
    TWO(2);

    private int numericValue;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    Value(int numericValue) {
        this.numericValue = numericValue;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter for the numeric value.
     */
    public int getNumericValue() {
        return numericValue;
    }

    @Override
    public String toString() {
        String name = name().toLowerCase();
        String first = String.valueOf(name.charAt(0)).toUpperCase();
        return first + name.substring(1);
    }

}
