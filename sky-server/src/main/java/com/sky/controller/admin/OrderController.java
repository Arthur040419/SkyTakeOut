package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@Api(tags = "管理端-订单相关接口")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("条件分页查询订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("条件分页查询订单信息：{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 查询订单详细
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详细")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单详细：{}",id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 统计各个状态订单的数量
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("统计各个状态订单的数量")
    public Result<OrderStatisticsVO> orderStatistics(){
        log.info("统计各个状态订单的数量");
        OrderStatisticsVO orderStatisticsVO = orderService.orderStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 商家接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("商家接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("商家接单：{}",ordersConfirmDTO);
        orderService.confrim(ordersConfirmDTO.getId());
        return Result.success();
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("商家拒单")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("商家拒单：{}",ordersRejectionDTO.getId());
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("商家取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}",ordersCancelDTO);
        orderService.cancelByAdmin(ordersCancelDTO);
        return Result.success();
    }


    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单：{}",id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("完成订单：{}",id);
        orderService.complete(id);
        return Result.success();
    }

}
