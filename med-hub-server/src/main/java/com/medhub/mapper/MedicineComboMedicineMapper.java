package com.medhub.mapper;

import com.medhub.entity.MedicineComboMedicine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicineComboMedicineMapper {

    List<Long> getMedicineComboId(List<Long> medicineIds);

    void insertBatch(List<MedicineComboMedicine> medicineComboMedicines);

    @Delete("delete from medicine_combo_medicine where combo_id = #{medicineComboId}")
    void deleteByMedicineComboId(Long medicineComboId);

    @Select("select * from medicine_combo_medicine where combo_id = #{medicineComboId}")
    List<MedicineComboMedicine> getByMedicineComboId(Long medicineComboId);
}
