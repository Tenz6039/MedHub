package com.medhub.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.medhub.constant.CouponConstant;
import com.medhub.context.BaseContext;
import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.dto.UserCouponQueryDTO;
import com.medhub.entity.Coupon;
import com.medhub.entity.UserCoupon;
import com.medhub.exception.CouponBusinessException;
import com.medhub.exception.CouponConditionNotMetException;
import com.medhub.exception.CouponInvalidException;
import com.medhub.exception.CouponNotFoundException;
import com.medhub.mapper.CouponMapper;
import com.medhub.mapper.UserCouponMapper;
import com.medhub.result.PageResult;
import com.medhub.service.UserCouponService;
import com.medhub.util.CouponConvertUtil;
import com.medhub.util.UserCouponConvertUtil;
import com.medhub.vo.CouponApplyResultVO;
import com.medhub.vo.CouponValidateResultVO;
import com.medhub.vo.OrderAmountCalculateVO;
import com.medhub.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户优惠券 Service 实现类
 */
@Service
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    /**
     * 查询可领取的优惠券列表
     * @param couponPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    @Override
    public PageResult availableList(CouponPageQueryDTO couponPageQueryDTO) {
        log.info("查询可领取的优惠券列表：{}", couponPageQueryDTO);

        // 1. 设置默认状态为启用
        couponPageQueryDTO.setStatus(CouponConstant.COUPON_STATUS_ENABLED);

        // 2. 分页查询
        PageHelper.startPage(couponPageQueryDTO.getPage(), couponPageQueryDTO.getPageSize());
        Page<Coupon> page = (Page<Coupon>) couponMapper.pageQueryAvailable(couponPageQueryDTO);

        // 3. 转换为 CouponVO（可领取的是优惠券信息，不是用户优惠券）
        List<com.medhub.vo.CouponVO> voList = new ArrayList<>();
        for (Coupon coupon : page.getResult()) {
            com.medhub.vo.CouponVO vo = com.medhub.util.CouponConvertUtil.toCouponVO(coupon);
            voList.add(vo);
        }

        // 4. 返回分页结果
        return new PageResult(page.getTotal(), voList);
    }

    /**
     * 领取优惠券
     * @param couponId 优惠券 ID
     * @return 用户优惠券 ID
     */
    @Override
    @Transactional
    public Long obtain(Long couponId) {
        log.info("用户领取优惠券：couponId={}", couponId);

        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询优惠券信息
        Coupon coupon = couponMapper.getById(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }

        // 3. 校验优惠券状态
        if (!CouponConstant.COUPON_STATUS_ENABLED.equals(coupon.getStatus())) {
            throw new CouponBusinessException("优惠券已禁用");
        }

        // 4. 校验有效期
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new CouponBusinessException("优惠券还未开始领取");
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new CouponBusinessException("优惠券已过期");
        }

        //5. 校验库存
        if (!CouponConstant.UNLIMITED_COUNT.equals(coupon.getTotalCount())) {
            Integer issuedCount = coupon.getIssuedCount() != null ? coupon.getIssuedCount() : 0;
            if (issuedCount >= coupon.getTotalCount()) {
                throw new CouponBusinessException("优惠券已领完");
            }
        }

        //6. 校验用户限领数量
        if (!CouponConstant.UNLIMITED_COUNT.equals(coupon.getUserLimit())) {
            Integer obtainedCount = userCouponMapper.countUserObtained(userId, couponId);
            if (obtainedCount != null && obtainedCount >= coupon.getUserLimit()) {
                throw new CouponBusinessException("已达到用户限领数量");
            }
        }

        // 7. 创建用户优惠券
        UserCoupon userCoupon = UserCoupon.builder()
                .couponId(couponId)
                .userId(userId)
                .status(CouponConstant.USER_COUPON_STATUS_UNUSED)
                .obtainTime(now)
                .expireTime(coupon.getEndTime())
                .build();

        // 8. 插入数据库
        userCouponMapper.insert(userCoupon);

        // 9. 更新优惠券已领取数量
        couponMapper.updateIssuedCount(couponId);

        log.info("用户领取优惠券成功：userId={}, couponId={}, userCouponId={}", userId, couponId, userCoupon.getId());
        return userCoupon.getId();
    }

    /**
     * 查询我的优惠券列表
     * @param userCouponQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult myCoupons(UserCouponQueryDTO userCouponQueryDTO) {
        log.info("查询我的优惠券列表：{}", userCouponQueryDTO);

        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();

        // 2. 分页查询（已关联优惠券表）
        PageHelper.startPage(userCouponQueryDTO.getPage(), userCouponQueryDTO.getPageSize());
        Page<UserCoupon> page = (Page<UserCoupon>) userCouponMapper.pageQuery(userId, userCouponQueryDTO);

        // 3. 转换为 VO（包含优惠券详细信息）
        List<UserCouponVO> voList = new ArrayList<>();
        for (UserCoupon userCoupon : page.getResult()) {
            UserCouponVO vo = UserCouponConvertUtil.toUserCouponVO(
                userCoupon,
                userCoupon.getCouponName(),
                userCoupon.getCouponType(),
                userCoupon.getMinAmount(),
                userCoupon.getDiscountValue(),
                userCoupon.getMaxDiscountAmount(),
                userCoupon.getDescription()
            );
            // 设置用户限领数量和结束时间
            vo.setUserLimit(userCoupon.getUserLimit());
            vo.setEndTime(userCoupon.getEndTime());
            voList.add(vo);
        }

        // 4. 返回分页结果
        return new PageResult(page.getTotal(), voList);
    }

    /**
     * 查询下单可用优惠券列表
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    @Override
    public List<UserCouponVO> getAvailableForOrder(BigDecimal orderAmount) {
        log.info("查询下单可用优惠券列表：orderAmount={}", orderAmount);

        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询可用优惠券（关联查询优惠券表）
        List<UserCoupon> userCoupons = userCouponMapper.getAvailableForOrder(userId, orderAmount);

        // 3. 转换为 VO 并计算优惠金额
        List<UserCouponVO> result = new ArrayList<>();
        for (UserCoupon userCoupon : userCoupons) {
            // 从 Mapper 查询结果中获取优惠券信息（通过 resultMap 映射）
            // 这里需要扩展 UserCoupon 实体或使用自定义 ResultMap
            // 简化处理：直接查询优惠券表获取信息
            Coupon coupon = couponMapper.getById(userCoupon.getCouponId());
            if (coupon != null) {
                UserCouponVO vo = UserCouponVO.builder()
                        .userCouponId(userCoupon.getId())
                        .couponId(userCoupon.getCouponId())
                        .name(coupon.getName())
                        .type(coupon.getType())
                        .typeDescription(CouponConvertUtil.getTypeDescription(coupon.getType()))
                        .minAmount(coupon.getMinAmount())
                        .discountValue(coupon.getDiscountValue())
                        .maxDiscountAmount(coupon.getMaxDiscountAmount())
                        .description(coupon.getDescription())
                        .status(userCoupon.getStatus())
                        .statusDescription(CouponConvertUtil.getUserCouponStatusDescription(userCoupon.getStatus()))
                        .obtainTime(userCoupon.getObtainTime())
                        .useTime(userCoupon.getUseTime())
                        .orderId(userCoupon.getOrderId())
                        .expireTime(userCoupon.getExpireTime())
                        .build();

                // 计算优惠金额
                BigDecimal discountAmount = CouponConvertUtil.calculateDiscountAmount(
                        coupon.getType(),
                        coupon.getDiscountValue(),
                        coupon.getMinAmount(),
                        coupon.getMaxDiscountAmount(),
                        orderAmount
                );
                vo.setDiscountAmount(discountAmount);

                // 计算使用优惠券后预估金额
                vo.setActualAmount(orderAmount.subtract(discountAmount));

                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 验证优惠券有效性
     * @param userCouponId 用户优惠券 ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    @Override
    public CouponValidateResultVO validateCoupon(Long userCouponId, BigDecimal orderAmount) {
        log.info("验证优惠券有效性：userCouponId={}, orderAmount={}", userCouponId, orderAmount);

        // 1. 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.getById(userCouponId);
        if (userCoupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }

        // 2. 验证优惠券所有权
        Long currentUserId = BaseContext.getCurrentId();
        if (!userCoupon.getUserId().equals(currentUserId)) {
            throw new CouponInvalidException("优惠券不属于当前用户");
        }

        // 3. 查询优惠券模板信息
        Coupon coupon = couponMapper.getById(userCoupon.getCouponId());
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券模板不存在");
        }

        // 4. 构建验证结果
        CouponValidateResultVO result = CouponValidateResultVO.builder()
                .userCouponId(userCouponId)
                .couponId(userCoupon.getCouponId())
                .couponName(coupon.getName())
                .couponType(coupon.getType())
                .typeDescription(CouponConvertUtil.getTypeDescription(coupon.getType()))
                .ruleDescription(buildRuleDescription(coupon))
                .minAmount(coupon.getMinAmount())
                .discountValue(coupon.getDiscountValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .status(userCoupon.getStatus())
                .statusDescription(CouponConvertUtil.getUserCouponStatusDescription(userCoupon.getStatus()))
                .startTime(coupon.getStartTime())
                .endTime(coupon.getEndTime())
                .build();

        // 5. 验证优惠券状态
        if (!CouponConstant.USER_COUPON_STATUS_UNUSED.equals(userCoupon.getStatus())) {
            result.setValid(false);
            result.setInvalidReason("优惠券已" + result.getStatusDescription());
            return result;
        }

        // 6. 验证有效期
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            result.setValid(false);
            result.setInvalidReason("优惠券已过期");
            return result;
        }

        // 7. 检查是否即将过期（7 天内）
        long daysUntilExpire = java.time.Duration.between(now, coupon.getEndTime()).toDays();
        result.setExpiringSoon(daysUntilExpire <= 7);

        // 8. 验证订单金额是否满足使用条件
        if (coupon.getType() == 1 && coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (orderAmount == null || orderAmount.compareTo(coupon.getMinAmount()) < 0) {
                result.setValid(false);
                result.setInvalidReason(String.format("订单金额满%s元可用", coupon.getMinAmount()));
                return result;
            }
        }

        // 9. 所有验证通过
        result.setValid(true);
        return result;
    }

    /**
     * 计算订单金额（使用优惠券）
     * @param orderAmount 订单商品总金额
     * @param userCouponId 用户优惠券 ID
     * @return 订单金额计算结果
     */
    @Override
    public OrderAmountCalculateVO calculateOrderAmount(BigDecimal orderAmount, Long userCouponId) {
        log.info("开始计算订单金额：orderAmount={}, userCouponId={}", orderAmount, userCouponId);

        // 1. 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.getById(userCouponId);
        if (userCoupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }

        // 2. 验证优惠券所有权
        Long currentUserId = BaseContext.getCurrentId();
        if (!userCoupon.getUserId().equals(currentUserId)) {
            throw new CouponInvalidException("优惠券不属于当前用户");
        }

        // 3. 验证优惠券状态
        if (!CouponConstant.USER_COUPON_STATUS_UNUSED.equals(userCoupon.getStatus())) {
            throw new CouponInvalidException("优惠券已使用");
        }

        // 4. 验证优惠券有效期
        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = couponMapper.getById(userCoupon.getCouponId());
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券模板不存在");
        }

        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new CouponInvalidException("优惠券已过期");
        }

        // 5. 验证优惠券使用条件
        if (coupon.getType() == 1 && coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (orderAmount.compareTo(coupon.getMinAmount()) < 0) {
                throw new CouponConditionNotMetException(
                    String.format("订单金额满%s元可用", coupon.getMinAmount())
                );
            }
        }

        // 6. 计算优惠金额
        BigDecimal discountAmount = calculateDiscountAmount(coupon, orderAmount);

        // 7. 计算最终金额（这里假设配送费和打包费为 0，实际可从订单获取）
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal packageFee = BigDecimal.ZERO;
        BigDecimal totalAmount = orderAmount.add(deliveryFee).add(packageFee);
        BigDecimal payAmount = totalAmount.subtract(discountAmount);

        // 8. 构建返回结果
        OrderAmountCalculateVO result = OrderAmountCalculateVO.builder()
                .goodsAmount(orderAmount)
                .deliveryFee(deliveryFee)
                .packageFee(packageFee)
                .totalAmount(totalAmount)
                .userCouponId(userCouponId)
                .couponName(coupon.getName())
                .couponType(coupon.getType())
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .calculationDescription(buildCalculationDescription(coupon, orderAmount, discountAmount))
                .build();

        log.info("订单金额计算完成：discountAmount={}, payAmount={}", discountAmount, payAmount);
        return result;
    }

    /**
     * 应用优惠券到订单
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     * @param orderAmount 订单金额
     * @return 应用结果
     */
    @Override
    public CouponApplyResultVO applyCoupon(Long userCouponId, Long orderId, BigDecimal orderAmount) {
        log.info("应用优惠券到订单：userCouponId={}, orderId={}, orderAmount={}", userCouponId, orderId, orderAmount);

        try {
            // 1. 验证优惠券有效性
            CouponValidateResultVO validateResult = validateCoupon(userCouponId, orderAmount);
            if (!validateResult.getValid()) {
                return CouponApplyResultVO.builder()
                        .success(false)
                        .userCouponId(userCouponId)
                        .failReason(validateResult.getInvalidReason())
                        .errorCode("COUPON_INVALID")
                        .build();
            }

            // 2. 查询优惠券信息
            UserCoupon userCoupon = userCouponMapper.getById(userCouponId);
            Coupon coupon = couponMapper.getById(userCoupon.getCouponId());

            // 3. 计算优惠金额
            BigDecimal discountAmount = calculateDiscountAmount(coupon, orderAmount);

            // 4. 计算最终金额
            BigDecimal finalAmount = orderAmount.subtract(discountAmount);

            // 5. 锁定优惠券（修改状态为已锁定）
            userCoupon.setStatus(CouponConstant.USER_COUPON_STATUS_LOCKED);
            userCoupon.setOrderId(orderId);
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.update(userCoupon);

            log.info("优惠券应用成功：userCouponId={}, orderId={}, discountAmount={}", userCouponId, orderId, discountAmount);

            // 6. 返回成功结果
            return CouponApplyResultVO.builder()
                    .success(true)
                    .userCouponId(userCouponId)
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .couponType(coupon.getType())
                    .originalAmount(orderAmount)
                    .discountAmount(discountAmount)
                    .finalAmount(finalAmount)
                    .discountDescription(buildCalculationDescription(coupon, orderAmount, discountAmount))
                    .locked(true)
                    .lockTime(LocalDateTime.now())
                    .build();

        } catch (CouponNotFoundException e) {
            log.error("优惠券不存在：userCouponId={}", userCouponId, e);
            return CouponApplyResultVO.builder()
                    .success(false)
                    .userCouponId(userCouponId)
                    .failReason("优惠券不存在")
                    .errorCode("COUPON_NOT_FOUND")
                    .build();
        } catch (CouponInvalidException e) {
            log.error("优惠券无效：userCouponId={}", userCouponId, e);
            return CouponApplyResultVO.builder()
                    .success(false)
                    .userCouponId(userCouponId)
                    .failReason(e.getMessage())
                    .errorCode("COUPON_INVALID")
                    .build();
        } catch (CouponConditionNotMetException e) {
            log.error("优惠券使用条件不满足：userCouponId={}", userCouponId, e);
            return CouponApplyResultVO.builder()
                    .success(false)
                    .userCouponId(userCouponId)
                    .failReason(e.getMessage())
                    .errorCode("CONDITION_NOT_MET")
                    .build();
        } catch (Exception e) {
            log.error("优惠券应用失败：userCouponId={}", userCouponId, e);
            return CouponApplyResultVO.builder()
                    .success(false)
                    .userCouponId(userCouponId)
                    .failReason("系统异常，请稍后重试")
                    .errorCode("SYSTEM_ERROR")
                    .build();
        }
    }

    /**
     * 取消优惠券应用（解锁优惠券）
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     */
    @Override
    public void cancelCouponApply(Long userCouponId, Long orderId) {
        log.info("取消优惠券应用：userCouponId={}, orderId={}", userCouponId, orderId);

        // 1. 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.getById(userCouponId);
        if (userCoupon == null) {
            log.warn("优惠券不存在：userCouponId={}", userCouponId);
            return;
        }

        // 2. 验证优惠券是否被该订单锁定
        if (!CouponConstant.USER_COUPON_STATUS_LOCKED.equals(userCoupon.getStatus())) {
            log.warn("优惠券未锁定：userCouponId={}, status={}", userCouponId, userCoupon.getStatus());
            return;
        }

        if (!orderId.equals(userCoupon.getOrderId())) {
            log.warn("优惠券未被该订单锁定：userCouponId={}, orderId={}, lockedOrderId={}", 
                    userCouponId, orderId, userCoupon.getOrderId());
            return;
        }

        // 3. 解锁优惠券（恢复为未使用状态）
        userCoupon.setStatus(CouponConstant.USER_COUPON_STATUS_UNUSED);
        userCoupon.setOrderId(null);
        userCoupon.setUpdateTime(LocalDateTime.now());
        userCouponMapper.update(userCoupon);

        log.info("优惠券解锁成功：userCouponId={}", userCouponId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算优惠金额（核心算法）
     */
    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal goodsAmount) {
        BigDecimal discountAmount;

        if (coupon.getType() == 1) {
            // 满减券
            if (goodsAmount.compareTo(coupon.getMinAmount()) < 0) {
                discountAmount = BigDecimal.ZERO;
            } else {
                discountAmount = coupon.getDiscountValue().min(goodsAmount);
            }
        } else if (coupon.getType() == 2) {
            // 打折券
            discountAmount = goodsAmount.multiply(
                BigDecimal.ONE.subtract(coupon.getDiscountValue())
            );

            if (coupon.getMaxDiscountAmount() != null) {
                discountAmount = discountAmount.min(coupon.getMaxDiscountAmount());
            }

            discountAmount = discountAmount.min(goodsAmount);
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        return discountAmount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 构建优惠券规则描述
     */
    private String buildRuleDescription(Coupon coupon) {
        if (coupon.getType() == 1) {
            // 满减券
            if (coupon.getMinAmount() == null || coupon.getMinAmount().compareTo(BigDecimal.ZERO) == 0) {
                return String.format("减%s元", coupon.getDiscountValue());
            } else {
                return String.format("满%s减%s", coupon.getMinAmount(), coupon.getDiscountValue());
            }
        } else if (coupon.getType() == 2) {
            // 打折券
            BigDecimal discountRate = coupon.getDiscountValue().multiply(new BigDecimal("10"));
            String desc = String.format("%.1f 折", discountRate);
            if (coupon.getMaxDiscountAmount() != null) {
                desc += String.format("（最高省%s元）", coupon.getMaxDiscountAmount());
            }
            return desc;
        }
        return "";
    }

    /**
     * 构建计算说明
     */
    private String buildCalculationDescription(Coupon coupon, BigDecimal orderAmount, BigDecimal discountAmount) {
        StringBuilder sb = new StringBuilder();
        sb.append(coupon.getName()).append(": ");

        if (coupon.getType() == 1) {
            sb.append(String.format("满%s减%s", coupon.getMinAmount(), coupon.getDiscountValue()));
        } else if (coupon.getType() == 2) {
            BigDecimal discountRate = coupon.getDiscountValue().multiply(new BigDecimal("10"));
            sb.append(String.format("%.1f 折", discountRate));
            if (coupon.getMaxDiscountAmount() != null) {
                sb.append(String.format("，最高省%s元", coupon.getMaxDiscountAmount()));
            }
        }

        sb.append(String.format("，优惠%s元", discountAmount));
        return sb.toString();
    }
}
