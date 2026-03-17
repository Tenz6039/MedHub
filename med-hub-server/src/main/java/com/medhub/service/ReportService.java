package com.medhub.service;

import com.medhub.vo.OrderReportVO;
import com.medhub.vo.SalesTop10ReportVO;
import com.medhub.vo.TurnoverReportVO;
import com.medhub.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
