package com.medhub.controller.user;

import com.medhub.constant.StatusConstant;
import com.medhub.entity.MedicineCombo;
import com.medhub.result.Result;
import com.medhub.service.MedicineComboService;
import com.medhub.vo.MedicineItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@RestController("userMedicineComboController")
@RequestMapping("/user/medicineCombo")
@Api(tags = "C端-药品组合浏览接口")
public class MedicineComboController {
    @Autowired
    private MedicineComboService medicineComboService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询药品组合")
    @Cacheable(cacheNames = "medicineComboCache", key = "#categoryId")
    public Result<List<MedicineCombo>> list(Long categoryId) {
        MedicineCombo medicineCombo = new MedicineCombo();
        medicineCombo.setCategoryId(categoryId);
        medicineCombo.setStatus(StatusConstant.ENABLE);

        List<MedicineCombo> list = medicineComboService.list(medicineCombo);
        return Result.success(list);
    }

    @GetMapping("/medicine/{id}")
    @ApiOperation("根据药品组合id查询包含的药品列表")
    public Result<List<MedicineItemVO>> medicineList(@PathVariable("id") Long id) {
        List<MedicineItemVO> list = medicineComboService.getMedicineItemById(id);
        return Result.success(list);
    }
}
