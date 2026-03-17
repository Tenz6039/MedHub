package com.medhub.mapper;

import com.github.pagehelper.Page;
import com.medhub.annotation.AutoFill;
import com.medhub.dto.MedicinePageQueryDTO;
import com.medhub.entity.Medicine;
import com.medhub.enumeration.OperationType;
import com.medhub.vo.MedicineVO;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface MedicineMapper {

    @Select("select count(id) from medicine where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

   @Insert(value = "insert into medicine (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
           "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
   @AutoFill(value = OperationType.INSERT)
   @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
   void insert(Medicine medicine);

    Page<MedicineVO> pageQuery(MedicinePageQueryDTO medicinePageQueryDTO);

    @Select("select * from medicine where id = #{id}")
    Medicine getById(Long id);

    void deleteBatch(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Medicine medicine);

    List<Medicine> list(Medicine medicine);

     @Update("update medicine set status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void updateStatus(Integer status, Long id, LocalDateTime updateTime, Long updateUser);

    @Select("select a.* from medicine a left join medicine_combo_medicine b on a.id = b.medicine_id where b.combo_id = #{medicineComboId}")
    List<Medicine> getByMedicineComboId(Long medicineComboId);
}
