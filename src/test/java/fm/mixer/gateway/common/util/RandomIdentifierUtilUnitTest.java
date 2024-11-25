package fm.mixer.gateway.common.util;

import fm.mixer.gateway.test.UnitTest;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RandomIdentifierUtilUnitTest {

    @Test
    void shouldGenerateValidRandomIdentifierList() {
        // Given
        final var numberOfIterations = 10000;
        final var identifierList = new ArrayList<String>();

        // When
        for (int i = 0; i < numberOfIterations; i++) {
            identifierList.add(RandomIdentifierUtil.randomIdentifier());
        }

        // Then
        identifierList.forEach(identifier -> {
            assertThat(identifier).hasSize(RandomIdentifierUtil.SIZE);
            assertThat(identifier).isAlphanumeric();
        });
        // check for duplicates
        assertThat(identifierList).hasSize(new HashSet<>(identifierList).size());
    }

    @Test
    void shouldGenerateRandomlyDistributedIdentifierList() {
        // Given
        final var numberOfIterations = 10000;
        final var characterCounter = new HashMap<Integer, Integer>();

        // When
        for (int i = 0; i < numberOfIterations; i++) {
            RandomIdentifierUtil.randomIdentifier().chars().forEach(character -> {
                if (characterCounter.containsKey(character)) {
                    characterCounter.put(character, characterCounter.get(character) + 1);
                }
                else {
                    characterCounter.put(character, 1);
                }
            });
        }

        // Then
        characterCounter.values().forEach(count -> {
            final var distribution = (count * RandomIdentifierUtil.ALPHABET.length() / (double) (numberOfIterations * RandomIdentifierUtil.SIZE));

            assertThat(distribution).isCloseTo(1.0, Percentage.withPercentage(10));
        });
    }
}