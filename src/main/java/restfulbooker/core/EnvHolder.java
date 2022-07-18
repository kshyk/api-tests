package restfulbooker.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvHolder {
    public static final String RESTFUL_BOOKER_USERNAME = System.getenv("RESTFUL_BOOKER_USERNAME");
    public static final String RESTFUL_BOOKER_PASSWORD = System.getenv("RESTFUL_BOOKER_PASSWORD");
}
