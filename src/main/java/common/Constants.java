package common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    @UtilityClass
    public class Numbers {
        public static final long AVG_TIMEOUT = 3000L;
    }

    @UtilityClass
    public class Strings {
        public static final String INCORRECT_FIRSTNAME = "Incorrect first name";
        public static final String INCORRECT_LASTNAME = "Incorrect last name";
        public static final String INCORRECT_TOTAL_PRICE = "Incorrect total price";
        public static final String INCORRECT_DEPOSIT_PAID_STATE = "Incorrect deposit paid state";
        public static final String INCORRECT_CHECK_IN_DATE = "Incorrect check-in date";
        public static final String INCORRECT_CHECK_OUT_DATE = "Incorrect check-out date";
        public static final String INCORRECT_ADDITIONAL_NEEDS = "Additional needs do not match";
    }
}
