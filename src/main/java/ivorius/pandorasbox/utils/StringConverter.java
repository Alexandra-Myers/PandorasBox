package ivorius.pandorasbox.utils;

import java.util.Objects;

public class StringConverter {
    private StringConverter() {

    }
    public static String convertCamelCase(String id) {
        int firstUpper;
        final int len = id.length();

        /* Now check if there are any characters that need to be changed. */
        while (!Objects.equals(id, id.toLowerCase())) {
            scan:
            {
                for (firstUpper = 0; firstUpper < len; firstUpper++) {
                    int c = id.codePointAt(firstUpper);
                    if (c != Character.toLowerCase(c)) {
                        String[] split = id.split(Character.toString(c), 2);
                        id = split[0] + "_" + Character.toLowerCase(c) + split[1];
                        break scan;
                    }
                }
            }
        }
        return id;
    }
}
