package org.gfg.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.gfg.walletservice.model.Wallet;
import org.gfg.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.gfg.walletservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.gfg.walletservice.constants.UserCreationTopicConstants.PHONENO;
import static org.gfg.walletservice.constants.UserCreationTopicConstants.USERID;

@Service
@Slf4j
public class UserCreationConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    WalletRepository walletRepository;

    @Value("${wallet.initial.amount}")
    Double walletAmount;

    @KafkaListener(topics = USER_CREATION_TOPIC, groupId = "wallet-group")
    public void userCreated(String message) throws JsonProcessingException {
        log.info("User created message received: {}", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String phoneNo = node.get(PHONENO).textValue();
        Integer userId = node.get(USERID).intValue();

        Wallet wallet = Wallet.builder()
                .phoneNo(phoneNo)
                .userId(userId)
                .balance(walletAmount).build();

        walletRepository.save(wallet);

        log.info("wallet saved for user: {}", userId);

    }
}
