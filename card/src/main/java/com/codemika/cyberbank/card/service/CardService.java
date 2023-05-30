package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.entity.CreditCardEntity;
import com.codemika.cyberbank.card.entity.DebitCardEntity;
import com.codemika.cyberbank.card.repository.CreditCardRepository;
import com.codemika.cyberbank.card.repository.DebitCardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final DebitCardRepository debitRepository;
    private final CreditCardRepository creditRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String urlGetUserByPhone = "http://localhost:8081/api/auth/jgkg3459-ffklre-dgjkrl345tkg94vkdpfjogrpo394/?phone=";

    /**
     * Главный метод для всех переводов. Определяет типы карт и отправляет в нужный moneyTransfer.
     *
     * @param token                  токен пользователя, переводящего деньги
     * @param pincode                пин-код карты, с которой переводятся деньги
     * @param accountNumber          номер карты, с которой переводятся деньги
     * @param value                  количество переводимых денег (в рублях)
     * @param receivingAccountNumber номер карты, на которую переводятся деньги
     * @return см. вызываемый метод
     */
    @Transactional
    public ResponseEntity<?> mainMoneyTransfer(String token,
                                               String pincode,
                                               String accountNumber,
                                               Long value,
                                               String receivingAccountNumber) {
        Optional<DebitCardEntity> card = debitRepository.findCardByAccountNumber(accountNumber);
        Optional<DebitCardEntity> receivingCard = debitRepository.findCardByAccountNumber(receivingAccountNumber);
        Optional<CreditCardEntity> cCard = null;
        Optional<CreditCardEntity> cRec = null;

        boolean isDebit = true;
        boolean isRecDebit = true;

        if (value == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Некорректная сумма перевода");
        if (value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent()) {
            cCard = creditRepository.findAllByAccountNumber(accountNumber);
            isDebit = false;
            if (!cCard.isPresent())
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Карты с номером карты " + accountNumber + " не существует");
        }

        if (!receivingCard.isPresent()) {
            cRec = creditRepository.findAllByAccountNumber(receivingAccountNumber);
            isRecDebit = false;
            if (!cRec.isPresent())
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Карты с номером " + receivingAccountNumber + " не существует");
        }
        if (isDebit && isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    receivingAccountNumber,
                    card.get(),
                    receivingCard.get());
        } else if (!isDebit && isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    receivingAccountNumber,
                    cCard.get(),
                    receivingCard.get());
        } else if (isDebit && !isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    receivingAccountNumber,
                    card.get(),
                    cRec.get());
        } else {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    receivingAccountNumber,
                    cCard.get(),
                    cRec.get());
        }
    }

    /**
     * Главный метод для всех переводов. Определяет типы карт и отправляет в нужный moneyTransfer. (Это оказалось довольно сложно)
     *
     * @param token         токен пользователя, переводящего деньги
     * @param pincode       пин-код карты, с которой переводятся деньги
     * @param accountNumber номер карты, с которой переводятся деньги
     * @param value         количество переводимых денег (в рублях)
     * @param phone         номер телефона владельца карты, на которую переводятся деньги
     * @return см. вызываемый метод
     */
    @Transactional
    public ResponseEntity<?> mainMoneyTransferByPhone(String token,
                                                      String pincode,
                                                      String accountNumber,
                                                      Long value,
                                                      String phone) {
        Optional<DebitCardEntity> card = debitRepository.findCardByAccountNumber(accountNumber);
        ResponseEntity<Long> response = restTemplate.getForEntity(urlGetUserByPhone + phone, Long.class);
        List<DebitCardEntity> dCards = debitRepository.findAllByOwnerUserId(response.getBody());
        //fixme: header не работает, тут временная мера
        /*
        org.springframework.web.client.HttpClientErrorException$BadRequest:
        400 : [Missing request header 'Authorization' for method parameter of type String]
         */
        List<CreditCardEntity> cCards = null;
        boolean isD = true;
        if (dCards.isEmpty()) {
            isD = false;
            cCards = creditRepository.findAllByOwnerUserId(response.getBody());//fixme
            if (cCards.isEmpty())
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("Получатель не имеет карт.");
        }
        String recNumber;
        if (isD) {
            recNumber = dCards.get(0).getAccountNumber();
        } else {
            recNumber = cCards.get(0).getAccountNumber();
        }
        Optional<DebitCardEntity> dreceivingCard = null;
        Optional<CreditCardEntity> creceivingCard = null;
        if (isD) {
            dreceivingCard = debitRepository.findCardByAccountNumber(recNumber);
        } else {
            creceivingCard = creditRepository.findCardByAccountNumber(recNumber);
        }

        Optional<CreditCardEntity> cCard = null;
        Optional<CreditCardEntity> cRec = null;

        boolean isDebit = true;
        boolean isRecDebit = true;

        if (value == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Некорректная сумма перевода");
        if (value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent()) {
            cCard = creditRepository.findAllByAccountNumber(accountNumber);
            isDebit = false;
            if (!cCard.isPresent())
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Карты с номером карты " + accountNumber + " не существует");
        }

        if (!dreceivingCard.isPresent()) {
            cRec = creditRepository.findAllByAccountNumber(recNumber);
            isRecDebit = false;
            if (!cRec.isPresent())
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Карты с номером " + recNumber + " не существует");
        }
        if (isDebit && isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    recNumber,
                    card.get(),
                    dreceivingCard.get());
        } else if (!isDebit && isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    recNumber,
                    cCard.get(),
                    dreceivingCard.get());
        } else if (isDebit && !isRecDebit) {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    recNumber,
                    card.get(),
                    cRec.get());
        } else {
            return moneyTransfer(token,
                    pincode,
                    accountNumber,
                    value,
                    recNumber,
                    cCard.get(),
                    cRec.get());
        }
    }

    /**
     * Один из методов перевода. Тип - Д&Д
     *
     * @param token                  токен переводящего
     * @param pincode                пин-код карты, с которой происходит перевод
     * @param accountNumber          номер карты, с которой происходит перевод
     * @param value                  сумма перевода
     * @param receivingAccountNumber номер, на который происходит перевод
     * @param card                   карта, с которой происходит перевод
     * @param rCard                  карта, на которую происходит перевод
     * @return перевод + вывод нового баланса карты переводящего
     */
    private ResponseEntity<?> moneyTransfer(String token,
                                            String pincode,
                                            String accountNumber,
                                            Long value,
                                            String receivingAccountNumber,
                                            DebitCardEntity card,
                                            DebitCardEntity rCard) {

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        if (!card.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта отправителя заморожена");

        if (!rCard.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта получателя заморожена");

        if (!passwordEncoder.matches(pincode, card.getPincode()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пин-код");

        if (!card.getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с id " + ownerUserId + " не обладает картой с номером карты " + accountNumber);

        if (card.getBalance() < value)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("На карте недостаточно средств");

        if (accountNumber.equals(receivingAccountNumber))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете перевести деньги на свою карту");

        rCard.setBalance(rCard.getBalance() + value);
        card.setBalance(card.getBalance() - value);

        debitRepository.moneyTransfer(card.getBalance(), card.getId());
        debitRepository.moneyTransfer(rCard.getBalance(), rCard.getId());

        return ResponseEntity
                .ok("Перевод доставлен! На данный момент ваш баланс " + card.getBalance() + " рублей");
    }

    /**
     * Один из методов перевода. Тип - К&Д
     *
     * @param token                  токен переводящего
     * @param pincode                пин-код карты, с которой происходит перевод
     * @param accountNumber          номер карты, с которой происходит перевод
     * @param value                  сумма перевода
     * @param receivingAccountNumber номер, на который происходит перевод
     * @param card                   карта, с которой происходит перевод
     * @param rCard                  карта, на которую происходит перевод
     * @return перевод + вывод нового баланса карты переводящего
     */
    private ResponseEntity<?> moneyTransfer(String token,
                                            String pincode,
                                            String accountNumber,
                                            Long value,
                                            String receivingAccountNumber,
                                            CreditCardEntity card,
                                            DebitCardEntity rCard) {

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        if (!card.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта отправителя заморожена");

        if (!rCard.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта получателя заморожена");

        if (!passwordEncoder.matches(pincode, card.getPincode()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пин-код");

        if (!card.getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с id " + ownerUserId + " не обладает картой с номером карты " + accountNumber);

        if (card.getBalance() < value)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("На карте недостаточно средств");

        if (accountNumber.equals(receivingAccountNumber))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете перевести деньги на свою карту");

        rCard.setBalance(rCard.getBalance() + value);
        card.setBalance(card.getBalance() - value);

        creditRepository.moneyTransfer(card.getBalance(), card.getId());
        debitRepository.moneyTransfer(rCard.getBalance(), rCard.getId());

        return ResponseEntity
                .ok("Перевод доставлен! На данный момент ваш баланс " + card.getBalance() + " рублей");
    }

    /**
     * Один из методов перевода. Тип - Д&К
     *
     * @param token                  токен переводящего
     * @param pincode                пин-код карты, с которой происходит перевод
     * @param accountNumber          номер карты, с которой происходит перевод
     * @param value                  сумма перевода
     * @param receivingAccountNumber номер, на который происходит перевод
     * @param card                   карта, с которой происходит перевод
     * @param rCard                  карта, на которую происходит перевод
     * @return перевод + вывод нового баланса карты переводящего
     */
    private ResponseEntity<?> moneyTransfer(String token,
                                            String pincode,
                                            String accountNumber,
                                            Long value,
                                            String receivingAccountNumber,
                                            DebitCardEntity card,
                                            CreditCardEntity rCard) {

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        if (!card.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта отправителя заморожена");

        if (!rCard.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта получателя заморожена");

        if (!passwordEncoder.matches(pincode, card.getPincode()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пин-код");

        if (!card.getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с id " + ownerUserId + " не обладает картой с номером карты " + accountNumber);

        if (card.getBalance() < value)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("На карте недостаточно средств");

        if (accountNumber.equals(receivingAccountNumber))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете перевести деньги на свою карту");

        rCard.setBalance(rCard.getBalance() + value);
        card.setBalance(card.getBalance() - value);

        debitRepository.moneyTransfer(card.getBalance(), card.getId());
        creditRepository.moneyTransfer(rCard.getBalance(), rCard.getId());

        return ResponseEntity
                .ok("Перевод доставлен! На данный момент ваш баланс " + card.getBalance() + " рублей");
    }

    /**
     * Один из методов перевода. Тип - К&К
     *
     * @param token                  токен переводящего
     * @param pincode                пин-код карты, с которой происходит перевод
     * @param accountNumber          номер карты, с которой происходит перевод
     * @param value                  сумма перевода
     * @param receivingAccountNumber номер, на который происходит перевод
     * @param card                   карта, с которой происходит перевод
     * @param rCard                  карта, на которую происходит перевод
     * @return перевод + вывод нового баланса карты переводящего
     */
    private ResponseEntity<?> moneyTransfer(String token,
                                            String pincode,
                                            String accountNumber,
                                            Long value,
                                            String receivingAccountNumber,
                                            CreditCardEntity card,
                                            CreditCardEntity rCard) {

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        if (!card.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта отправителя заморожена");

        if (!rCard.getIsActive())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Карта получателя заморожена");

        if (!passwordEncoder.matches(pincode, card.getPincode()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пин-код");

        if (!card.getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с id " + ownerUserId + " не обладает картой с номером карты " + accountNumber);

        if (card.getBalance() < value)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("На карте недостаточно средств");

        if (accountNumber.equals(receivingAccountNumber))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете перевести деньги на свою карту");

        rCard.setBalance(rCard.getBalance() + value);
        card.setBalance(card.getBalance() - value);

        creditRepository.moneyTransfer(card.getBalance(), card.getId());
        creditRepository.moneyTransfer(rCard.getBalance(), rCard.getId());

        return ResponseEntity
                .ok("Перевод доставлен! На данный момент ваш баланс " + card.getBalance() + " рублей");
    }

    /**
     * Генерация случайного номера карты
     *
     * @param n размер строки(у нас 16)
     * @return Случайную строку
     */
    public static String generateAccountNumber(int n) {
        String alphabet = "0123456789";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = (int) (alphabet.length() * Math.random());
            result.append(alphabet.charAt(index));
        }

        return result.toString();
    }

    /**
     * Заморозка и разморозка карты
     *
     * @param token   токен владельца
     * @param cardId  id карты
     * @param pincode пин-код карты(не точно, это проверяется)
     * @return сообщение об успешной/не успешной заморозке/разморозке
     */
    @Transactional
    public ResponseEntity<?> FreezeAndUnfreezeCard(String token, Long cardId, String pincode) {
        Optional<DebitCardEntity> cardEntity = debitRepository.findById(cardId);
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        if (!cardEntity.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Данной карты не существует!");
        }

        if (!Objects.equals(cardEntity.get().getOwnerUserId(), id)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не являетесь владельцем данной карты!");
        }

        if (!passwordEncoder.matches(pincode, cardEntity.get().getPincode())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вами введён неверный пин-код!");
        }

        debitRepository.updateById(!cardEntity.get().getIsActive(), cardId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Ваша карта была успешно " + (
                        cardEntity.get().getIsActive()
                                ? "заморожена!"  //Если карта была размороженной(true)
                                : "разморожена!" //Если карта была замороженной(false)
                ));
    }

    /**
     * Вывод всех карт пользователя
     *
     * @param token уникальный токен авторизации
     * @return Все карты
     */
    public ResponseEntity<?> getAllCards(String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        List<DebitCardEntity> cards = debitRepository.findAllByOwnerUserId(id);// todo временно

        if (cards.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("У вас нет карт!");

        return ResponseEntity.ok(cards);
    }

    /**
     * ТОЛЬКО ДЛЯ МОДЕРОВ
     * Получение ВСЕХ карт в банке
     *
     * @return Все карты банка
     */
    public ResponseEntity<?> getAllCards() {
        List<DebitCardEntity> cards = debitRepository.findAll();// TODO временно

        if (cards.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("All users have no cards!");

        return ResponseEntity.ok(cards);
    }

    /**
     * Метод только для пользователей с ролями МОДЕР и ТЕСТЕР
     * Создание денег из воздуха
     *
     * @param cardId карта, на которую зачисляются деньги
     * @param value  количество денег
     * @return сообщение
     */
    @Transactional
    public ResponseEntity<?> getMeMoney(Long cardId, Long value) {
        Optional<DebitCardEntity> card = debitRepository.findById(cardId);
        if (value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с id: " + cardId + " не существует");

        card.get().setBalance(card.get().getBalance() + value);

        debitRepository.moneyTransfer(card.get().getBalance(), cardId);

        return ResponseEntity
                .ok("Вы успешно получили " + value + " рублей");
    }
}