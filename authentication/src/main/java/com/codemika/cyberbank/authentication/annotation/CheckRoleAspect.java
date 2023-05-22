package com.codemika.cyberbank.authentication.annotation;

import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
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

import static com.codemika.cyberbank.authentication.constants.RoleConstants.*;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckRoleAspect {
    private final JwtUtil jwtUtil;

    /**
     * Проверка ролей
     *
     * @param proceedingJoinPoint
     * @param checkRole boolean-значения для каждой роли является/не является
     * @return proceedingJoinPoint типо прога продолжает свою работу
     * @throws Throwable
     */
    @Around(value = "@annotation(checkRole)")
    public Object checkRole(ProceedingJoinPoint proceedingJoinPoint, CheckRole checkRole) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature(); // получаем сигнатуру метода

        Object[] args = proceedingJoinPoint.getArgs(); // массив значение всех атрибутов метода
        String[] paramsName = methodSignature.getParameterNames(); // массив названий всех атрибутов метода

        int index = Arrays.asList(paramsName).indexOf(checkRole.tokenParamName()); // ищем атрибут, у которого имя из нашей аннотации (по умолчанию token)
        String token;
        try {
            token = args[index].toString();
        } catch (IndexOutOfBoundsException e) {
            log.error("Блин, я опять не добавил поле \"токен\" в контроллер!");
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Извините, данная опция пока не работает.");
        }
        Claims claims = jwtUtil.getClaims(token);

        boolean isUserToken = claims.get(IS_USER_ROLE_EXIST_CLAIMS_KEY, Boolean.class);
        boolean isModerToken = claims.get(IS_MODER_ROLE_EXIST_CLAIMS_KEY, Boolean.class);
        boolean isTesterToken = claims.get(IS_TESTER_ROLE_EXIST_CLAIMS_KEY, Boolean.class);
        boolean isHackerToken = claims.get(IS_HACKER_ROLE_EXIST_CLAIMS_KEY, Boolean.class);

        if (!isUserToken) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Ваш последний сеанс истёк. Пожалуйста, войдите в свой аккаунт заново!");
        }
        if (isLet(isModerToken, checkRole.isModer(),
                isTesterToken, checkRole.isTester(),
                isHackerToken, checkRole.isHacker())) {
            return proceedingJoinPoint.proceed();
        } else {
            //403 - Forbidden - Доступ запрещён
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Вы не имеете доступа к данной функции.");
        }
    }

    /**
     *
     * @param isModerToken имеется ли роль MODER
     * @param isModer требуется ли роль MODER
     * @param isTesterToken имеется ли роль TESTER
     * @param isTester требуется ли роль TESTER
     * @param isHackerToken имеется ли роль Hacker
     * @param isHacker требуется ли роль HACKER
     * @return разрешён ли доступ (да/нет - true/false)
     */
    boolean isLet(boolean isModerToken, boolean isModer,
                  boolean isTesterToken, boolean isTester,
                  boolean isHackerToken, boolean isHacker) {
        if (isTester || isHacker) return isTesterToken || isHackerToken;
        if (isModer) return isModerToken;
        return false;
    }
}
