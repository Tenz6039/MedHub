package com.medhub.controller.admin;

import com.medhub.dto.MedicineComboDTO;
import com.medhub.dto.MedicineComboPageQueryDTO;
import com.medhub.result.PageResult;
import com.medhub.result.Result;
import com.medhub.service.MedicineComboService;
import com.medhub.vo.MedicineComboVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/medicineCombo")
@Api(tags = "药品组合相关接口")
@Slf4j
public class MedicineComboController {

    @Autowired
    private MedicineComboService medicineComboService;

    @PostMapping
    @ApiOperation("新增药品组合")
    @CacheEvict(cacheNames = "medicineComboCache", key = "#medicineComboDTO.categoryId")
    public Result save(@RequestBody MedicineComboDTO medicineComboDTO) {
        log.info("新增药品组合：{}", medicineComboDTO);
        medicineComboService.saveWithMedicine(medicineComboDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(MedicineComboPageQueryDTO medicineComboPageQueryDTO) {
        log.info("药品组合分页查询：{}", medicineComboPageQueryDTO);
        PageResult pageResult = medicineComboService.pageQuery(medicineComboPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除药品组合")
    @CacheEvict(cacheNames = "medicineComboCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除药品组合：{}", ids);
        medicineComboService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询药品组合")
    public Result<MedicineComboVO> getById(@PathVariable Long id) {
        log.info("根据id查询药品组合：{}", id);
        MedicineComboVO medicineComboVO = medicineComboService.getByIdWithMedicine(id);
        return Result.success(medicineComboVO);
    }

    @PutMapping
    @ApiOperation("修改药品组合")
    @CacheEvict(cacheNames = "medicineComboCache", allEntries = true)
    public Result update(@RequestBody MedicineComboDTO medicineComboDTO) {
        log.info("修改药品组合：{}", medicineComboDTO);
        medicineComboService.update(medicineComboDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("药品组合起售停售")
    @CacheEvict(cacheNames = "medicineComboCache", allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("药品组合起售停售：{}，{}", status, id);
        medicineComboService.startOrStop(status, id);
        return Result.success();
    }
}
