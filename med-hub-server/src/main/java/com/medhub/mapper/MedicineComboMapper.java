package com.medhub.mapper;

import com.github.pagehelper.Page;
import com.medhub.annotation.AutoFill;
import com.medhub.dto.MedicineComboPageQueryDTO;
import com.medhub.entity.MedicineCombo;
import com.medhub.enumeration.OperationType;
import com.medhub.vo.MedicineItemVO;
import com.medhub.vo.MedicineComboVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicineComboMapper {

    @Select("select count(id) from medicine_combo where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    List<MedicineCombo> list(MedicineCombo medicineCombo);

    List<MedicineItemVO> getMedicineItemByMedicineComboId(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(MedicineCombo medicineCombo);

    Page<MedicineComboVO> pageQuery(MedicineComboPageQueryDTO medicineComboPageQueryDTO);

    @Select("select * from medicine_combo where id = #{id}")
    MedicineCombo getById(Long id);

    @Delete("delete from medicine_combo where id = #{id}")
    void deleteById(Long medicineComboId);

    @AutoFill(OperationType.UPDATE)
    void update(MedicineCombo medicineCombo);
}
