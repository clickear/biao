package com.biao.web.controller;

import com.biao.config.sercurity.RedisSessionUser;
import com.biao.constant.Constants;
import com.biao.entity.Coin;
import com.biao.entity.WithdrawLog;
import com.biao.enums.CoinTypeEnum;
import com.biao.enums.WithdrawStatusEnum;
import com.biao.google.GoogleAuthenticator;
import com.biao.pojo.GlobalMessageResponseVo;
import com.biao.service.CoinService;
import com.biao.service.WithdrawLogService;
import com.biao.vo.WithdrawListVO;
import com.biao.vo.WithdrawVO;
import com.biao.vo.WithdrawValidateVO;
import com.biao.web.valid.ValidateFiled;
import com.biao.web.valid.ValidateGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 用户coin提币相关操作
 */
@RestController
@RequestMapping("/biao")
public class WithdrawController {

    @Autowired
    private WithdrawLogService withdrawLogService;
    @Autowired
    private CoinService coinService;


    /**
     * 用户提现
     *
     * @param withdrawVO
     * @return
     */
    @PostMapping("/coin/volume/open/withdraw")
    @ValidateGroup(fileds = {
            @ValidateFiled(index = 0, filedName = "coinId", notNull = true, errMsg = "coinId不能为空"),
            @ValidateFiled(index = 0, filedName = "symbol", notNull = true, errMsg = "币种不能为空")
    })
    public GlobalMessageResponseVo openWithDraw(WithdrawVO withdrawVO) {

        Coin coin  = coinService.findById(withdrawVO.getCoinId());
        if(null != coin && coin.getCoinType().equals(CoinTypeEnum.EOS.getCode())){
            return GlobalMessageResponseVo
                    .newSuccessInstance((Object) "1");

        }else{
            return GlobalMessageResponseVo
                    .newSuccessInstance((Object) "0");
        }

    }
    /**
     * 用户提现
     *
     * @param withdrawVO
     * @return
     */
    @PostMapping("/coin/volume/withdraw")
    @ValidateGroup(fileds = {
            @ValidateFiled(index = 0, filedName = "coinId", notNull = true, errMsg = "coinId不能为空"),
            @ValidateFiled(index = 0, filedName = "symbol", notNull = true, errMsg = "币种不能为空"),
            @ValidateFiled(index = 0, filedName = "address", notNull = true, errMsg = "提币地址不能为空", maxLen = 64),
            @ValidateFiled(index = 0, filedName = "volume", notNull = true, errMsg = "提币数量不能为空")
    })
    public Mono<GlobalMessageResponseVo> withDraw(WithdrawVO withdrawVO) {

        Mono<SecurityContext> context
                = ReactiveSecurityContextHolder.getContext();

        return context.filter(c -> Objects.nonNull(c.getAuthentication()))
                .map(s -> s.getAuthentication().getPrincipal())
                .cast(RedisSessionUser.class)
                .map(e -> {
                    final boolean lockTrade = e.isLockTrade();
                    if (lockTrade) {
                        return GlobalMessageResponseVo.newErrorInstance("用户被锁定不能交易");
                    }

                    Coin coin  = coinService.findById(withdrawVO.getCoinId());
                    if(Objects.isNull(coin) || !coin.getName().equals(withdrawVO.getSymbol())){
                        return GlobalMessageResponseVo.newErrorInstance("参数异常");
                    }

                    WithdrawLog withdrawLog = new WithdrawLog();
                    BeanUtils.copyProperties(withdrawVO, withdrawLog);
                    withdrawLog.setUserId(e.getId());
                    withdrawLog.setCoinSymbol(withdrawVO.getSymbol());
                    withdrawLog.setConfirmStatus(0);
                    String id = withdrawLogService.save(withdrawLog);
                    return GlobalMessageResponseVo
                            .newSuccessInstance((Object) id);
                });
    }

    /**
     * 二次确认
     *
     * @param withdrawValidateVO
     * @return
     */
    @PostMapping("/coin/volume/withdraw/validate")
    public Mono<GlobalMessageResponseVo> withDrawValidate(WithdrawValidateVO withdrawValidateVO) {

        Mono<SecurityContext> context
                = ReactiveSecurityContextHolder.getContext();

        return context.filter(c -> Objects.nonNull(c.getAuthentication()))
                .map(s -> s.getAuthentication().getPrincipal())
                .cast(RedisSessionUser.class)
                .map(e -> {
                    String userId = e.getId();
                    //验证邮箱
                    //messageSendService.mailValid("withdraw", VerificationCodeType.WITHDRAW_CODE, e.getMail(), withdrawValidateVO.getEmailCode());
                    //验证Google
                    GoogleAuthenticator ga = new GoogleAuthenticator();
                    ga.setWindowSize(5);
                    boolean result = ga.checkCode(e.getGoogleAuth(), withdrawValidateVO.getGoogleCode(), System.currentTimeMillis());
                    if (!result) {
                        return GlobalMessageResponseVo
                                .newInstance(Constants.GOOGLE_ERROR, "google验证码错误");
                    }
                    withdrawLogService.updateStatusById(userId, WithdrawStatusEnum.INIT.getCode(), withdrawValidateVO.getId());

                    return GlobalMessageResponseVo
                            .newSuccessInstance("提现成功");
                });
    }

    /**
     * 提现取消
     *
     * @param id 主键id
     * @return
     */
    @GetMapping("/coin/volume/cancel/{id}")
    public Mono<GlobalMessageResponseVo> withDrawCancel(@PathVariable("id") String id) {

        Mono<SecurityContext> context
                = ReactiveSecurityContextHolder.getContext();

        return context.filter(c -> Objects.nonNull(c.getAuthentication()))
                .map(s -> s.getAuthentication().getPrincipal())
                .cast(RedisSessionUser.class)
                .map(e -> {
                    String userId = e.getId();
                    withdrawLogService.withDrawCancel(userId, id);
                    return GlobalMessageResponseVo
                            .newSuccessInstance("操作成功！");
                });
    }

    /**
     * 提现列表
     *
     * @param
     * @return
     */
    @PostMapping("/coin/withdraw/list")
    public Mono<GlobalMessageResponseVo> withDrawList(WithdrawListVO withdrawListVO) {

        Mono<SecurityContext> context
                = ReactiveSecurityContextHolder.getContext();

        return context.filter(c -> Objects.nonNull(c.getAuthentication()))
                .map(s -> s.getAuthentication().getPrincipal())
                .cast(RedisSessionUser.class)
                .map(e -> {
                    String userId = e.getId();
                    withdrawListVO.setUserId(userId);
                    return GlobalMessageResponseVo.newSuccessInstance(withdrawLogService.findPage(withdrawListVO));

                });
    }

    /**
     * 提现详情
     *
     * @param
     * @return
     */
//    @GetMapping("/coin/withdraw/list/{id}")
//    public Mono<GlobalMessageResponseVo> withDrawDetail(@PathVariable("id") String id) {
//
//        Mono<SecurityContext> context
//                = ReactiveSecurityContextHolder.getContext();
//
//        return context.filter(c -> Objects.nonNull(c.getAuthentication()))
//                .map(s -> s.getAuthentication().getPrincipal())
//                .cast(RedisSessionUser.class)
//                .map(e -> {
//                    String userId = e.getId();
//                    return GlobalMessageResponseVo.newSuccessInstance(withdrawLogService.findById(id));
//                });
//    }


}
