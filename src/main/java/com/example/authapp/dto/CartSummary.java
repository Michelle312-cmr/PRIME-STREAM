package com.example.authapp.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public record CartSummary(
        List<CartLine> lines,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal shipping,
        BigDecimal discount,
        BigDecimal total,
        int itemCount,
        String coupon
) {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
    
    public String formatPrice(BigDecimal price) {
        if (price == null) return "0.00";
        return FORMATTER.format(price);
    }
    
    public String subtotalFormatted() {
        return formatPrice(subtotal);
    }
    
    public String taxFormatted() {
        return formatPrice(tax);
    }
    
    public String shippingFormatted() {
        return formatPrice(shipping);
    }
    
    public String discountFormatted() {
        return formatPrice(discount);
    }
    
    public String totalFormatted() {
        return formatPrice(total);
    }
    
    public String getDiscountLabel() {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        }
        return formatPrice(discount);
    }
}
