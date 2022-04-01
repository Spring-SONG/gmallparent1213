package com.atguigu.gmall1213.list.repository;

import com.atguigu.gmall1213.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author mqx
 * @date 2020/6/19 11:52
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
