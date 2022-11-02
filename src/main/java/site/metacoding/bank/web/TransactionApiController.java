package site.metacoding.bank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransperReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransperRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.enums.UserEnum;
import site.metacoding.bank.handler.exception.CustomApiException;
import site.metacoding.bank.service.TransactionService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionApiController {
    private final TransactionService transactionService;

    /*
     * 출금
     */
    @PostMapping("/user/{userId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long userId,
            @RequestBody WithdrawReqDto withdrawReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        // 권한 확인
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, userId);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, withdrawRespDto),
                HttpStatus.CREATED);

    }

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
     * 이체 (== 출금)
     */
    @PostMapping("/user/{userId}/transper")
    public ResponseEntity<?> transper(@PathVariable Long userId,
            @RequestBody TransperReqDto transperReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        // 권한 확인
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        TransperRespDto transperRespDto = transactionService.이체하기(transperReqDto, userId);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, transperRespDto),
                HttpStatus.CREATED);
    }

    /*
     * 유저 1번의 계좌 조회 ?gubun=WITHDRAW
     */
    @GetMapping("/user/{userId}/account/{accountId}/withdraw")
    public ResponseEntity<?> withdrawList(@PathVariable Long userId, @PathVariable Long accountId, String gubun,
            @AuthenticationPrincipal LoginUser loginUser) {
        // 권한 확인
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        transactionService.출금목록보기(null);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, null), HttpStatus.OK);
    }
}
