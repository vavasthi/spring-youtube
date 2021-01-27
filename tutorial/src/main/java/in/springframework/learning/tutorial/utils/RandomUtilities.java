package in.springframework.learning.tutorial.utils;

import com.github.javafaker.Faker;

public class RandomUtilities {

    private static Faker faker = new Faker();
    public static String createRandomName() {
        return faker.name().fullName();
    }
    public static String createRandomCity() {
        return faker.address().city();
    }
}
