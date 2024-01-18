package kodong.spotimate.authentication.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomStringGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> CHARACTERS.charAt(random.nextInt(CHARACTERS.length())))
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
