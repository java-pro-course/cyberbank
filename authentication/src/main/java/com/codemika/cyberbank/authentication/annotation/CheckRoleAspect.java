package com.codemika.cyberbank.authentication.annotation;

import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckRoleAspect {
    private final JwtUtil jwtUtil;

    @Around(value = "@annotation(checkRole)")
    public Object checkRole(ProceedingJoinPoint proceedingJoinPoint, CheckRole checkRole) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature(); // получаем сигнатуру метода

        Object[] args = proceedingJoinPoint.getArgs(); // массив значение всех атрибутов метода
        String[] paramsName = methodSignature.getParameterNames(); // массив названий всех атрибутов метода

        int index = Arrays.asList(paramsName).indexOf(checkRole.tokenParamName()); // ищем атрибут, у которого имя из нашей аннотации (по умолчанию token)

        String token = args[index].toString();
        Claims claims = jwtUtil.getClaims(token);

        String role = claims.get("role", String.class);
        if (!Objects.equals(role, checkRole.role())) {
            throw new RuntimeException("Вы не имеете доступа к данной функции.");
        }

        return proceedingJoinPoint.proceed();
    }
}
