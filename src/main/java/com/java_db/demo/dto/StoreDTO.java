package com.java_db.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店添加/更新 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    
    @NotBlank(message = "门店名称不能为空")
    private String name;
    
    private String address;
    
    private String phone;
}
