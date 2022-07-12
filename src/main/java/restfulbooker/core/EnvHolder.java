package restfulbooker.core;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class EnvHolder {
    public static final String RESTFUL_BOOKER_USERNAME = getEnvironmentValue("RESTFUL_BOOKER_USERNAME");
    public static final String RESTFUL_BOOKER_PASSWORD = getEnvironmentValue("RESTFUL_BOOKER_PASSWORD");

    private static String getEnvironmentValue(String envName) {
        return getEnvironmentValue(envName, "");
    }

    private static String getEnvironmentValue(String envName, String otherwise) {
        return Optional.ofNullable(System.getenv(envName)).orElse(otherwise);
    }
}
