package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.bean.DummyBeans;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountApiControllerTest extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());
        private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
        private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

        // DI
        @Autowired
        private ObjectMapper om;
        @Autowired
        private MockMvc mvc;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private TransactionRepository transactionRepository;

        @BeforeEach
        public void setUp() {
                dataSetting();
        }

        /**
         * 계좌등록
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void save_test() throws Exception {
                // given
                AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
                accountSaveReqDto.setNumber(55556666L);
                accountSaveReqDto.setOwnerName("홍길동");
                accountSaveReqDto.setPassword("1234");

                String requestBody = om.writeValueAsString(accountSaveReqDto);
                log.debug("디버그 : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/account").content(requestBody).contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
        }

        /**
         * 본인 계좌목록
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void list_test() throws Exception {
                // given

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/1/account"));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        /**
         * 본인 계좌 상세보기
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void detail_test() throws Exception {
                // given
                Long accountId = 1L;
                Long userId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void delete_test() throws Exception {
                // given
                Long accountId = 1L;
                Long userId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(delete("/api/user/" + userId + "/account/" + accountId));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        public void dataSetting() {
                User ssarUser = userRepository.save(newUser(1L, "ssar"));
                User cosUser = userRepository.save(newUser(2L, "cos"));
                User adminUser = userRepository.save(newUser(3L, "admin"));
                Account ssarAccount1 = accountRepository.save(newAccount(1L, 1111L, ssarUser));
                Account ssarAccount2 = accountRepository.save(newAccount(2L, 2222L, ssarUser));
                Account cosAccount1 = accountRepository.save(newAccount(3L, 3333L, cosUser));
                Transaction withdrawTransaction1 = transactionRepository.save(newWithdrawTransaction(1L, ssarAccount1));
                Transaction withdrawTransaction2 = transactionRepository.save(newWithdrawTransaction(2L, ssarAccount1));
                Transaction depositTransaction1 = transactionRepository.save(newDepositTransaction(3L, ssarAccount1));
                Transaction transferTransaction1 = transactionRepository
                                .save(newTransferTransaction(4L, ssarAccount1, cosAccount1));
        }
}
