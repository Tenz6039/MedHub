package com.medhub.service.impl;


import com.medhub.context.BaseContext;
import com.medhub.dto.ShoppingCartDTO;
import com.medhub.entity.Medicine;
import com.medhub.entity.MedicineCombo;
import com.medhub.entity.ShoppingCart;
import com.medhub.mapper.MedicineComboMapper;
import com.medhub.mapper.MedicineMapper;
import com.medhub.mapper.ShoppingCartMapper;
import com.medhub.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private  ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private MedicineComboMapper medicineComboMapper;

    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
    }

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO == null) {
            throw new IllegalArgumentException("购物车数据不能为空");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        
        if (userId == null) {
            throw new IllegalStateException("用户未登录");
        }
        
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (!list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        } else {
            Long medicineId = shoppingCartDTO.getMedicineId();
            Long comboId = shoppingCartDTO.getComboId();
            
            if (medicineId != null) {
                Medicine medicine = medicineMapper.getById(medicineId);
                if (medicine == null) {
                    throw new IllegalArgumentException("药品不存在，ID: " + medicineId);
                }
                shoppingCart.setName(medicine.getName());
                shoppingCart.setAmount(medicine.getPrice());
                shoppingCart.setImage(medicine.getImage());
            } else if (comboId != null) {
                MedicineCombo medicineCombo = medicineComboMapper.getById(comboId);
                if (medicineCombo == null) {
                    throw new IllegalArgumentException("药品组合不存在，ID: " + comboId);
                }
                shoppingCart.setName(medicineCombo.getName());
                shoppingCart.setAmount(medicineCombo.getPrice());
                shoppingCart.setImage(medicineCombo.getImage());
            } else {
                throw new IllegalArgumentException("必须指定药品 ID 或药品组合 ID");
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);

            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;

    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO == null) {
            throw new IllegalArgumentException("购物车数据不能为空");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        
        if (userId == null) {
            throw new IllegalStateException("用户未登录");
        }
        
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            Integer number = cart.getNumber();
            if (number == null || number <= 1) {
                shoppingCartMapper.deleteByCartId(cart.getId());
            } else {
                cart.setNumber(number - 1);
                shoppingCartMapper.update(cart);
            }
        }
    }
}
