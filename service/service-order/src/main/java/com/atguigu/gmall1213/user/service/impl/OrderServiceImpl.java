package com.atguigu.gmall1213.user.service.impl;

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

    private void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setProcessStatus(processStatus.name());
        orderInfoMapper.updateById(orderInfo);
    }
}
