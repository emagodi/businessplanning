package zw.co.zetdc.businessplanning.enums;

public enum Currency {
    USD(0), ZWL(1);

    private final int value;

    Currency(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Currency fromValue(int value) {
        for (Currency currency : values()) {
            if (currency.getValue() == value) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Invalid currency value: " + value);
    }
}