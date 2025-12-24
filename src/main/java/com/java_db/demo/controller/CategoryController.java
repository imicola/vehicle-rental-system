package com.java_db.demo.controller;

import com.java_db.demo.entity.Category;
import com.java_db.demo.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆分类控制器
 * 处理车辆分类查询等请求
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类接口", description = "车辆分类查询等相关接口")
public class CategoryController {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * 查询所有分类
     * 用于前端下拉框选择
     * 
     * @return 所有分类列表
     */
    @GetMapping
    @Operation(summary = "查询所有分类", description = "获取所有车辆分类列表，供用户选择车型")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 查询分类详情
     * 
     * @param id 分类 ID
     * @return 分类详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询分类详情", description = "根据分类 ID 查询详细信息")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        return ResponseEntity.ok(category);
    }
}
