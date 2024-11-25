package fm.mixer.gateway.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomIdentifierUtil {

    public static final int SIZE = 10;
    public static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Java implementation of <a href="https://github.com/ai/nanoid">NanoID</a>.
     * Probability of hitting same key is defined here: <a href="https://zelark.github.io/nano-id-cc/">NanoID calculator</a>.
     * In this implementation, probability of generating same key if each second we are generating 1 000 identifiers is
     * 1% for each ID in every 15 years.
     */
    public static String randomIdentifier() {
        final var mask = (2 << (Integer.SIZE - 1 - Integer.numberOfLeadingZeros(ALPHABET.length() - 1))) - 1;
        final var step = (int) Math.ceil(1.6 * mask * SIZE / ALPHABET.length());

        final var idBuilder = new StringBuilder(SIZE);
        final var bytes = new byte[step];

        while (true) {
            RANDOM.nextBytes(bytes);

            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;

                if (alphabetIndex < ALPHABET.length()) {
                    idBuilder.append(ALPHABET.charAt(alphabetIndex));
                    if (idBuilder.length() == SIZE) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
}
