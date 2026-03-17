package com.medhub.mapper;

import com.medhub.entity.MedicineSpec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicineSpecMapper {

    void insertBath(List<MedicineSpec> specs);

    void deleteByMedicineIds(List<Long> medicineIds);

    @Select("select * from medicine_spec where medicine_id = #{id}")
    List<MedicineSpec> getByMedicineId(Long id);
}
