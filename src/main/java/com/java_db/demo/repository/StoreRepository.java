package com.java_db.demo.repository;

import com.java_db.demo.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 门店数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    // 基础 CRUD 方法由 JpaRepository 提供：
    // - save(Store) - 保存/更新
    // - findById(Integer) - 根据 ID 查询
    // - findAll() - 查询所有
    // - deleteById(Integer) - 删除
    // - count() - 统计数量
}
