package com.medhub.mapper;

import com.medhub.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    @Select("select date(order_time) as date, sum(amount) as amount from orders " +
            "where order_time between #{begin} and #{end} and status = 5 " +
            "group by date(order_time) order by date")
    List<Map<String, Object>> getTurnoverStatistics(LocalDate begin, LocalDate end);

    @Select("select date(create_time) as date, count(*) as count from user " +
            "where create_time between #{begin} and #{end} " +
            "group by date(create_time) order by date")
    List<Map<String, Object>> getNewUserStatistics(LocalDate begin, LocalDate end);

    @Select("select date(create_time) as date, count(*) as count from user " +
            "where create_time <= #{end} " +
            "group by date(create_time) order by date")
    List<Map<String, Object>> getTotalUserStatistics(LocalDate end);

    @Select("select date(order_time) as date, count(*) as count from orders " +
            "where order_time between #{begin} and #{end} " +
            "group by date(order_time) order by date")
    List<Map<String, Object>> getOrderCountStatistics(LocalDate begin, LocalDate end);

    @Select("select date(order_time) as date, count(*) as count from orders " +
            "where order_time between #{begin} and #{end} and status = 5 " +
            "group by date(order_time) order by date")
    List<Map<String, Object>> getValidOrderCountStatistics(LocalDate begin, LocalDate end);

    @Select("select count(*) from orders where order_time between #{begin} and #{end}")
    Integer getTotalOrderCount(LocalDate begin, LocalDate end);

    @Select("select count(*) from orders where order_time between #{begin} and #{end} and status = 5")
    Integer getValidOrderCount(LocalDate begin, LocalDate end);

    List<GoodsSalesDTO> getSalesTop10(LocalDate begin, LocalDate end);
}
