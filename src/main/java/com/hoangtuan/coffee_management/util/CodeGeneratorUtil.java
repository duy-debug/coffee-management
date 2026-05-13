package com.hoangtuan.coffee_management.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CodeGeneratorUtil {

    private CodeGeneratorUtil() {
    }

    public static String generateNextCode(String prefix, Collection<String> existingCodes, int digits) {
        int max = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(prefix) + "(\\d+)");

        for (String code : existingCodes) {
            if (code == null) {
                continue;
            }
            Matcher matcher = pattern.matcher(code.trim());
            if (matcher.matches()) {
                try {
                    max = Math.max(max, Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException ignored) {
                    // Bỏ qua mã không hợp lệ
                }
            }
        }

        int next = max + 1;
        return prefix + String.format("%0" + digits + "d", next);
    }
}
