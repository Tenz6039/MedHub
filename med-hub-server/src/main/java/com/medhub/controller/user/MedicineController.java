package com.medhub.controller.user;

import com.medhub.constant.StatusConstant;
import com.medhub.entity.Medicine;
import com.medhub.result.Result;
import com.medhub.service.MedicineService;
import com.medhub.vo.MedicineVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userMedicineController")
@RequestMapping("/user/medicine")
@Slf4j
@Api(tags = "C端-药品浏览接口")
public class MedicineController {
    @Autowired
    private MedicineService medicineService;
    @Autowired
    private RedisTemplate redisTemplate;



    @GetMapping("/list")
    @ApiOperation("根据分类id查询药品")
    public Result<List<MedicineVO>> list(Long categoryId) {
        String redisKey = "medicine_list_" + categoryId;

        List<MedicineVO> list = null;
        try {
            list = (List<MedicineVO>) redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            redisTemplate.delete(redisKey);
            log.warn("Redis缓存反序列化失败，已删除缓存: {}", redisKey);
            list = null;
        }

        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }
        Medicine medicine = new Medicine();
        medicine.setCategoryId(categoryId);
        medicine.setStatus(StatusConstant.ENABLE);

        list = medicineService.listWithSpec(medicine);
    
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(redisKey, list);
            } catch (Exception e) {
                log.error("Redis缓存失败: {}", e.getMessage());
            }
        }
        
        return Result.success(list);
    }

}
