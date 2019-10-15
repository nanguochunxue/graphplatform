package com.haizhi.graph.server.es.client.rest.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/11/9.
 */
public class ErrorUtils {

    // find error inside ElasticsearchParseException
    private static final Pattern XCONTENT_PAYLOAD = Pattern.compile("Elastic[s|S]earchParseException.+: \\[(.+)\\]]");
    private static final Pattern OFFSET = Pattern.compile("offset=(\\d+)");
    private static final Pattern LENGTH = Pattern.compile("length=(\\d+)");
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static String extractInvalidXContent(String errorMessage) {
        if (!hasText(errorMessage)) {
            return null;
        }

        String group = findMatch(XCONTENT_PAYLOAD.matcher(errorMessage));
        if (!hasText(group)) {
            return null;
        }

        String match = findMatch(OFFSET.matcher(errorMessage));
        int offset = (hasText(match) ? Integer.valueOf(match) : 0);
        match = findMatch(LENGTH.matcher(errorMessage));
        int length = (hasText(match) ? Integer.valueOf(match) : 0);


        List<Byte> bytes = new ArrayList<>();
        // parse the collection into numbers and back to a String
        try {
            for (String byteValue : tokenize(group, ",")) {
                bytes.add(Byte.parseByte(byteValue));
            }
            if (length == 0) {
                length = bytes.size();
            }

            byte[] primitives = new byte[length];
            for (int index = 0; index < length; index++) {
                primitives[index] = bytes.get(index + offset).byteValue();
            }
            return new String(primitives, UTF_8);
        } catch (Exception ex) {
            // can't convert back the byte array - give up
            return null;
        }
    }

    public static boolean hasText(CharSequence sequence) {
        if (!hasLength(sequence)) {
            return false;
        }
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(sequence.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLength(CharSequence sequence) {
        return (sequence != null && sequence.length() > 0);
    }

    public static List<String> tokenize(String string, String delimiters) {
        return tokenize(string, delimiters, true, true);
    }

    public static List<String> tokenize(String string, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (!hasText(string)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(string, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private static String findMatch(Matcher matcher) {
        return (matcher.find() ? matcher.group(1) : null);
    }
}
