package com.medhub.controller.admin;

import com.medhub.dto.MedicineDTO;
import com.medhub.dto.MedicinePageQueryDTO;
import com.medhub.entity.Medicine;
import com.medhub.result.PageResult;
import com.medhub.result.Result;
import com.medhub.service.MedicineService;
import com.medhub.vo.MedicineVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/medicine")
@Api(tags = "药品相关接口")
public class MedicineController {
    @Autowired
    MedicineService medicineService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增药品")
    public Result save(@RequestBody MedicineDTO medicineDTO) {
        log.info("新增药品：{}",medicineDTO);
        medicineService.saveWithSpecs(medicineDTO);
        String redisKey = "medicine_list_" + medicineDTO.getCategoryId();
        redisTemplate.delete(redisKey);
        return Result.success();
    }

     @GetMapping("/page")
     @ApiOperation("药品分页查询")
     public Result page(MedicinePageQueryDTO medicinePageQueryDTO) {
         log.info("药品分页查询：{}",medicinePageQueryDTO);
         PageResult pageResult = medicineService.pageQuery(medicinePageQueryDTO);
         return Result.success(pageResult);

     }

      @DeleteMapping
      @ApiOperation("药品删除和批量删除")
      public Result delete(@RequestParam("ids") List<Long> ids) {
          log.info("药品删除和批量删除：{}", ids);
          medicineService.deleteBatch(ids);
          deleteCache("medicine_list_*");
          return Result.success();
      }

       @GetMapping("/{id}")
       @ApiOperation("根据id查询药品")
       public Result<MedicineVO> getById(@PathVariable Long id) {
           log.info("根据id查询药品：{}", id);
           MedicineVO medicineVO = medicineService.getByIdWithSpecs(id);
           return Result.success(medicineVO);
       }

        @PutMapping
        @ApiOperation("根据id修改药品")
        public Result update(@RequestBody MedicineDTO medicineDTO) {
            log.info("根据id修改药品：{}", medicineDTO);
            medicineService.updateWithSpecs(medicineDTO);
            deleteCache("medicine_list_*");
            return Result.success();
        }

        @PostMapping("/status/{status}")
        @ApiOperation("药品起售、停售")
        public Result updateStatus(@PathVariable Integer status, @RequestParam("id") Long id) {
            log.info("药品起售、停售：{}，{}", status, id);
            medicineService.updateStatus(status, Arrays.asList(id));
            deleteCache("medicine_list_*");
            return Result.success();
        }

        private void deleteCache(String pattern) {
            Set redisKey = redisTemplate.keys(pattern);
            redisTemplate.delete(redisKey);
        }

        @GetMapping("/list")
        @ApiOperation("根据分类id查询药品")
        public Result<List<Medicine>> list(Long categoryId) {
            log.info("根据分类id查询药品：{}", categoryId);
            List<Medicine> list = medicineService.list(categoryId);
            return Result.success(list);
        }
}
