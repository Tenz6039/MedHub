package com.medhub.service;

import com.medhub.dto.EmployeeDTO;
import com.medhub.dto.EmployeeLoginDTO;
import com.medhub.dto.EmployeePageQueryDTO;
import com.medhub.entity.Employee;
import com.medhub.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 更新员工状态
     *
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据id查找员工
     *
     * @param id
     * @return
     */
    Employee getById(Long id);

     /**
      * 更新员工信息
      *
      * @param employeeDTO
      */
    void update(EmployeeDTO employeeDTO);
}
