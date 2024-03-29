# webflux实现socket
## 测试入口
- http://localhost:8080/client
- http://localhost:8080/heart-特别推荐参考byArvin-2019-06-25-1534
- http://localhost:8080/online  在线人数，统计
- http://localhost:8080/chatweb 聊天室

## 当前容量规化
- ExecutorSingleton线程池：10000；

## 使用思路--heart
1. 通过心跳，保持客户端与服务器端长链接
2. 通过 sock.send("Hello-World");每3秒发送一次请求，获取到用户想要的信息-（此处是轮训方式）
3. 使用场景：
    - 可用于活动通知与订单通知。
    - 在线人数与在线时长（用于智能分析）（通过时间长短判断用户对产品的关注度）
    - 活动推送等
    - 课程在线关看时长

## websocket里面添加Token
- 通过请求地址中带参数，来传递token
    - 参考 ：[websocket里面添加Token](https://blog.csdn.net/qq_22560431/article/details/82895075)

## websocket跨域访问测试
- 跨域访问测试，默认情况下，就可以实现跨域访问

## 基于 WebSocket 的聊天室
- [看完让你彻底理解 WebSocket 原理，附完整的实战代码（包含前端和后端）](https://blog.csdn.net/li_jia_wei/article/details/81148053)

## 参考
- [webflux的websocket连接与生命周期](https://blog.csdn.net/sinat_39291367/article/details/89467555)-推荐参考byArvin
- [Spring-boot2 WebFlux WebSockit实现-实现心跳](https://blog.csdn.net/daisy_xiu/article/details/80708620)-推荐参考byArvin
- [springboot2学习-webflux与websocke](https://blog.csdn.net/j903829182/article/details/80545876)
- [京东到家基于netty与websocket的实践](https://blog.csdn.net/zl1zl2zl3/article/details/84660271)
- [WebSocket重连reconnecting-websocket.js的使用](https://www.cnblogs.com/kennyliu/p/6477746.html)
- 如何保证客户端存活-通过心跳
    - 有一点要注意，你要定时给服务端发送心跳数据包证明客户端存活

> **使用场景参考**

```
背景
在京东到家商家中心系统中，商家提出在 Web 端实现自动打印的需求，不需要人工盯守点击打印，直接打印小票，以节约人工成本。
解决思路
关于问题的思考逻辑：
第一种：想到的是可以用ajax来轮询服务端获取最新订单，也就是pull。
第二种：我们是否可以用类似推送的设计来实现，也就是push。
两种思路我们评估其优缺点：
ajax方式实现简单，只需要定时从服务端pull数据即可，但也增加了很多次无效的轮询， 无形中增加服务端无效查询。
push方式实现稍复杂，需要服务端与PC端保持连接，这就需要建立长连接，最终通过长连接的方式来实现push效果。
经过讨论，我们选择了第二种，订单中心生产出的新订单，通过MQ的方式推送给web端，最终获得一个比较好的用户体验。
```

## 参考
- [Java8新特性 default关键字](https://blog.csdn.net/xcy1193068639/article/details/80249380)

### 问题处理：
- Netty内存溢出-（待解决）
    - io.netty.util.ResourceLeakDetector       : LEAK: ByteBuf.release() was not called before it's garbage-collected
    ```
    解决方案：
    升级spring-boot-starter-parent组件到2.1.6.RELEASE即可。（此方案目前还是不要解决）
    ```
