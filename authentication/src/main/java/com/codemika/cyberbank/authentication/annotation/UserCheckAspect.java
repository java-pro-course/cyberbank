package com.codemika.cyberbank.authentication.annotation;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.service.AuthorizationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * Класс для логики аннотации проверки пользователя
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class UserCheckAspect {
    private final AuthorizationService authorizationService;

    /**
     * Основной метод класса(связующее звено)
     * @param proceedingJoinPoint
     * @param userCheck
     * @return результат регистрации
     */
    @Around(value = "@annotation(userCheck)")
    public ResponseEntity<?> checkThisUser(ProceedingJoinPoint proceedingJoinPoint, UserCheck userCheck){
        //Используется для поиска параметра по имени в аннотации
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        int nameIndex = Arrays.asList(parameterNames).indexOf(userCheck.name());

        RqCreateUser rq = (RqCreateUser) args[nameIndex];

        if(bigCheck(rq).getBody().toString().contains("!")){
            authorizationService.setCheck(false);
            authorizationService.setErrorMessage(bigCheck(rq));
            return authorizationService.registration(rq);
        }

        authorizationService.setCheck(true);
        return authorizationService.registration(rq);

    }

    /**
     * Большой метод для всех основных проверок
     *
     * @param user
     * @return Результат
     */
    private ResponseEntity<?> bigCheck(RqCreateUser user){
        //Пустота заполнения(null не нужно, т.к. могут быть только пустые строчки).
        if (user.getName().equals("") || user.getSurname().equals("")
                || user.getEmail().equals("") || user.getPassword().equals("")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("None of the fields must not be empty!");
        }
        //Пробелы в имени
        if (user.getName().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Your name must not contain spaces!");
        }
        //Пробелы в фамилии
        if (user.getSurname().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Your surname must not contain spaces!");
        }
        //Пробелы в почте
        if (user.getEmail().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The email must not contain spaces!");
        }
        //Пробелы в пароле
        if (user.getPassword().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The password must not contain spaces!");
        }
        //Содержание имени или фамилии в пароле
        if (user.getPassword().toLowerCase().contains(user.getName().toLowerCase())
                || user.getPassword().toLowerCase().contains(user.getSurname().toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The password must not contain your name or your surname! It's not secure!");
        }
        //Заглавные буквы в пароле
        if (user.getPassword().equals(user.getPassword().toLowerCase())
                || user.getPassword().equals(user.getPassword().toUpperCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The password must contain uppercase and lowercase letters!");
        }
        //Корректность почты
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email!");
        }

        String symbols = "§±!#$%&()*+,-./0123456789:;<=>?@[]^_`{|}~\"'\\";
        //Символы и т.д. в пароле
        if (!lettersCheck(symbols, user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Provide your password with symbols and numbers!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Success");
    }

    /**
     * Проверка на содержание символов
     *
     * @param current строка для сравнения
     * @param check проверяемая строка
     * @return true/false
     */
    public static boolean lettersCheck(String current, String check) {
        boolean result = false;
        for (int i = 0; i < check.length(); i++) {
            for (int j = 0; j < current.length(); j++) {
                String a = String.valueOf(current.charAt(j));
                if(check.contains(a)){
                    result = true;
                }
            }
        }
        return result;
    }
}
