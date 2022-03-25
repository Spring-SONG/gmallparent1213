package com.atguigu.gmall1213.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall1213.common.cache.GmallCache;
import com.atguigu.gmall1213.common.constant.RedisConst;
import com.atguigu.gmall1213.model.product.*;
import com.atguigu.gmall1213.product.mapper.*;
import com.atguigu.gmall1213.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2021/10/9 15:27
 */
@Service
public class ManageServiceImpl implements ManageService {

    // 通常会调用mapper 层。
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1s=baseCategory1Mapper.selectList(null);
        return baseCategory1s;
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper=new QueryWrapper<>();
        wrapper.eq("category1_id", category1Id);
        return baseCategory2Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper=new QueryWrapper<>();
        wrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        // 在这个方法中，一个是插入数据功能，一个是修改数据功能
        // 一个是平台属性表；baseAttrInfo
        if (baseAttrInfo.getId() != null) {
            // 修改功能
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            // 插入数据
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // 一个是平台属性值表： baseAttrValue  插入的时候，直接insert ，
        // 修改{ 一：update，二：【先将数据删除，然后新增】}

        // 删除数据 有条件的删除
        // 根据传递过来的平台属性值Id 进行删除

        // 删除的是value所以跟infoid没关系是吧，删除平台属性下的平台属性值！
        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper=new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueQueryWrapper);

