package com.codemika.cyberbank.authentication.constants;

public class RoleConstants {
    public final static String IS_USER_ROLE_EXIST_CLAIMS_KEY = "is_user_role";

    public final static String USER_ROLE = "USER";

    // Роль для модераторов банка, имеет повышенный, но не полный доступ к функциям банка.
    public final static String MODER_ROLE = "MODER";

    // Роль для тестировщиков, имеет доступ ко всем функциям банка
    public final static String TESTER_ROLE = "TESTER";

    // Шуточная роль, имеет доступ ко всем функциям банка
    public final static String HACKER_ROLE = "HACKER";
}
