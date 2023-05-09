package com.codemika.cyberbank.authentication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    /**
     * Название роли пользователя, которому нужно дать доступ
     * @return название роли
     */
    String role();

    /**
     * Название атрибута внутри контроллера, в котором хранится токен пользователя
     * @return название атрибута
     */
    String tokenParamName() default "token";
}
