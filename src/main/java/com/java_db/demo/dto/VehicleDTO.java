package com.java_db.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 车辆添加/更新 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    
    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;
    
    @NotBlank(message = "车型不能为空")
    private String model;
    
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
    
    @NotNull(message = "门店ID不能为空")
    private Integer storeId;
    
    @DecimalMin(value = "0.01", message = "日租金必须大于0")
    private BigDecimal dailyRate;
}
