package com.java_db.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单创建 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "车辆ID不能为空")
    private Integer vehicleId;
    
    @NotNull(message = "取车门店ID不能为空")
    private Integer pickupStoreId;
    
    @NotNull(message = "还车门店ID不能为空")
    private Integer returnStoreId;
    
    @NotNull(message = "租赁开始时间不能为空")
    @Future(message = "租赁开始时间必须是未来时间")
    private LocalDateTime startTime;
    
    @NotNull(message = "预计还车时间不能为空")
    @Future(message = "预计还车时间必须是未来时间")
    private LocalDateTime endTime;
}
