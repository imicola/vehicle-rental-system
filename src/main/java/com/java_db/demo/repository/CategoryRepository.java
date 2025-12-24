package com.java_db.demo.repository;

import com.java_db.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 车辆分类数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // 基础 CRUD 方法由 JpaRepository 提供
}