        List<BaseAttrValue> attrValueList=baseAttrInfo.getAttrValueList();
        if (null != attrValueList && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                // 页面在提交数据的时候，并没有给attrId 赋值，所以在此处需要手动赋值
                // attrId = baseAttrInfo.getId();
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                // 循环将数据添加到数据表中
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getAttrInfo(Long attrId) {
        // attrId 平台属性Id  attrId=base_attr_info.id  此id 是base_attr_info 主键
        BaseAttrInfo baseAttrInfo=baseAttrInfoMapper.selectById(attrId);

        // 判断如果当前baseAttrInfo 存在，则查询平台属性值集合。
        if (null != baseAttrInfo) {
            // 不能直接返回baseAttrInfo，因为控制器需要的是baseAttrInfo 下的平台属性值集合。
            // 需要给平台属性值属性赋值。
            // select * from base_attr_value where attr_id = attrId;
            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper=new QueryWrapper<>();
            baseAttrValueQueryWrapper.eq("attr_id", attrId);
            List<BaseAttrValue> baseAttrValueList=baseAttrValueMapper.selectList(baseAttrValueQueryWrapper);
            // 将平台属性值结合放入baseAttrInfo 中，此时才能返回！
            baseAttrInfo.setAttrValueList(baseAttrValueList);
        }

        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> selectPage(Page<SpuInfo> spuInfoPageParam, SpuInfo spuInfo) {

        // 封装查询条件 where category3_id = ? order by id
        QueryWrapper<SpuInfo> spuInfoQueryWrapper=new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id", spuInfo.getCategory3Id());
        // 查询完成之后，可以按照某一种规则进行排序。
        spuInfoQueryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(spuInfoPageParam, spuInfoQueryWrapper);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        // 调用mapper 层。
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        /*
            需要对应的mapper
            spuInfo 表中的数据
            spuImage 图片列表
            spuSaleAttr 销售属性
            spuSaleAttrValue 销售属性值
         */
        spuInfoMapper.insert(spuInfo);
        // 从获取到数据
        List<SpuImage> spuImageList=spuInfo.getSpuImageList();
        if (null != spuImageList && spuImageList.size() > 0) {
            // 循环遍历添加
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList=spuInfo.getSpuSaleAttrList();
        if (null != spuSaleAttrList && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                // 在销售属性中获取销售属性值集合
                List<SpuSaleAttrValue> spuSaleAttrValueList=spuSaleAttr.getSpuSaleAttrValueList();

                if (null != spuSaleAttrValueList && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());

                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
//        skuInfo 库存单元表
//        skuSaleAttrValue sku与销售属性值的中间表
//        skuAttrValue sku与平台属性中间表
//        skuImage 库存单元图片表
        skuInfoMapper.insert(skuInfo);
        //获取销售属性数据
        List<SkuSaleAttrValue> skuSaleAttrValueList=skuInfo.getSkuSaleAttrValueList();
        if (null != skuSaleAttrValueList && skuSaleAttrValueList.size() > 0) {
            for (SkuSaleAttrValue attrValue : skuSaleAttrValueList) {
                //给sku数据添加id
                attrValue.setId(skuInfo.getId());
                attrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(attrValue);
            }
        }
        //平台属性数据
        List<SkuAttrValue> skuAttrValueList=skuInfo.getSkuAttrValueList();
        if (null != skuAttrValueList && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
//skuImage图片列表
        List<SkuImage> skuImageList=skuInfo.getSkuImageList();
        if (null != skuImageList && skuImageList.size() > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        //发送一个消息队列通知商品上架，发送的内容就是skudI
        //TODO
    }

    @Override
    public IPage<SkuInfo> selectPage(Page<SkuInfo> skuInfoPage) {
        // 需要使用mapper
        return skuInfoMapper.selectPage(skuInfoPage, new QueryWrapper<SkuInfo>().orderByDesc("id"));
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setIsSale(1);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    @GmallCache(prefix="sku")
    public SkuInfo getSkuInfo(Long skuId) {
        return getSkuInfoDB(skuId);
    }

    //使用redisson做锁
    private SkuInfo getSkuInfoRedisson(Long skuId) {
        //先判断缓存中是否有数据
        SkuInfo skuInfo=null;
        try {
            // 先判断缓存中是否有数据，查询缓存必须知道缓存的key是什么！
            // 定义缓存的key 商品详情的缓存key=sku:skuId:info
            String skuKey=RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            //根据key获取缓存中的数据，如果不存在则返回一个空对象
            skuInfo=(SkuInfo) redisTemplate.opsForValue().get(skuKey);
            if (skuInfo == null) {
                //从数据库中获取数据，为了防止缓存击穿做分布式锁
                String lockKye=RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
                //使用redisson
                RLock lock=redissonClient.getLock(skuKey);
                //尝试加锁，最多等待1秒，1秒后自动解锁
                boolean res=lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (res) {
                    try {
                        skuInfo=getSkuInfoDB(skuId);
                        if (skuInfo == null) {
                            //防止缓存穿透，放一个空对象放入缓存中，过期时间不宜过长
                            SkuInfo skuInfo1=new SkuInfo();
                            redisTemplate.opsForValue().set(skuKey, skuInfo1, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);

                            return skuInfo1;
                        }
                        //从数据库中获取到数据，放入缓存
                        redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        return skuInfo;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    //没有获取到锁的线程等待
                    Thread.sleep(100);
                    //等待结束后继续
                    return getSkuInfo(skuId);
                }
            } else {
                //缓存中有数据
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //如果途中数显问题，数据库做数据兜底
        return getSkuInfoDB(skuId);
    }

    //使用redis和lua脚本来做锁
    private SkuInfo getSkuInfoRedis(Long skuId) {
        SkuInfo skuInfo=null;

        try {
            //获取缓存中的key
            String skuKey=RedisConst.SKUKEY_PREFIX +skuId+ RedisConst.SKUKEY_SUFFIX;
            //根据key查询缓存数据
            skuInfo=(SkuInfo) redisTemplate.opsForValue().get(skuKey);
            if (skuInfo == null) {

                //获取lockKey
                String lockKey=RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
                //uuid作为锁的值
                String uuid=UUID.randomUUID().toString();
                //开始上锁
                Boolean isExist=redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                //如果返回true，则代表获取到锁
                if (isExist) {
                    //查询数据库数据
                    skuInfo=getSkuInfoDB(skuId);
                    if (skuInfo == null) {
                        //预防缓存穿透
                        SkuInfo skuInfo1=new SkuInfo();
                        redisTemplate.opsForValue().set(skuKey, skuInfo1, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        //返回数据
                        return skuInfo1;
                    }
                    //从数据库查询出来的不是空值，放入缓存
                    redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    //删除锁，使用lua脚本来删除锁
                    String script="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    DefaultRedisScript<Long> redisScript=new DefaultRedisScript<>();
                    // 指定好返回的数据类型
                    redisScript.setResultType(Long.class);
                    // 指定好lua 脚本
                    redisScript.setScriptText(script);
                    // 第一个参数存储的RedisScript  对象，第二个参数指的锁的key，第三个参数指的key所对应的值
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);

                    // 返回正常数据
                    return skuInfo;
                } else {
                    //无锁线程等待
                    Thread.sleep(100);
                    //再次尝试获取锁
                    return getSkuInfo(skuId);
                }

            } else {
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //数据库兜底
        return getSkuInfoDB(skuId);

    }


    //抽取出方法
    private SkuInfo getSkuInfoDB(Long skuId) {
        SkuInfo skuInfo=skuInfoMapper.selectById(skuId);
        List<SkuImage> skuImageList=skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id", skuId));
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }

    @Override
    @GmallCache(prefix = "baseCategoryView")
    public BaseCategoryView getBaseCategoryViewBycategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    @GmallCache(prefix="price")
    public BigDecimal getSkuPriceBySkuId(Long sukId) {

        SkuInfo skuInfo=skuInfoMapper.selectById(sukId);
        if (null != skuInfo) {
            BigDecimal price=skuInfo.getPrice();
            return price;
        }
        return new BigDecimal(0);
    }

    // 销售属性-销售属性值：
    @GmallCache(prefix = "spuSaleAttr")
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @Override
    @GmallCache(prefix = "skuValueIdsMap")
    public Map getSkuValueIdsMap(Long spuId) {
        // 调用mapper 自定义方法获取数据，将数据查询之后直接放入List。
        HashMap<Object, Object> map=new HashMap<>();
        /*
            select sv.sku_id, group_concat(sv.sale_attr_value_id order by sp.base_sale_attr_id asc separator '|')
                value_ids from sku_sale_attr_value sv
                inner  join spu_sale_attr_value  sp on sp.id = sv.sale_attr_value_id
                where sv.spu_id = 12
                group by sku_id;

            执行出来的结果应该是List<Map>
            map.put("55|57","30") skuSaleAttrValueMapper
         */
        List<Map> mapList=skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        // 获取到数据以后。开始循环遍历集合中的每条数据
        if (null != mapList && mapList.size() > 0) {
            for (Map skuMaps : mapList) {
                // map.put("55|57","30")
                map.put(skuMaps.get("value_ids"), skuMaps.get("sku_id"));
            }
        }
        return map;
    }

    /**
     * 将首页数据封装成前端页面需要的数据格式后放入缓存，实时展示切换
     * @return
     */
    @Override
    @GmallCache(prefix="index")
    public List<JSONObject> getBaseCategoryList() {

        ArrayList<JSONObject> list=new ArrayList<>();
        //查询所有数据
        List<BaseCategoryView> baseCategoryViewList=baseCategoryViewMapper.selectList(null);
        //按照一级分类数据进行分组
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        //初始化index构建字符串
        int index=1;

        for (Map.Entry<Long, List<BaseCategoryView>> entry : category1Map.entrySet()) {
            //获取一级分类id
            Long categor1Id=entry.getKey();
            //一级分类数据
            List<BaseCategoryView> category2List=entry.getValue();
            // 声明一个对象保存一级分类数据 一级分类的Json字符串
            JSONObject category1=new JSONObject();
            category1.put("index", index);
            category1.put("categoryId", categor1Id);
            //获取分组后的商品名称
            String categoryName=category2List.get(0).getCategory1Name();
            category1.put("categoryName", categoryName);
            //变量迭代
            index++;

            //获取二级分类数据
            Map<Long, List<BaseCategoryView>> category2Map=category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //创建一个对象保存二级分类数据集合

            ArrayList<JSONObject> category2Child=new ArrayList<>();
            //获取二级分类中的数据
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : category2Map.entrySet()) {
                //获取二级分类id
                Long categor2Id=entry2.getKey();

                //获取二级分类id下的数据->三级数据
                List<BaseCategoryView> category3List=entry2.getValue();
                JSONObject category2=new JSONObject();
                category2.put("categoryId", categor2Id);
                category2.put("categoryName", category3List.get(0).getCategory2Name());
                category2Child.add(category2);
                //处理三级分类数据
                ArrayList<JSONObject> category3Child=new ArrayList<>();
                category3List.stream().forEach(category3View->{
                    JSONObject category3=new JSONObject();
                    category3.put("categoryId",category3View.getCategory3Id());
                    category3.put("categoryName", category3View.getCategory3Name());
                    //将数据放入三级对象中
                    category3Child.add(category3);
                });
                //将数据放入二级对象中
                category2.put("categoryChild", category3Child);
            }
            //将二级数据放入一级对象中
            category1.put("categoryChild", category2Child);
            // 按照json数据接口方式 分别去封装 一级分类，二级分类，三级分类数据。
            list.add(category1);
        }
        return list;
    }

}
