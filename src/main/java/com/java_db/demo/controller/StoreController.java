package com.java_db.demo.controller;

import com.java_db.demo.dto.StoreDTO;
import com.java_db.demo.entity.Store;
import com.java_db.demo.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店控制器
 * 处理门店查询、管理等请求
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "门店接口", description = "门店查询、管理等相关接口")
public class StoreController {
    
    private final StoreService storeService;
    
    /**
     * 查询所有门店
     * 用于前端下拉框选择
     * 
     * @return 所有门店列表
     */
    @GetMapping
    @Operation(summary = "查询所有门店", description = "获取所有门店列表，供用户选择取车/还车门店")
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }
    
    /**
     * 查询门店详情
     * 
     * @param id 门店 ID
     * @return 门店详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询门店详情", description = "根据门店 ID 查询详细信息")
    public ResponseEntity<Store> getStoreById(
            @Parameter(description = "门店ID") @PathVariable Integer id) {
        Store store = storeService.findById(id);
        return ResponseEntity.ok(store);
    }
    
    /**
     * 添加门店（管理员功能）
     * 
     * @param storeDTO 门店信息
     * @return 新增的门店
     */
    @PostMapping
    @Operation(summary = "添加门店", description = "管理员添加新门店")
    public ResponseEntity<Store> addStore(@Valid @RequestBody StoreDTO storeDTO) {
        Store store = storeService.addStore(storeDTO);
        return ResponseEntity.ok(store);
    }
    
    /**
     * 更新门店信息（管理员功能）
     * 
     * @param id 门店 ID
     * @param storeDTO 门店信息
     * @return 更新后的门店
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新门店信息", description = "管理员更新门店信息")
    public ResponseEntity<Store> updateStore(
            @Parameter(description = "门店ID") @PathVariable Integer id,
            @Valid @RequestBody StoreDTO storeDTO) {
        Store store = storeService.updateStore(id, storeDTO);
        return ResponseEntity.ok(store);
    }
}
