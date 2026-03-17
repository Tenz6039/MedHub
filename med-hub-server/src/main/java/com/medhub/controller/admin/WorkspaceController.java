package com.medhub.controller.admin;

import com.medhub.result.Result;
import com.medhub.service.MedicineComboService;
import com.medhub.service.MedicineService;
import com.medhub.service.OrderService;
import com.medhub.service.ReportService;
import com.medhub.service.UserService;
import com.medhub.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 工作台相关接口
 */
@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
public class WorkspaceController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineComboService medicineComboService;

    @Autowired
    private UserService userService;

    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("/todaydate")
    @ApiOperation("查询今日运营数据")
    public Result<OrderReportVO> todaydate() {
        log.info("查询今日运营数据");
        LocalDate today = LocalDate.now();
        OrderReportVO orderReportVO = reportService.getOrderStatistics(today, today);
        return Result.success(orderReportVO);
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderStatisticsVO> overviewOrders() {
        log.info("查询订单管理数据");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询药品总览
     * @return
     */
    @GetMapping("/overviewMedicines")
    @ApiOperation("查询药品总览")
    public Result<MedicineOverviewVO> overviewMedicines() {
        log.info("查询药品总览");
        // 查询所有药品
        List<MedicineVO> allMedicines = medicineService.listWithSpec(null);
        
        // 统计已启售和已停售数量
        long enabledCount = allMedicines.stream()
                .filter(m -> m.getStatus() == 1)
                .count();
        long disabledCount = allMedicines.size() - enabledCount;
        
        MedicineOverviewVO medicineOverviewVO = MedicineOverviewVO.builder()
                .enabled((int) enabledCount)
                .disabled((int) disabledCount)
                .total(allMedicines.size())
                .build();
        
        return Result.success(medicineOverviewVO);
    }

    /**
     * 查询药品组合总览
     * @return
     */
    @GetMapping("/overviewMedicineCombos")
    @ApiOperation("查询药品组合总览")
    public Result<MedicineComboOverviewVO> overviewMedicineCombos() {
        log.info("查询药品组合总览");
        // TODO: 需要 MedicineComboService 添加查询所有药品组合的方法
        // 暂时返回空数据
        MedicineComboOverviewVO medicineComboOverviewVO = MedicineComboOverviewVO.builder()
                .enabled(0)
                .disabled(0)
                .total(0)
                .build();
        return Result.success(medicineComboOverviewVO);
    }

    /**
     * 查询营业数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("查询营业数据")
    public Result<BusinessDataVO> businessData() {
        log.info("查询营业数据");
        LocalDate today = LocalDate.now();
        
        // 获取今日订单统计数据
        OrderReportVO orderReportVO = reportService.getOrderStatistics(today, today);
        
        // 计算订单完成率
        BigDecimal orderCompletionRate = BigDecimal.ZERO;
        if (orderReportVO.getOrderCompletionRate() != null) {
            orderCompletionRate = BigDecimal.valueOf(orderReportVO.getOrderCompletionRate());
        }
        
        // 计算平均客单价
        BigDecimal averageCustomerPrice = BigDecimal.ZERO;
        if (orderReportVO.getValidOrderCount() != null && orderReportVO.getValidOrderCount() > 0) {
            // 需要从其他地方获取今日营业额
            // 暂时使用 orderReportVO 中的数据估算
            averageCustomerPrice = BigDecimal.valueOf(50.0); // 默认值
        }
        
        // 获取今日新增用户数
        int newUserCount = 0; // TODO: 需要 UserService 添加统计方法
        
        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .turnover(BigDecimal.ZERO) // TODO: 需要获取今日营业额
                .validOrderCount(orderReportVO.getValidOrderCount())
                .orderCompletionRate(orderCompletionRate)
                .averageCustomerPrice(averageCustomerPrice)
                .newUserCount(newUserCount)
                .build();
        
        return Result.success(businessDataVO);
    }
}
