package com.medhub.mapper;

import com.github.pagehelper.Page;
import com.medhub.annotation.AutoFill;
import com.medhub.dto.EmployeePageQueryDTO;
import com.medhub.entity.Employee;
import com.medhub.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

     /**
     * 新增员工
     * @param employee
     */
     @AutoFill(OperationType.INSERT)
     @Insert("insert into employee(username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
             "values(#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
     void insert(Employee employee);

    /**
     * 分页查询员工
     *
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

     /**
     * 更新员工状态
     *
     * @param employee
     */

     @AutoFill(OperationType.UPDATE)
     @Update("update employee set status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void updateStatus(Employee employee);

     /**
      * 根据id查询员工
      *
      * @param id
      * @return
      */
     @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

     /**
      * 更新员工信息
      *
      * @param employee
      */
      @AutoFill(OperationType.UPDATE)
     @Update("update employee set username = #{username}, name = #{name}, phone = #{phone}, sex = #{sex}, id_number = #{idNumber}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void update(Employee employee);
}
