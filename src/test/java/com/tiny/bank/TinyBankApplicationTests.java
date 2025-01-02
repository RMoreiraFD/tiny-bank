package com.tiny.bank;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.tiny.bank.api.model.request.TransactionRequest;
import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TinyBankApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);

    @Test
    void userCreation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Rui Moreira",
                                  "ccNumber": "14958775",
                                  "birthdate": "2001-12-10"
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.name").value("Rui Moreira"))
                .andExpect(jsonPath("$.ccNumber").value("14958775"))
                .andExpect(jsonPath("$.birthdate").value("2001-12-10"))
                .andExpect(jsonPath("$.accounts").isEmpty())
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andDo(print());
    }

    @Test
    void userDeactivation() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}/deactivate", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ccNumber").value(userId))
                .andExpect(jsonPath("$.state").value("INACTIVE"))
                .andDo(print());
    }

    @Test
    void userAccountCreation() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ccNumber").value(userId))
                .andExpect(jsonPath("$.accounts[0].accountId").exists())
                .andExpect(jsonPath("$.accounts[0].transactions").isEmpty())
                .andExpect(jsonPath("$.accounts[0].balance").value("0"))
                .andDo(print());
    }

    @Test
    void userAccountCheckBalance() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        final User user = createAccount(userId);

        final Account account = user.accounts().stream().findAny().get();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/accounts/{accountId}/balance", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("0"))
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andDo(print());
    }

    @Test
    void shouldDepositInAnAccount() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        final User user = createAccount(userId);

        final Account account = user.accounts().stream().findAny().get();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts/{accountId}/deposit", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "150.0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldWithdrawalInAnAccount() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        final User user = createAccount(userId);

        final Account account = user.accounts().stream().findAny().get();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts/{accountId}/deposit", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "150.0"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts/{accountId}/withdraw", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "150.0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldTransactionHistoryFromAccount() throws Exception {
        final String userId = UUID.randomUUID().toString();
        createUser(userId);

        final User user = createAccount(userId);

        final Account account = user.accounts().stream().findAny().get();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts/{accountId}/deposit", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "150.0"))
                .andExpect(status().isOk())
                .andDo(print());


        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/accounts/{accountId}/transactions", userId, account.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionRecords[0].amount").value("150.0"))
                .andExpect(jsonPath("$.transactionRecords[0].accountId").value(account.getAccountId().toString()))
                .andDo(print());
    }

    @Test
    void shouldTestATransaction() throws Exception {
        final String senderUserId = UUID.randomUUID().toString();
        createUser(senderUserId);

        final String receiverUserId = UUID.randomUUID().toString();
        createUser(receiverUserId);

        final User senderUser = createAccount(senderUserId);
        final User receiverUser = createAccount(receiverUserId);

        final Account senderAccount = senderUser.accounts().stream().findAny().get();
        final Account receiverAccount = receiverUser.accounts().stream().findAny().get();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts/{accountId}/deposit", senderUserId, senderAccount.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "150.0"))
                .andExpect(status().isOk())
                .andDo(print());

        var transaction = new TransactionRequest(senderUserId, senderAccount.getAccountId().toString(), receiverUserId, receiverAccount.getAccountId().toString(), 140.0);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());
    }


    private void createUser(final String ccNumber) throws Exception {
        objectMapper.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION).registerModule(new JSR310Module());
        var user = User.createUser(UUID.randomUUID().toString(), ccNumber, LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)));
    }

    private User createAccount(String userId) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ccNumber").value(userId))
                .andDo(print())
                .andReturn();

       return objectMapper.readValue(result.getResponse().getContentAsByteArray(), User.class);
    }

}
