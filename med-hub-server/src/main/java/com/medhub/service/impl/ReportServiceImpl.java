package com.medhub.service.impl;

import com.medhub.dto.GoodsSalesDTO;
import com.medhub.mapper.ReportMapper;
import com.medhub.service.ReportService;
import com.medhub.vo.OrderReportVO;
import com.medhub.vo.SalesTop10ReportVO;
import com.medhub.vo.TurnoverReportVO;
import com.medhub.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<String> dateStrList = dateList.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .collect(Collectors.toList());

        List<Map<String, Object>> turnoverList = reportMapper.getTurnoverStatistics(dateList.get(0), dateList.get(dateList.size() - 1));

        Map<LocalDate, Double> turnoverMap = new HashMap<>();
        for (Map<String, Object> map : turnoverList) {
            java.sql.Date sqlDate = (java.sql.Date) map.get("date");
            LocalDate date = sqlDate.toLocalDate();
            Double amount = ((Number) map.get("amount")).doubleValue();
            turnoverMap.put(date, amount);
        }

        List<String> turnoverStrList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Double turnover = turnoverMap.get(date);
            if (turnover == null) {
                turnoverStrList.add("0.0");
            } else {
                turnoverStrList.add(turnover.toString());
            }
        }

        return TurnoverReportVO.builder()
                .dateList(String.join(",", dateStrList))
                .turnoverList(String.join(",", turnoverStrList))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<String> dateStrList = dateList.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .collect(Collectors.toList());

        List<Map<String, Object>> newUserList = reportMapper.getNewUserStatistics(dateList.get(0), dateList.get(dateList.size() - 1));

        Map<LocalDate, Integer> newUserMap = new HashMap<>();
        for (Map<String, Object> map : newUserList) {
            java.sql.Date sqlDate = (java.sql.Date) map.get("date");
            LocalDate date = sqlDate.toLocalDate();
            Integer count = ((Number) map.get("count")).intValue();
            newUserMap.put(date, count);
        }

        List<String> newUserStrList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer newUser = newUserMap.get(date);
            if (newUser == null) {
                newUserStrList.add("0");
            } else {
                newUserStrList.add(newUser.toString());
            }
        }

        List<Map<String, Object>> totalUserList = reportMapper.getTotalUserStatistics(dateList.get(dateList.size() - 1));

        Map<LocalDate, Integer> totalUserMap = new HashMap<>();
        for (Map<String, Object> map : totalUserList) {
            java.sql.Date sqlDate = (java.sql.Date) map.get("date");
            LocalDate date = sqlDate.toLocalDate();
            Integer count = ((Number) map.get("count")).intValue();
            totalUserMap.put(date, count);
        }

        List<String> totalUserStrList = new ArrayList<>();
        int totalUsers = 0;
        for (LocalDate date : dateList) {
            Integer newUser = newUserMap.get(date);
            if (newUser == null) {
                newUser = 0;
            }
            totalUsers += newUser;
            totalUserStrList.add(String.valueOf(totalUsers));
        }

        return UserReportVO.builder()
                .dateList(String.join(",", dateStrList))
                .newUserList(String.join(",", newUserStrList))
                .totalUserList(String.join(",", totalUserStrList))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<String> dateStrList = dateList.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .collect(Collectors.toList());

        List<Map<String, Object>> orderCountList = reportMapper.getOrderCountStatistics(dateList.get(0), dateList.get(dateList.size() - 1));

        Map<LocalDate, Integer> orderCountMap = new HashMap<>();
        for (Map<String, Object> map : orderCountList) {
            java.sql.Date sqlDate = (java.sql.Date) map.get("date");
            LocalDate date = sqlDate.toLocalDate();
            Integer count = ((Number) map.get("count")).intValue();
            orderCountMap.put(date, count);
        }

        List<String> orderCountStrList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer orderCount = orderCountMap.get(date);
            if (orderCount == null) {
                orderCountStrList.add("0");
            } else {
                orderCountStrList.add(orderCount.toString());
            }
        }

        List<Map<String, Object>> validOrderCountList = reportMapper.getValidOrderCountStatistics(dateList.get(0), dateList.get(dateList.size() - 1));

        Map<LocalDate, Integer> validOrderCountMap = new HashMap<>();
        for (Map<String, Object> map : validOrderCountList) {
            java.sql.Date sqlDate = (java.sql.Date) map.get("date");
            LocalDate date = sqlDate.toLocalDate();
            Integer count = ((Number) map.get("count")).intValue();
            validOrderCountMap.put(date, count);
        }

        List<String> validOrderCountStrList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer validOrderCount = validOrderCountMap.get(date);
            if (validOrderCount == null) {
                validOrderCountStrList.add("0");
            } else {
                validOrderCountStrList.add(validOrderCount.toString());
            }
        }

        Integer totalOrderCount = reportMapper.getTotalOrderCount(dateList.get(0), dateList.get(dateList.size() - 1));
        Integer validOrderCount = reportMapper.getValidOrderCount(dateList.get(0), dateList.get(dateList.size() - 1));

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount.doubleValue()) * 100;
        }

        return OrderReportVO.builder()
                .dateList(String.join(",", dateStrList))
                .orderCountList(String.join(",", orderCountStrList))
                .validOrderCountList(String.join(",", validOrderCountStrList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> salesTop10List = reportMapper.getSalesTop10(begin, end);

        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();

        for (GoodsSalesDTO goodsSalesDTO : salesTop10List) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(String.valueOf(goodsSalesDTO.getNumber()));
        }

        return SalesTop10ReportVO.builder()
                .nameList(String.join(",", nameList))
                .numberList(String.join(",", numberList))
                .build();
    }
}
