package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {


    /**
     * 营业额统计
     * @param start
     * @param end
     * @return
     */
    TurnoverReportVO turnoverReport(LocalDate start,LocalDate end);



    /**
     * 营业额统计
     * @param start
     * @param end
     * @return
     */
    UserReportVO userReport(LocalDate start, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersReport(LocalDate begin, LocalDate end);


    /**
     * 统计销量排名前10的菜品
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出Excel报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
