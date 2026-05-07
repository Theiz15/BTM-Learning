package com.learning.btmlearning.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexPatternUtil {
    public static final Pattern NON_LATIN = Pattern.compile("[^a-z0-9-]");
    public static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
}
