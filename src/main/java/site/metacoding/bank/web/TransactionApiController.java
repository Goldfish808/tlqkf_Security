package site.metacoding.bank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.annotations.AuthorizationCheck;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransferReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransferRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;
import site.metacoding.bank.service.TransactionService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionApiController {
        private final TransactionService transactionService;

        /*
         * 입금
         * ATM에서 계좌로 입금하는 것이기 때문에 인증이 필요없다.
         */
        @PostMapping("/deposit")
        public ResponseEntity<?> deposit(@RequestBody DepositReqDto depositReqDto) {
                DepositRespDto depositRespDto = transactionService.입금하기(depositReqDto);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, depositRespDto),
                                HttpStatus.CREATED);
        }

        /*
         * 출금
         */
        @AuthorizationCheck
        @PostMapping("/user/{userId}/withdraw")
        public ResponseEntity<?> withdraw(@PathVariable Long userId,
                        @RequestBody WithdrawReqDto withdrawReqDto,
                        @AuthenticationPrincipal LoginUser loginUser) {
                WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, userId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, withdrawRespDto),
                                HttpStatus.CREATED);

        }

        /*
         * 이체 (== 출금)
         */
        @AuthorizationCheck
        @PostMapping("/user/{userId}/transfer")
        public ResponseEntity<?> transfer(@PathVariable Long userId,
                        @RequestBody TransferReqDto transferReqDto,
                        @AuthenticationPrincipal LoginUser loginUser) {
                TransferRespDto transferRespDto = transactionService.이체하기(transferReqDto, userId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, transferRespDto),
                                HttpStatus.CREATED);
        }

        /*
         * 입출금 내역 보기 (동적 쿼리로 변경)
         */
        @AuthorizationCheck
        @GetMapping("/user/{userId}/account/{accountId}/transaction")
        public ResponseEntity<?> transactionList(String gubun,
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @PathVariable Long userId,
                        @PathVariable Long accountId,
                        @AuthenticationPrincipal LoginUser loginUser) {
                TransactionListRespDto transactionListRespDto = transactionService.입출금목록보기(userId,
                                accountId,
                                gubun,
                                page);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, transactionListRespDto),
                                HttpStatus.OK);
        }
}
