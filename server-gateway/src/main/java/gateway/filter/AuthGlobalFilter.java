package gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.common.result.ResultCodeEnum;
import com.atguigu.gmall1213.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/8 14:24
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {
    @Autowired
    private RedisTemplate redisTemplate;

    //路径匹配的工具类
    private AntPathMatcher antPathMatcher=new AntPathMatcher();

    @Value("authUrls.url")
    private String authUrlsUrl;//从配置文件中获取白名单页面

    //实现全局过滤器
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request=exchange.getRequest();
        String path=request.getURI().getPath();
        //匹配路径中是否包含白名单页面，
        if (antPathMatcher.match("/**/inner/**", path)) {
            //获取相应对象，提示没有访问权限
            ServerHttpResponse response=exchange.getResponse();
            return out(response, ResultCodeEnum.PERMISSION);
        }
        String userId=getUserId(request);
        //获取临时用户id
        String userTempId=getUserTempId(request);
        if ("-1".equals(userId)) {
            ServerHttpResponse response=exchange.getResponse();

            return out(response, ResultCodeEnum.PERMISSION);
        }

        //用户登录认证
        if (antPathMatcher.match("/api/**/auth/**",path)) {
            if (StringUtils.isEmpty(userId)){
                // 获取响应对象
                ServerHttpResponse response = exchange.getResponse();
                // out 方法提示信息
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }
        //验证用户通过web-all 访问时有没有带着白名单
        for (String url : authUrlsUrl.split(",")) {

            if (path.indexOf(url)!=-1&& StringUtils.isEmpty(userId)) {
                // 获取响应对象
                ServerHttpResponse response = exchange.getResponse();
                // 返回一个响应的状态码，重定向获取请求资源
                response.setStatusCode(HttpStatus.SEE_OTHER);
                // 访问登录页面
                response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
                // 设置返回
                return response.setComplete();
            }
        }
        if (!StringUtils.isEmpty(userId) || !StringUtils.isEmpty(userTempId)){
            if (!StringUtils.isEmpty(userId)){
                // 将用户Id 存储在请求头中
                request.mutate().header("userId",userId).build();
            }
            if (!StringUtils.isEmpty(userTempId)){
                // 将临时用户Id 存储在请求头中
                request.mutate().header("userTempId",userTempId).build();
            }
            // 固定写法
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    /**
     * 获取用户id
     *
     * @param request
     * @return
     */
    private String getUserId(ServerHttpRequest request) {

        String token="";
        //从header中获取
        List<String> list=request.getHeaders().get("token");
        if (null != list) {
            token=list.get(0);
        } else {
            //从coolie中获取
            MultiValueMap<String, HttpCookie> cookies=request.getCookies();
            HttpCookie cookie=cookies.getFirst("token");
            if (null != cookie) {
                //token需要经过url进行传送
                token=URLDecoder.decode(cookie.getValue());
            }
        }
        //从缓存中获取数据
        if (!StringUtils.isEmpty(token)) {
            //定义缓存的key
            String userKey="user:login:" + token;
            //从缓存中获取数据
            String userJson =(String) redisTemplate.opsForValue().get(userKey);
            JSONObject jsonObject=JSONObject.parseObject(userJson);
            String ip=jsonObject.getString("ip");
            //获取当前电脑的IP
            String ipAddress=IpUtil.getGatwayIpAddress(request);

            if (ip.equals(ipAddress)) {
                //说明是同一台电脑，返回用户id
                return jsonObject.getString("useId");
            } else {
                return "-1";
            }
        }
        return null;
    }
    // 获取临时用户Id,添加购物车时，临时用户Id 已经存在cookie 中！ 同时也可能存在header 中

    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";
        // 从header 中获取
        List<String> list = request.getHeaders().get("userTempId");
        if (null!=list){
            // 集合中的数据是如何存储，集合中只有一个数据因为key 是同一个
            userTempId = list.get(0);
        }else {
            // 从cookie 中获取
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            HttpCookie cookie = cookies.getFirst("userTempId");
            if (null!=cookie){
                // 因为token 要经过url进行传送
                userTempId = URLDecoder.decode(cookie.getValue());
            }
        }
        return userTempId;
    }
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum permission) {

        //返回用户没有权限的信息
        Result<Object> result=Result.build(null, permission);
        byte[] bytes=JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap=response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(wrap));
    }
}
