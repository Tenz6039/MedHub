package com.medhub.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.medhub.constant.MessageConstant;
import com.medhub.constant.StatusConstant;
import com.medhub.dto.MedicineComboDTO;
import com.medhub.dto.MedicineComboPageQueryDTO;
import com.medhub.entity.Medicine;
import com.medhub.entity.MedicineCombo;
import com.medhub.entity.MedicineComboMedicine;
import com.medhub.exception.DeletionNotAllowedException;
import com.medhub.exception.MedicineComboEnableFailedException;
import com.medhub.mapper.MedicineComboMapper;
import com.medhub.mapper.MedicineComboMedicineMapper;
import com.medhub.mapper.MedicineMapper;
import com.medhub.result.PageResult;
import com.medhub.service.MedicineComboService;
import com.medhub.vo.MedicineComboVO;
import com.medhub.vo.MedicineItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MedicineComboServiceImpl implements MedicineComboService {

    @Autowired
    private MedicineComboMapper medicineComboMapper;
    @Autowired
    private MedicineComboMedicineMapper medicineComboMedicineMapper;
    @Autowired
    private MedicineMapper medicineMapper;

    public List<MedicineCombo> list(MedicineCombo medicineCombo) {
        List<MedicineCombo> list = medicineComboMapper.list(medicineCombo);
        return list;
    }

    public List<MedicineItemVO> getMedicineItemById(Long id) {
        return medicineComboMapper.getMedicineItemByMedicineComboId(id);
    }

    @Transactional
    public void saveWithMedicine(MedicineComboDTO medicineComboDTO) {
        MedicineCombo medicineCombo = new MedicineCombo();
        BeanUtils.copyProperties(medicineComboDTO, medicineCombo);

        medicineComboMapper.insert(medicineCombo);

        Long medicineComboId = medicineCombo.getId();

        List<MedicineComboMedicine> medicineComboMedicines = medicineComboDTO.getMedicineComboMedicines();
        if (medicineComboMedicines != null && !medicineComboMedicines.isEmpty()) {
            medicineComboMedicines.forEach(medicineComboMedicine -> {
                medicineComboMedicine.setComboId(medicineComboId);
            });

            medicineComboMedicineMapper.insertBatch(medicineComboMedicines);
        }
    }

    public PageResult pageQuery(MedicineComboPageQueryDTO medicineComboPageQueryDTO) {
        int pageNum = medicineComboPageQueryDTO.getPage();
        int pageSize = medicineComboPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<MedicineComboVO> page = medicineComboMapper.pageQuery(medicineComboPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            MedicineCombo medicineCombo = medicineComboMapper.getById(id);
            if(StatusConstant.ENABLE == medicineCombo.getStatus()){
                throw new DeletionNotAllowedException(MessageConstant.MEDICINE_COMBO_ON_SALE);
            }
        });

        ids.forEach(medicineComboId -> {
            medicineComboMapper.deleteById(medicineComboId);
            medicineComboMedicineMapper.deleteByMedicineComboId(medicineComboId);
        });
    }

    public MedicineComboVO getByIdWithMedicine(Long id) {
        MedicineCombo medicineCombo = medicineComboMapper.getById(id);
        List<MedicineComboMedicine> medicineComboMedicines = medicineComboMedicineMapper.getByMedicineComboId(id);

        MedicineComboVO medicineComboVO = new MedicineComboVO();
        BeanUtils.copyProperties(medicineCombo, medicineComboVO);
        medicineComboVO.setMedicineComboMedicines(medicineComboMedicines);

        return medicineComboVO;
    }

    @Transactional
    public void update(MedicineComboDTO medicineComboDTO) {
        MedicineCombo medicineCombo = new MedicineCombo();
        BeanUtils.copyProperties(medicineComboDTO, medicineCombo);

        medicineComboMapper.update(medicineCombo);

        Long medicineComboId = medicineComboDTO.getId();

        medicineComboMedicineMapper.deleteByMedicineComboId(medicineComboId);

        List<MedicineComboMedicine> medicineComboMedicines = medicineComboDTO.getMedicineComboMedicines();
        if (medicineComboMedicines != null && !medicineComboMedicines.isEmpty()) {
            medicineComboMedicines.forEach(medicineComboMedicine -> {
                medicineComboMedicine.setComboId(medicineComboId);
            });
            medicineComboMedicineMapper.insertBatch(medicineComboMedicines);
        }
    }

    public void startOrStop(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            List<Medicine> medicineList = medicineMapper.getByMedicineComboId(id);
            if(medicineList != null && medicineList.size() > 0){
                medicineList.forEach(medicine -> {
                    if(StatusConstant.DISABLE == medicine.getStatus()){
                        throw new MedicineComboEnableFailedException(MessageConstant.MEDICINE_COMBO_ENABLE_FAILED);
                    }
                });
            }
        }

        MedicineCombo medicineCombo = MedicineCombo.builder()
                .id(id)
                .status(status)
                .build();
        medicineComboMapper.update(medicineCombo);
    }
}
