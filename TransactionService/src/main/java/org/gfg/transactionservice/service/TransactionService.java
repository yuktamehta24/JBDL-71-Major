package org.gfg.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.gfg.transactionservice.client.UserServiceClient;
import org.gfg.transactionservice.dto.InitiateTransactionRequest;
import org.gfg.transactionservice.enums.TransactionStatus;
import org.gfg.transactionservice.model.Transaction;
import org.gfg.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.gfg.transactionservice.constants.KafkaCostants.TRANSACTION_INITIATED_TOPIC;
import static org.gfg.transactionservice.constants.TransactionInitiatedConstants.AMOUNT;
import static org.gfg.transactionservice.constants.TransactionInitiatedConstants.RECEIVERPHONENO;
import static org.gfg.transactionservice.constants.TransactionInitiatedConstants.SENDERPHONENO;
import static org.gfg.transactionservice.constants.TransactionInitiatedConstants.TRANSACTIONID;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {

        String auth = "transaction-service:transaction-service";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());

        String authValue = "Basic "+ new String(encodedAuth);
        ObjectNode node = userServiceClient.getUser(phoneNo, authValue);

        log.info("user fetched: {}", node);

        if (node == null) {
            throw new UsernameNotFoundException("user does not exist");
        }

        ArrayNode authorities = (ArrayNode) node.get("authorities");

        final List<GrantedAuthority> authorityList = new ArrayList<>();

        authorities.iterator().forEachRemaining(jsonNode -> {
            authorityList.add(new SimpleGrantedAuthority(jsonNode.get("authority").textValue()));
        });

        User user = new User(node.get("phoneNo").textValue(), node.get("password").textValue(), authorityList);
        return user;
    }

    public String initiateTransaction(String senderPhoneNo,
                                      InitiateTransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderPhoneNo(senderPhoneNo)
                .receiverPhoneNo(request.getReceiverPhoneNo())
                .amount(request.getAmount())
                .purpose(request.getMessage())
                .status(TransactionStatus.INITIATED).build();

        transactionRepository.save(transaction);

        log.info("transaction saved");

        //publish the data to Kafka

        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put(SENDERPHONENO, transaction.getSenderPhoneNo());
        objectNode.put(RECEIVERPHONENO, transaction.getReceiverPhoneNo());
        objectNode.put(AMOUNT, transaction.getAmount());
        objectNode.put(TRANSACTIONID, transaction.getTransactionId());

        String kafkaMessage = objectNode.toString();
        kafkaTemplate.send(TRANSACTION_INITIATED_TOPIC, kafkaMessage);

        log.info("Message published to Kafka: {}", kafkaMessage);

        return transaction.getTransactionId();
    }

    public List<Transaction> findTransactions(String senderPhoneNo,
                                              Integer pageNo,
                                              Integer limit) {

        Pageable pageable = PageRequest.of(pageNo, limit);

//        Page<Transaction> response = transactionRepository.findBySenderPhoneNo(senderPhoneNo, pageable);

        return transactionRepository.findBySenderPhoneNo(senderPhoneNo, pageable);

    }
}
