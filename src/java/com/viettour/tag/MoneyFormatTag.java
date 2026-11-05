package com.viettour.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormatTag extends SimpleTagSupport {
    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void doTag() throws JspException, IOException {
        BigDecimal number = BigDecimal.ZERO;
        if (value instanceof BigDecimal) {
            number = (BigDecimal) value;
        } else if (value instanceof Number) {
            number = BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value != null && !value.toString().trim().isEmpty()) {
            try {
                number = new BigDecimal(value.toString().trim());
            } catch (NumberFormatException ignored) {
            }
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        getJspContext().getOut().write(formatter.format(number) + " VND");
    }
}