package com.expensetracker.enums;

/**
 * валюта
 */
public enum Currency {
    /**
     * казахстанский тенге
     */
    KZT("₸"),
    
    /**
     * доллар США
     */
    USD("$"),
    
    /**
     * евро
     */
    EUR("€");
    
    private final String symbol;
    
    Currency(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
