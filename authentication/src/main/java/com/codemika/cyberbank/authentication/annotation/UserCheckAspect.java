package com.codemika.cyberbank.authentication.annotation;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.repository.UserRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final UserRepository userRepository;

    /**
     * Основной метод класса(связующее звено)
     *
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

        if(!bigCheck(rq).getStatusCode().is2xxSuccessful()){
            authorizationService.setCheck(false); // устанавливаем параметр проверки в сервисе
            authorizationService.setErrorMessage(bigCheck(rq)); // отправляем сообщение с ошибкой
            return authorizationService.registration(rq); // вызываем метод сервиса
        }

        authorizationService.setCheck(true);
        return authorizationService.registration(rq);

    }

    /**
     * Большой метод для всех основных проверок
     *
     * @param user проверяемый пользователь
     * @return Результат
     */
    private ResponseEntity<?> bigCheck(RqCreateUser user){
        //Проверка на уникальность пользователя по почте и номеру
        if(userRepository.findByPhone(user.getPhone()).isPresent() || userRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Пожалуйста, проверьте свою контактную информацию. Такой человек уже существует!");
        }
        //Пустота заполнения(null не нужно, т.к. могут быть только пустые строчки).
        if (user.getName().equals("") || user.getSurname().equals("")
                || user.getPatronymic().equals("") || user.getEmail().equals("")
                || user.getPhone().equals("") || user.getPassword().equals("")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ни одно из полей не должно быть пустым!");
        }
        //Пробелы в имени
        if (user.getName().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ваше имя не должно содержать пробелы!");
        }
        //Пробелы в фамилии
        if (user.getSurname().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ваша фамилия не должна содержать пробелы!");
        }
        //Пробелы в отчестве
        if (user.getPatronymic().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ваше отчество не должно содержать пробелы!");
        }
        //Пробелы в почте
        if (user.getEmail().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Адрес электронной почты не должен содержать пробелы!");
        }
        //Пробелы в номере телефона
        if (user.getPhone().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Номер телефона не должен содержать пробелы!");
        }
        //Пробелы в пароле
        if (user.getPassword().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пароль не должен содержать пробелы!");
        }
        //Содержание имени, фамилии или отчества в пароле
        if (user.getPassword().toLowerCase().contains(user.getName().toLowerCase())
                || user.getPassword().toLowerCase().contains(user.getSurname().toLowerCase())
                || user.getPassword().toLowerCase().contains(user.getPatronymic().toLowerCase())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("В пароле не должно быть вашего имени, фамилии или отчества! Это не безопасно!");
        }
        //Заглавные буквы в пароле
        if (user.getPassword().equals(user.getPassword().toLowerCase())
                || user.getPassword().equals(user.getPassword().toUpperCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ваш пароль должен состоять из ЗАГЛАВНЫХ и строчных букв!");
        }
        //Корректность номера телефона
        if(!numberCheck(user.getPhone())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ваш номер телефона должен быть настоящим!");
        }
        //Корректность почты
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверная эл. почта!");
        }
        //Символы и т.д. в пароле
        String symbols = "§±!#$%&()*+,-./0123456789:;<=>?@[]^_`{|}~\"'\\";

        if (!lettersCheck(symbols, user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Введите пароль с символами и цифрами!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Успешно.");
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

    /**
     * Проверка номера телефона
     *
     * @param number проверяемый номер телефона
     * @return true/false
     */
    public static boolean numberCheck(String number){
        Pattern ptrn = Pattern.compile("(0/300)?[7-9]?[0-9]{9}");
        Matcher match = ptrn.matcher(number);

        return match.find();
    }
}
