package com.gaia3d.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Vector;

@Slf4j
public class StringUtils {
    public static String doPadding4Bytes(String text) {
        return doPaddingBytes(text, 4);
    }

    public static String doPadding8Bytes(String text) {
        return doPaddingBytes(text, 8);
    }

    private static String doPaddingBytes(String text, int byteSize) {
        int length = text.length();
        StringBuilder featureTableText = new StringBuilder(text);
        int featureTableJsonOffset = length % byteSize;
        if (featureTableJsonOffset != 0) {
            int padding = 8 - featureTableJsonOffset;
            featureTableText.append(" ".repeat(Math.max(0, padding)));
        }
        return featureTableText.toString();
    }


    public static void splitString(String wordToSplit, String delimiter, Vector<String> resultSplittedStrings, boolean skipEmptyStrings) {
        String[] splitStrings = wordToSplit.split(delimiter);
        for (String word : splitStrings) {
            if (skipEmptyStrings) {
                if (!word.isEmpty()) {
                    resultSplittedStrings.add(word);
                }
            } else {
                resultSplittedStrings.add(word);
            }
        }
    }

    public static String getRawFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static String convertUTF8(String ascii) {
        if (ascii == null) {
            return "";
        }
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(ascii);
        String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        if (utf8EncodedString.isBlank()) {
            return "";
        }
        return utf8EncodedString;
    }
}
