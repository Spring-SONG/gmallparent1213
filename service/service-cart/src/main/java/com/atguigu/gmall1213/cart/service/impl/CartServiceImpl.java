package com.atguigu.gmall1213.cart.service.impl;

import com.atguigu.gmall1213.cart.mapper.CartInfoMapper;
import com.atguigu.gmall1213.cart.service.CartAsyncService;
import com.atguigu.gmall1213.cart.service.CartService;
import com.atguigu.gmall1213.common.constant.RedisConst;
import com.atguigu.gmall1213.model.cart.CartInfo;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/11 10:47
 */
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CartAsyncService cartAsyncService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {

        String cartKey=getCartKey(userId);

        QueryWrapper<CartInfo> infoQueryWrapper=new QueryWrapper<>();
        infoQueryWrapper.eq("user_id", userId).eq("sku_id", skuId);
        CartInfo cartInfoExist=cartInfoMapper.selectOne(infoQueryWrapper);
        if (null != cartInfoExist) {
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            cartInfoExist.setSkuPrice(productFeignClient.getSkuPrice(skuId));
            //更新使用异步接口
            cartAsyncService.updateCartInfo(cartInfoExist);
            //缓存放最外层
        } else {
            //购物车中没有数据
            SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
            CartInfo cartInfo=new CartInfo();
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartAsyncService.saveCartInfo(cartInfo);
            //到这代表cartInfoExist是null，为了防止cartInfoExist被GC掉可以废物利用
            cartInfoExist=cartInfo;
        }
        //在缓存中放数据
        redisTemplate.boundHashOps(cartKey).put(skuId.toString(), cartInfoExist);
        //设置过期时间
        setCartKeyExpire(cartKey);

    }


    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        //判断是登录还是未登录
        if (StringUtils.isEmpty(userId)) {
            cartInfoList=getCartList(userTempId);
        }
        if (StringUtils.isEmpty(userTempId)) {
            cartInfoList=getCartList(userId);
        }
        return cartInfoList;
    }

    //获取购物车列表
    private List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        //先判断缓存中是否有购物车
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        String cartKey=getCartKey(userId);
        cartInfoList=redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            //书名缓存中有数据
            //按照id给集合排序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        } else {
            //说明缓存中没有数据
            cartInfoList= loadCartCache(userId);
            return cartInfoList;
        }

    }

    // 根据用户Id 查询数据库并将数据放入缓存。
    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList=cartInfoMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", userId));
        if (CollectionUtils.isEmpty(cartInfoList)) {
            return cartInfoList;
        }
        //不为空将数据放入缓存
        HashMap<String, CartInfo> map=new HashMap<>();
        String cartKey=getCartKey(userId);
        //遍历集合将数据放入map中
        for (CartInfo cartInfo : cartInfoList) {
            //因为价格可能会发生变化
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));
            map.put(cartKey,cartInfo);
        }
        redisTemplate.opsForHash().putAll(cartKey, map);
        //设置过期时间
        setCartKeyExpire(cartKey);
        return cartInfoList;
    }

    //设置购物车过期时间
    private void setCartKeyExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    //获取用户购物车缓存的key
    private String getCartKey(String userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
