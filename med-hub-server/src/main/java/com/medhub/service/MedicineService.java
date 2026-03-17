package com.medhub.service;

import com.medhub.dto.MedicineDTO;
import com.medhub.dto.MedicinePageQueryDTO;
import com.medhub.entity.Medicine;
import com.medhub.result.PageResult;
import com.medhub.vo.MedicineVO;

import java.util.List;

public interface MedicineService {
    void saveWithSpecs(MedicineDTO medicineDTO);

    PageResult pageQuery(MedicinePageQueryDTO medicinePageQueryDTO);

    void deleteBatch(List<Long> ids);

    MedicineVO getByIdWithSpecs(Long id);

    void updateWithSpecs(MedicineDTO medicineDTO);

    List<MedicineVO> listWithSpec(Medicine medicine);

    void updateStatus(Integer status, List<Long> ids);

    List<Medicine> list(Long categoryId);
}
