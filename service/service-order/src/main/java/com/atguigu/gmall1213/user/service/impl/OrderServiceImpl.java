package com.atguigu.gmall1213.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall1213.common.util.HttpClientUtil;
import com.atguigu.gmall1213.model.enums.OrderStatus;
import com.atguigu.gmall1213.model.enums.ProcessStatus;
import com.atguigu.gmall1213.model.order.OrderDetail;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.user.mapper.OrderDetailMapper;
import com.atguigu.gmall1213.user.mapper.OrderInfoMapper;
import com.atguigu.gmall1213.user.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import common.constant.MqConst;
import common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/14 14:54
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper,OrderInfo> implements OrderService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;

    @Value("${ware.url}")
    private String WareUrl;

    @Override
    @Transactional
    public Long saveOrderInfo(OrderInfo orderInfo) {

        orderInfo.sumTotalAmount();
        String outTradeNo="ATGUIGU" + System.currentTimeMillis() + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        List<OrderDetail> orderDetailList=orderInfo.getOrderDetailList();
        StringBuffer sb=new StringBuffer();
        for (OrderDetail orderDetail : orderDetailList) {

            sb.append(orderDetail.getSkuName() + " ");
        }
            if (sb.toString().length() > 100) {
                orderInfo.setTradeBody(sb.toString().substring(100));
            } else {
                orderInfo.setTradeBody(sb.toString());
            }
            //赋值订单状态，刚开始都是未付款
            orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
            //创建时间
            orderInfo.setCreateTime(new Date());
            //过期时间，默认一天
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            orderInfoMapper.insert(orderInfo);

            if (CollectionUtils.isEmpty(orderDetailList)) {
                for (OrderDetail detail : orderDetailList) {
                    detail.setOrderId(orderInfo.getId());
                    orderDetailMapper.insert(detail);
                }
            }
        rabbitService.sendDelayMessage(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, MqConst.ROUTING_ORDER_CANCEL,
                orderInfo.getId(), MqConst.DELAY_TIME);
        return orderInfo.getId();
    }

    @Override
    public String getTradeNo(String userId) {
        String tradeNoKey =  "user:"+userId+":tradeNo";

        String tradeNo=UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(tradeNoKey, tradeNo);

        return tradeNo;
    }

    @Override
    public boolean checkTradeNo(String tradeNo, String userId) {
        String tradeNoKey = "user:"+userId+":tradeNo";
        String tradeNoRedis=(String) redisTemplate.opsForValue().get(tradeNoKey);

        return tradeNo.equals(tradeNoRedis);
    }

    @Override
    public void deleteTradeNo(String userId) {
        String tradeNoKey = "user:"+userId+":tradeNo";
        redisTemplate.delete(tradeNoKey);
    }

    @Override
    public boolean checkStock(Long skuId, Integer skuNum) {

        String result = HttpClientUtil.doGet(WareUrl + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
        // 0 无货， 1 有货
        return "1".equals(result);
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {

        OrderInfo orderInfo=orderInfoMapper.selectById(orderId);
        List<OrderDetail> orderDetailList=orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orderId));
        orderInfo.setOrderDetailList(orderDetailList);

        return orderInfo;
    }

    @Override
    public void execExpiredOrder(Long orderId) {
        updateOrderStatus(orderId, ProcessStatus.CLOSED);
    }

    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setProcessStatus(processStatus.name());
        orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public void sendOrderStatus(Long orderId) {
        updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        // 需要参考库存管理文档 根据管理手册。
        // 发送的数据 是 orderInfo 中的部分属性数据，并非全部属性数据！
        // 获取发送的字符串：
        String wareJson = initWareOrder(orderId);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK, MqConst.ROUTING_WARE_STOCK, wareJson);

    }

    public String initWareOrder(Long orderId) {
        // 首先查询到orderInfo
        OrderInfo orderInfo = getOrderInfo(orderId);
        // 将orderInfo 中的部分属性，放入一个map 集合中。
        Map map = initWareOrder(orderInfo);
        // 返回json 字符串
        return JSON.toJSONString(map);
    }

    // 将orderInfo 部分数据组成map
    public Map initWareOrder(OrderInfo orderInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId",orderInfo.getId());
        map.put("consignee", orderInfo.getConsignee());
        map.put("consigneeTel", orderInfo.getConsigneeTel());
        map.put("orderComment", orderInfo.getOrderComment());
        map.put("orderBody", orderInfo.getTradeBody());
        map.put("deliveryAddress", orderInfo.getDeliveryAddress());
        map.put("paymentWay", "2");
        // map.put("wareId", orderInfo.getWareId());// 仓库Id ，减库存拆单时需要使用！
        /*
            details 对应的是订单明细
            details:[{skuId:101,skuNum:1,skuName:’小米手64G’},
                       {skuId:201,skuNum:1,skuName:’索尼耳机’}]
         */
        // 声明一个list 集合 来存储map
        List<Map> maps = new ArrayList<>();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            // 先声明一个map 集合
            HashMap<String, Object> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId",orderDetail.getSkuId());
            orderDetailMap.put("skuNum",orderDetail.getSkuNum());
            orderDetailMap.put("skuName",orderDetail.getSkuName());
            maps.add(orderDetailMap);
        }
        map.put("details", JSON.toJSONString(maps));
        // 返回构成好的map集合。
        return map;
    }
}
