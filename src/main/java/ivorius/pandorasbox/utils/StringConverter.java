package ivorius.pandorasbox.utils;

public class StringConverter {
    private StringConverter() {

    }
    public static String convertCamelCase(String id) {
        int firstUpper;
        final int len = id.length();
        char[] charsSeen = new char[5];
        int[] charLocations = new int[5];

        /* Now check if there are any characters that need to be changed. */
        for (int it = 0; it < 3; it++) {
            scan:
            {
                for (firstUpper = 0; firstUpper < len; ) {
                    char c = id.charAt(firstUpper);
                    if(c == charsSeen[0] && c == charLocations[0]) {
                        firstUpper++;
                        continue;
                    }
                    if(c == charsSeen[1] && c == charLocations[1]) {
                        firstUpper++;
                        continue;
                    }
                    if(c == charsSeen[2] && c == charLocations[2]) {
                        firstUpper++;
                        continue;
                    }
                    if (c != Character.toLowerCase(c)) {
                        String[] split = id.split(String.valueOf(c), 2);
                        id = split[0] + "_" + Character.toLowerCase(c) + split[1];
                        charsSeen[it] = c;
                        charLocations[it] = firstUpper;

                        break scan;
                    }
                    firstUpper++;
                }
            }
        }
        return id;
    }
}
