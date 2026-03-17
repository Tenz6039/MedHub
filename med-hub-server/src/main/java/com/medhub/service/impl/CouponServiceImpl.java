package com.medhub.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.medhub.constant.CouponConstant;
import com.medhub.dto.CouponCreateDTO;
import com.medhub.dto.CouponEditDTO;
import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.entity.Coupon;
import com.medhub.exception.CouponBusinessException;
import com.medhub.exception.CouponNotFoundException;
import com.medhub.mapper.CouponMapper;
import com.medhub.result.PageResult;
import com.medhub.service.CouponService;
import com.medhub.util.CouponConvertUtil;
import com.medhub.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券管理服务实现类
 */
@Service
@Slf4j
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponMapper couponMapper;

    /**
     * 创建优惠券
     * @param couponCreateDTO 优惠券创建 DTO
     * @return 优惠券 ID
     */
    @Override
    public Long createCoupon(CouponCreateDTO couponCreateDTO) {
        log.info("开始创建优惠券：{}", couponCreateDTO);
        
        // 1. 参数校验
        validateCouponCreateDTO(couponCreateDTO);
        
        // 2. DTO 转 Entity
        Coupon coupon = CouponConvertUtil.toCoupon(couponCreateDTO);
        
        // 3. 插入数据库（@AutoFill 会自动设置 createUser, createTime, updateTime, updateUser）
        couponMapper.insert(coupon);
        
        log.info("优惠券创建成功，ID: {}", coupon.getId());
        return coupon.getId();
    }

    /**
     * 修改优惠券
     * @param couponEditDTO 优惠券编辑 DTO
     */
    @Override
    public void updateCoupon(CouponEditDTO couponEditDTO) {
        log.info("开始修改优惠券：{}", couponEditDTO);
        
        // 1. 参数校验
        if (couponEditDTO.getId() == null) {
            throw new CouponBusinessException("优惠券 ID 不能为空");
        }
        
        // 2. 检查优惠券是否存在
        Coupon coupon = couponMapper.getById(couponEditDTO.getId());
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }
        
        // 3. 将 DTO 数据复制到 Entity
        CouponConvertUtil.updateCoupon(coupon, couponEditDTO);
        
        // 4. 校验发行总量
        if (couponEditDTO.getTotalCount() != null && couponEditDTO.getTotalCount() != -1) {
            if (couponEditDTO.getTotalCount() < coupon.getIssuedCount()) {
                throw new CouponBusinessException("发行总量不能小于已领取数量");
            }
        }
        
        // 5. 校验有效期
        if (couponEditDTO.getStartTime() != null && couponEditDTO.getEndTime() != null) {
            if (couponEditDTO.getEndTime().isBefore(couponEditDTO.getStartTime())) {
                throw new CouponBusinessException("有效期设置错误：结束时间不能早于开始时间");
            }
        }
        
        // 6. 更新优惠券（@AutoFill 会自动设置 updateTime, updateUser）
        couponMapper.update(coupon);
        
        log.info("优惠券修改成功，ID: {}", couponEditDTO.getId());
    }

    /**
     * 删除优惠券
     * @param id 优惠券 ID
     */
    @Override
    public void deleteCoupon(Long id) {
        log.info("开始删除优惠券：{}", id);
        
        //1. 检查优惠券是否存在
        Coupon coupon = couponMapper.getById(id);
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }
        
        //2. 检查是否已被领取
        if (coupon.getIssuedCount() != null && coupon.getIssuedCount() > 0) {
            throw new CouponBusinessException("优惠券已被领取，不能删除");
        }
        
        //3. 【权限校验】校验操作人权限（可选：只能删除自己创建的优惠券）
        // Long currentEmpId = BaseContext.getCurrentId();
        // if (!coupon.getCreateUser().equals(currentEmpId)) {
        //     throw new CouponBusinessException("无权删除他人创建的优惠券");
        // }
        
        //4. 逻辑删除（禁用）
        couponMapper.deleteById(id);
        
        log.info("优惠券删除成功，ID: {}", id);
    }

    /**
     * 分页查询优惠券
     * @param couponPageQueryDTO 分页查询 DTO
     * @return 分页结果
     */
    @Override
    public PageResult pageCoupon(CouponPageQueryDTO couponPageQueryDTO) {
        log.info("分页查询优惠券：{}", couponPageQueryDTO);
        
        // 1. 设置分页参数
        PageHelper.startPage(couponPageQueryDTO.getPage(), couponPageQueryDTO.getPageSize());
        
        // 2. 查询数据
        Page<Coupon> page = (Page<Coupon>) couponMapper.pageQuery(couponPageQueryDTO);
        
        // 3. 转换为 CouponVO
        List<CouponVO> records = new ArrayList<>();
        for (Coupon coupon : page.getResult()) {
            CouponVO couponVO = CouponConvertUtil.toCouponVO(coupon);
            records.add(couponVO);
        }
        
        // 4. 构建分页结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(records);
        
        log.info("分页查询成功，总数：{}", page.getTotal());
        return pageResult;
    }

    /**
     * 查询优惠券详情
     * @param id 优惠券 ID
     * @return 优惠券 VO
     */
    @Override
    public CouponVO getById(Long id) {
        log.info("查询优惠券详情：{}", id);
        
        // 1. 检查优惠券是否存在
        Coupon coupon = couponMapper.getById(id);
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }
        
        // 2. 转换为 VO
        CouponVO couponVO = CouponConvertUtil.toCouponVO(coupon);
        
        log.info("查询优惠券详情成功，ID: {}", id);
        return couponVO;
    }

    /**
     * 校验创建优惠券参数
     * @param dto 创建 DTO
     */
    private void validateCouponCreateDTO(CouponCreateDTO dto) {
        // 1. 校验有效期
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new CouponBusinessException("有效期设置错误：结束时间不能早于开始时间");
        }
        
        // 2. 校验发行总量
        if (dto.getTotalCount() != null && dto.getTotalCount() < -1) {
            throw new CouponBusinessException("发行总量设置错误");
        }
        
        // 3. 校验优惠券类型和金额
        if (CouponConstant.COUPON_TYPE_DISCOUNT.equals(dto.getType())) {
            // 满减券
            if (dto.getDiscountValue() == null || dto.getDiscountValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new CouponBusinessException("满减券优惠金额必须大于 0");
            }
        } else if (CouponConstant.COUPON_TYPE_PERCENT_OFF.equals(dto.getType())) {
            // 打折券
            if (dto.getDiscountValue() == null || dto.getDiscountValue().compareTo(java.math.BigDecimal.ZERO) <= 0 
                    || dto.getDiscountValue().compareTo(java.math.BigDecimal.ONE) > 0) {
                throw new CouponBusinessException("打折券折扣率必须在 0-1 之间");
            }
            if (dto.getMaxDiscountAmount() == null || dto.getMaxDiscountAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new CouponBusinessException("打折券必须设置最大优惠金额");
            }
        } else {
            throw new CouponBusinessException("优惠券类型错误");
        }
    }

    /**
     * 修改优惠券状态
     * @param id 优惠券 ID
     * @param status 状态：0-禁用，1-启用
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("开始修改优惠券状态：id={}, status={}", id, status);
        
        // 1. 校验状态值
        if (status == null || (status != 0 && status != 1)) {
            throw new CouponBusinessException("状态值错误，只能为 0（禁用）或 1（启用）");
        }
        
        // 2. 检查优惠券是否存在
        Coupon coupon = couponMapper.getById(id);
        if (coupon == null) {
            throw new CouponNotFoundException("优惠券不存在");
        }
        
        // 3. 更新状态
        coupon.setStatus(status);
        couponMapper.update(coupon);
        
        log.info("优惠券状态修改成功，ID: {}, 新状态: {}", id, status);
    }
}

