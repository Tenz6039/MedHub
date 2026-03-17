package com.medhub.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.medhub.constant.MessageConstant;
import com.medhub.constant.StatusConstant;
import com.medhub.context.BaseContext;
import com.medhub.dto.CategoryDTO;
import com.medhub.dto.CategoryPageQueryDTO;
import com.medhub.entity.Category;
import com.medhub.exception.DeletionNotAllowedException;
import com.medhub.mapper.CategoryMapper;
import com.medhub.mapper.MedicineComboMapper;
import com.medhub.mapper.MedicineMapper;
import com.medhub.result.PageResult;
import com.medhub.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private MedicineComboMapper medicineComboMapper;

    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    public void deleteById(Long id) {
        Integer countByCategoryId = medicineMapper.countByCategoryId(id);
        if(countByCategoryId > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_MEDICINE);
        }
        countByCategoryId = medicineComboMapper.countByCategoryId(id);
        if(countByCategoryId > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_MEDICINE_COMBO);
        }

        categoryMapper.deleteById(id);
    }

    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    public void startOrStop(Integer status, Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        categoryMapper.update(category);
    }

    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

}
