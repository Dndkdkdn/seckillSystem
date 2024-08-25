package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.config.AccessLimit;
import com.example.controller.request.SeckillRequest;
import com.example.exception.GlobalException;
import com.example.pojo.RabbitMQSeckillMessage;
import com.example.pojo.SeckillOrder;
import com.example.pojo.User;
import com.example.rabbitmq.RabbitMQSender;
import com.example.service.GoodsService;
import com.example.service.SeckillGoodsService;
import com.example.service.SeckillOrderService;
import com.example.vo.GoodsVo;
import com.example.vo.RespBean;
import com.example.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: SeckillController
 * Package: com.example.controller
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/12 16:51
 * @Version 1.0
 */

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Long> script;

    @Autowired
    private RabbitMQSender sender;
    private Map<Long, Boolean> memoryMarkEmptyStock = new HashMap<>();


    /**
     * 在秒杀商品时生成验证码
     *
     * @param
     * @param response
     * @throws IOException
     */
    @RequestMapping("/captcha")
    public void captcha(User user, Long goodsId, HttpServletResponse response){
        if(Objects.isNull(user)){
            throw new GlobalException(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        // 设置请求头为输出图片类型
//        response.setContentType("image/jpg");
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);//永不失效

        // 三个参数分别为宽、高、位数
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 48, 5);
        arithmeticCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置

        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, arithmeticCaptcha.text(), 5, TimeUnit.MINUTES);
        try {
            arithmeticCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }

//        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
//        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
//        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        // 验证码存入session
//        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());

        // 输出图片流
//        specCaptcha.out(response.getOutputStream());
    }



    /**
     * 获取秒杀地址
     * @param user
     * @param request
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/getPath", method = RequestMethod.POST)
    @ResponseBody
    public RespBean getPath(User user, @RequestBody SeckillRequest request, HttpServletRequest servletRequest){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        request.setCaptcha("0");
        boolean check = seckillGoodsService.checkCaptcha(user, request.getGoodsId(), request.getCaptcha());
        if (!check){
            return RespBean.error(RespBeanEnum.VERIFY_CODE_ERROR);
        }
        String path = seckillGoodsService.createSeckillPath(user, request);
        return RespBean.success(path);
    }


    /**
     *
     * @param user
     * @param request
     * @return  orderId:下单成功  -1:秒杀失败  0:排队中
     *
     */
    @RequestMapping(value = "/querySeckillResult", method = RequestMethod.POST)
    @ResponseBody
    public RespBean querySeckillResult(User user, @RequestBody SeckillRequest request){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, request.getGoodsId());
        return RespBean.success(orderId);
    }


    @RequestMapping(value = "/{seckillPathStr}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, @PathVariable String seckillPathStr, @RequestBody SeckillRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        if(StringUtils.isEmpty(seckillPathStr)){
            return RespBean.error(RespBeanEnum.SECKILL_PATH_NULL_ERROR);
        }
        if (!seckillPathStr.equals((String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + request.getGoodsId()))){
            return RespBean.error(RespBeanEnum.SECKILL_PATH_VALIDATION_ERROR);
        }//其实我觉得此处的校验应该加强一些 比如生成SeckillPath时，不使用随机值，或者记录下随机值，
        // 然后用用户ID 商品ID和随机值重新进行md5加密，与传进来的seckillPathStr进行比较
        // （而将生成的SeckillPath用用户ID和商品ID标记存入redis就实现了这个想法）



        //通过内存标记减少redis的访问
        if(memoryMarkEmptyStock.get(request.getId())){
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }

        //先判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + request.getGoodsId());
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
        }
        //redis预减库存
//        Integer stockCountResult = (Integer)redisTemplate.opsForValue().get("SeckillGoods:" + request.getId());
//        if(stockCountResult < 1){//判断库存大于0，才能进行秒杀---->防止库存超卖
//            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
//        }
        //decrement()  递减  如果value是数字类型的话  每调用一次就会减一  并且递增递减都是原子性的  stockCountResult是递减之后的库存
