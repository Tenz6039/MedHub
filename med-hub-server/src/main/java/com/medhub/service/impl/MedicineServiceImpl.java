package com.medhub.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.medhub.constant.MessageConstant;
import com.medhub.constant.StatusConstant;
import com.medhub.context.BaseContext;
import com.medhub.dto.MedicineDTO;
import com.medhub.dto.MedicinePageQueryDTO;
import com.medhub.entity.Medicine;
import com.medhub.entity.MedicineSpec;
import com.medhub.exception.DeletionNotAllowedException;
import com.medhub.exception.MedicineNotFoundException;
import com.medhub.mapper.MedicineComboMedicineMapper;
import com.medhub.mapper.MedicineMapper;
import com.medhub.mapper.MedicineSpecMapper;
import com.medhub.result.PageResult;
import com.medhub.service.MedicineService;
import com.medhub.vo.MedicineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MedicineServiceImpl implements MedicineService {

    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private MedicineSpecMapper medicineSpecMapper;
    @Autowired
    private MedicineComboMedicineMapper medicineComboMedicineMapper;

    @Transactional
    public void saveWithSpecs(MedicineDTO medicineDTO) {
        Medicine medicine = new Medicine();
        BeanUtils.copyProperties(medicineDTO, medicine);
        medicineMapper.insert(medicine);

        Long medicineId = medicine.getId();
        List<MedicineSpec> specs = medicineDTO.getSpecs();
        if (CollectionUtils.isNotEmpty(specs)) {
            specs.forEach(spec -> spec.setMedicineId(medicineId));
            medicineSpecMapper.insertBath(specs);
        }
    }

    @Override
    public PageResult pageQuery(MedicinePageQueryDTO medicinePageQueryDTO) {
        PageHelper.startPage(medicinePageQueryDTO.getPage(), medicinePageQueryDTO.getPageSize());
        Page<MedicineVO> page = medicineMapper.pageQuery(medicinePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
       for (Long id : ids) {
           Medicine medicine = medicineMapper.getById(id);
           if(medicine.getStatus() == StatusConstant.ENABLE){
               throw new DeletionNotAllowedException(MessageConstant.MEDICINE_ON_SALE);
           }
       }

        List<Long> medicineComboIds = medicineComboMedicineMapper.getMedicineComboId(ids);
        if(CollectionUtils.isNotEmpty(medicineComboIds)){
            throw new DeletionNotAllowedException(MessageConstant.MEDICINE_BE_RELATED_BY_MEDICINE_COMBO);
        }

        medicineMapper.deleteBatch(ids);

        medicineSpecMapper.deleteByMedicineIds(ids);
    }

    @Override
    public MedicineVO getByIdWithSpecs(Long id) {
        Medicine medicine = medicineMapper.getById(id);
        if (medicine == null) {
            throw new MedicineNotFoundException(MessageConstant.MEDICINE_NOT_FOUND);
        }
        List<MedicineSpec> specs = medicineSpecMapper.getByMedicineId(id);
        MedicineVO medicineVO = new MedicineVO();
        BeanUtils.copyProperties(medicine, medicineVO);
        medicineVO.setSpecs(specs);
        return medicineVO;
    }

    @Override
    @Transactional
    public void updateWithSpecs(MedicineDTO medicineDTO) {
        Medicine medicine = new Medicine();
        BeanUtils.copyProperties(medicineDTO, medicine);
        medicineMapper.update(medicine);

        medicineSpecMapper.deleteByMedicineIds(Collections.singletonList(medicineDTO.getId()));

        List<MedicineSpec> specs = medicineDTO.getSpecs();
        if (CollectionUtils.isNotEmpty(specs)) {
            specs.forEach(spec -> spec.setMedicineId(medicineDTO.getId()));
            medicineSpecMapper.insertBath(specs);
        }
    }

    @Override
    public List<MedicineVO> listWithSpec(Medicine medicine) {
        List<Medicine> medicineList = medicineMapper.list(medicine);

        List<MedicineVO> medicineVOList = new ArrayList<>();

        if (medicineList != null && !medicineList.isEmpty()) {
            for (Medicine m : medicineList) {
                MedicineVO medicineVO = new MedicineVO();
                BeanUtils.copyProperties(m,medicineVO);

                List<MedicineSpec> specs = medicineSpecMapper.getByMedicineId(m.getId());

                medicineVO.setSpecs(specs);
                medicineVOList.add(medicineVO);
            }
        }

        return medicineVOList;
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        ids.forEach(id -> medicineMapper.updateStatus(status, id, now, currentId));
    }

    @Override
    public List<Medicine> list(Long categoryId) {
        Medicine medicine = Medicine.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return medicineMapper.list(medicine);
    }
}
