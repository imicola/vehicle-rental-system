package com.java_db.demo.service;

import com.java_db.demo.dto.StoreDTO;
import com.java_db.demo.entity.Store;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 门店管理服务
 * 处理门店的 CRUD 操作
 */
@Service
@RequiredArgsConstructor
public class StoreService {
    
    private final StoreRepository storeRepository;
    
    /**
     * 添加门店（管理员功能）
     * 
     * @param storeDTO 门店信息
     * @return 新增的门店
     */
    @Transactional
    public Store addStore(StoreDTO storeDTO) {
        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setAddress(storeDTO.getAddress());
        store.setPhone(storeDTO.getPhone());
        
        return storeRepository.save(store);
    }
    
    /**
     * 更新门店信息
     * 
     * @param storeId 门店 ID
     * @param storeDTO 门店信息
     * @return 更新后的门店
     */
    @Transactional
    public Store updateStore(Integer storeId, StoreDTO storeDTO) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("门店不存在"));
        
        store.setName(storeDTO.getName());
        store.setAddress(storeDTO.getAddress());
        store.setPhone(storeDTO.getPhone());
        
        return storeRepository.save(store);
    }
    
    /**
     * 查询所有门店
     * 供用户下拉框选择
     * 
     * @return 所有门店列表
     */
    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
    
    /**
     * 根据 ID 查询门店
     * 
     * @param storeId 门店 ID
     * @return 门店信息
     */
    @Transactional(readOnly = true)
    public Store findById(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("门店不存在"));
    }
    
    /**
     * 删除门店（管理员功能）
     * 注意：如果门店下有车辆或订单，会因外键约束失败
     * 
     * @param storeId 门店 ID
     */
    @Transactional
    public void deleteStore(Integer storeId) {
        Store store = findById(storeId);
        storeRepository.delete(store);
    }
}