//        Long stockCountResult = redisTemplate.opsForValue().decrement("SeckillGoods:" + request.getId());
        //进一步优化redis预减库存    使用lua脚本  redis分布式锁
        Long stockCountResult = (Long) redisTemplate.execute(script, Collections.singletonList("SeckillGoods:" + request.getId()),Collections.EMPTY_LIST);
        if(stockCountResult < 0){//判断库存大于等于0，才能进行秒杀---->防止库存超卖
            memoryMarkEmptyStock.put(request.getId(), true);
            redisTemplate.opsForValue().increment("SeckillGoods:" + request.getId());//秒杀失败，恢复库存
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }
        //下单 用RabbitMQ 可以从之前的代码中看出下单需要 用户对象信息和商品信息  所以需要去封装一个发送Message的对象，携带这两个信息
        RabbitMQSeckillMessage message = new RabbitMQSeckillMessage(user, request.getId());
        sender.sendDoSeckill(JSON.toJSONString(message), "doSeckill.create.order");
//        Order order= seckillOrderService.add(user, goods);
//        SeckillGoodsRes response = SeckillGoodsRes.init(goods, order);
        return RespBean.success(0);//0正在排队中  然后转到MQ消费者  去消费
    }

    /**
     * 系统初始化时，可以把商品库存信息加载到redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsVoList)){
            return;
        }
        goodsVoList.forEach(goods -> {
            if(goods.getStockCount() > 0){
                memoryMarkEmptyStock.put(goods.getId(), false);
            }else{
                memoryMarkEmptyStock.put(goods.getId(), true);
            }
            redisTemplate.opsForValue().set("SeckillGoods:" + goods.getId(), goods.getStockCount());
        });
    }




//    @RequestMapping("/doSeckill")
//    public String doSeckill(Model model, User user, Long id){
//        if(null == user){
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(id);
//        if(goods.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.STOCK_IS_EMPTY);
//            return  "seckillFail";
//        }
//        //判断该用户是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goods.getGoodsId()));
//        if(seckillOrder != null){
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_REPEAT);
//            return "seckillFail";
//        }
//        Order order= seckillOrderService.add(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "orderDetail";
//    }


//    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
//    @ResponseBody
//    public RespBean doSeckill(User user, @RequestBody SeckillRequest request){
//        if(null == user){
//            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
//        }
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(request.getId());
//        //判断库存大于0，才能进行秒杀---->防止库存超卖
//        if(goods.getStockCount() < 1){
//            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
//        }
//        //判断该用户是否重复抢购
//        System.out.println(user.getId());
//        System.out.println("判断该用户是否重复抢购");
////        SeckillOrder seckillOrder = seckillOrderService.getSeckillOrder(user.getId(), goods.getGoodsId());
//        //采用 用户id+商品id的唯一索引，解决同一个用户秒杀多件同一商品问题，虽然性能降低但是解决超卖问题
//        // 因为虽然能在这一步判断该用户是否重复抢购 但是如果是高并发场景下两个线程都配置了同一用户，
//        // 同时执行该判断，都判断未抢购，则会出现重复下单情况
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
//        if(seckillOrder != null){
//            System.out.println(seckillOrder.getOrderId());
//            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
//        }
//        Order order= seckillOrderService.add(user, goods);
//        SeckillGoodsRes response = SeckillGoodsRes.init(goods, order);
//        return RespBean.success(response);
//    }


//    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
//    @ResponseBody
//    public RespBean doSeckill(User user, Long id){
//        if(null == user){
//            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
//        }
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(id);
//        //判断库存大于0，才能进行秒杀---->防止库存超卖
//        if(goods.getStockCount() < 1){
//            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
//        }
//        //判断该用户是否重复抢购
//        System.out.println("判断该用户是否重复抢购");
////        SeckillOrder seckillOrder = seckillOrderService.getSeckillOrder(user.getId(), goods.getGoodsId());
//        //采用 用户id+商品id的唯一索引，解决同一个用户秒杀多个商品问题，虽然性能降低但是解决超卖问题
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
//        System.out.println(seckillOrder);
//        if(seckillOrder != null){
//            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
//        }
//        Order order= seckillOrderService.add(user, goods);
//        SeckillGoodsRes response = SeckillGoodsRes.init(goods, order);
//        return RespBean.success(response);
//    }
}
