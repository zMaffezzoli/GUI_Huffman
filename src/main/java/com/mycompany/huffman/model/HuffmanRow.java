package com.mycompany.huffman.model;

public class HuffmanRow {
    private String symbol;
    private int frequency;
    private String code;

    public HuffmanRow(String symbol, int frequency, String code) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.code = code;
    }

    public String getSymbol() { return symbol; }
    public int getFrequency() { return frequency; }
    public String getCode() { return code; }
}