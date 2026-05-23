package com.example.authapp.dto;

import com.example.authapp.model.Product;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public record CartLine(Product product, int quantity, BigDecimal lineTotal) {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
    
    public String formatPrice(BigDecimal price) {
        if (price == null) return "0.00";
        return FORMATTER.format(price);
    }
    
    public String lineTotalFormatted() {
        return formatPrice(lineTotal);
    }
    
    public String productPriceFormatted() {
        return formatPrice(product.getPrice());
    }
}
