package com.codemika.cyberbank.authentication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Интерфейс для полей аннотации проверки ролей
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    boolean isUser() default false;
    boolean isModer() default false;
    boolean isTester() default false;
    boolean isHacker() default false;
    /**
     * Название атрибута внутри контроллера, в котором хранится токен пользователя
     * @return название атрибута
     */
    String tokenParamName() default "token";


}
