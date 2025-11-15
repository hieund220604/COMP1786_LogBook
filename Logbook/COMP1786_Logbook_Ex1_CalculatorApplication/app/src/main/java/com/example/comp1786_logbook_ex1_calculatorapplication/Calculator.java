package com.example.comp1786_logbook_ex1_calculatorapplication;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pure Java calculator engine: parse/sanitize inputs, compute basic binary ops, and format for display.
 */
public final class Calculator {

    private Calculator() {}

    public static final class Result {
        public final boolean ok;
        public final BigDecimal value;
        public final String error;

        private Result(boolean ok, BigDecimal value, String error) {
            this.ok = ok; this.value = value; this.error = error;
        }
        public static Result ok(BigDecimal v) { return new Result(true, v, null); }
        public static Result error(String msg) { return new Result(false, null, msg); }
    }

    public static BigDecimal parse(String s) throws NumberFormatException {
        if (s == null) throw new NumberFormatException("null");
        String normalized = sanitizeNumber(s);
        return new BigDecimal(normalized);
    }

    /** Sanitize user input: trim spaces, handle leading/trailing dots, collapse leading zeros. */
    public static String sanitizeNumber(String s) {
        if (s == null) return "0";
        String t = s.trim();
        if (t.isEmpty()) return "0";
        if (t.endsWith(".")) t = t.substring(0, t.length() - 1);
        if (t.startsWith(".")) t = "0" + t;
        boolean neg = t.startsWith("-");
        String core = neg ? t.substring(1) : t;
        if (core.contains(".")) {
            int idx = core.indexOf('.');
            String intPart = core.substring(0, idx);
            String fracPart = core.substring(idx + 1);
            String intNorm = intPart.replaceFirst("^0+(?!$)", "");
            if (intNorm.isEmpty()) intNorm = "0";
            core = intNorm + "." + fracPart;
        } else {
            core = core.replaceFirst("^0+(?!$)", "");
            if (core.isEmpty()) core = "0";
        }
        return neg ? ("-" + core) : core;
    }

    public static Result compute(String aStr, String bStr, String op) {
        BigDecimal a, b;
        try {
            a = (aStr == null) ? BigDecimal.ZERO : parse(aStr);
        } catch (NumberFormatException ex) {
            return Result.error("invalid_a");
        }
        try {
            b = (bStr == null) ? null : parse(bStr);
        } catch (NumberFormatException ex) {
            return Result.error("invalid_b");
        }
        if (b == null) return Result.error("missing_b");

        BigDecimal res;
        switch (op) {
            case "+": res = a.add(b); break;
            case "−": case "-": res = a.subtract(b); break;
            case "×": case "x": case "X": res = a.multiply(b); break;
            case "÷": case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) return Result.error("divide_by_zero");
                res = a.divide(b, 10, RoundingMode.HALF_UP);
                break;
            default: return Result.error("unsupported_op");
        }
        res = res.stripTrailingZeros();
        return Result.ok(res);
    }

    /** Format BigDecimal: add grouping separators for moderate sizes; otherwise engineering string. */
    public static String format(BigDecimal bd) {
        if (bd == null) return "0";
        BigDecimal v = bd.stripTrailingZeros();
        String plain = v.toPlainString();
        if (plain.contains("E") || plain.contains("e")) {
            return v.toEngineeringString();
        }
        boolean neg = plain.startsWith("-");
        String core = neg ? plain.substring(1) : plain;
        String intPart = core;
        String fracPart = null;
        if (core.contains(".")) {
            int idx = core.indexOf('.');
            intPart = core.substring(0, idx);
            fracPart = core.substring(idx + 1);
        }
        if (intPart.length() > 15) {
            return v.toEngineeringString();
        }
        StringBuilder grouped = new StringBuilder();
        int len = intPart.length();
        for (int i = 0; i < len; i++) {
            grouped.append(intPart.charAt(i));
            int posFromEnd = len - i - 1;
            if (posFromEnd > 0 && posFromEnd % 3 == 0) grouped.append(',');
        }
        StringBuilder out = new StringBuilder();
        if (neg) out.append('-');
        out.append(grouped);
        if (fracPart != null && !fracPart.isEmpty()) {
            out.append('.').append(fracPart);
        }
        return out.toString();
    }

    /** Format a numeric string; if parsing fails, return original string. */
    public static String formatStringSafe(String s) {
        try {
            BigDecimal bd = parse(s);
            return format(bd);
        } catch (NumberFormatException ex) {
            return s == null ? "" : s;
        }
    }
}
