package com.medhub.service;

import com.medhub.dto.MedicineComboDTO;
import com.medhub.dto.MedicineComboPageQueryDTO;
import com.medhub.entity.MedicineCombo;
import com.medhub.result.PageResult;
import com.medhub.vo.MedicineItemVO;
import com.medhub.vo.MedicineComboVO;
import java.util.List;

public interface MedicineComboService {

    List<MedicineCombo> list(MedicineCombo medicineCombo);

    List<MedicineItemVO> getMedicineItemById(Long id);

    void saveWithMedicine(MedicineComboDTO medicineComboDTO);

    PageResult pageQuery(MedicineComboPageQueryDTO medicineComboPageQueryDTO);

    void deleteBatch(List<Long> ids);

    MedicineComboVO getByIdWithMedicine(Long id);

    void update(MedicineComboDTO medicineComboDTO);

    void startOrStop(Integer status, Long id);
}
