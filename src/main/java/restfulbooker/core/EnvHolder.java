package restfulbooker.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvHolder {
    public static final String RESTFUL_BOOKER_USERNAME = getEnvironmentValue("RESTFUL_BOOKER_USERNAME");
    public static final String RESTFUL_BOOKER_PASSWORD = getEnvironmentValue("RESTFUL_BOOKER_PASSWORD");

    private static String getEnvironmentValue(String envName) {
        return System.getenv(envName);
    }
}
