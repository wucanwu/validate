package com.lepao.validate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lepao.validate.entity.*;
import com.lepao.validate.util.AsigoUtil;
import com.lepao.validate.util.BaseEnAndDeUtil;
import com.lepao.validate.util.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Lazy(false)
public class VadidateService {
    String appKey = "2019101515362111803";
    String appSecret = "c6nz6secztpz558d4xn838dmvgchxb2b";
    String accessToken = "TbAldsfx5445w83zxbewbwprkmmxv8sanrk4343s2pwhebvyp8";
    String username = "5b72566775a03c3fadbbe0c9";
    String password = "lWWuek1ZOxsJfIeZ69zplSOXBqIWX4PP";
    private boolean flag;


    @Value("${service.onemonth}")
    private double month;
    @Value("${service.threemonth}")
    private double threeMonth;
    @Value("${service.halfmonth}")
    private double sixMonth;
    @Value("${service.year}")
    private double oneYear;

    @Autowired
    private JavaMailSender mailSender;

    //每天下午6天触发




    @Scheduled(fixedRate = 1000)
    @Async
    public void job2()
    {
        System.out.println("我自信了");
    }
    //
    @Scheduled(cron = "0 48 8 * * ?")
    @Async
    public void validate1(String token) {
        List<ResultMsg> succeesList = new LinkedList<>();
        List<ResultMsg> falseMsg = new LinkedList<>();
        flag = true;
        int pageNo = 1;
        List<Order> count = new LinkedList<>();
        while (flag) {
            //得到所有带发货的订单
            Result result = select(pageNo,token);
            //得到对应表单的id
            Form formSheBao = getAllForm("2019年社保处理订单");
            Form formGeShui = getAllForm("2019年个税处理订单");
            Form fromGongJiJing = getAllForm("2019年公积金处理订单");
            Map<String,Double>  map = new HashMap<>();
            for (Order order : result.getData()) {
                if(map.get(order.getBuyerNick())==null)
                {
                    System.out.println(order.getBuyerNick());
                    map.put(order.getBuyerNick(),Double.parseDouble(order.getTotalFee()));
                }else{
                    double temp= map.get(order.getBuyerNick());
                    temp+=Double.parseDouble(order.getTotalFee());
                    map.put(order.getBuyerNick(),temp);
                }
            }
            System.out.println("--------------------------------");
            for(Map.Entry<String,Double> entry:map.entrySet())
            {
                System.out.println(entry.getKey()+"     "+entry.getValue());
            }
            System.out.println("---------------------------------");
            for (Order order : result.getData()) {
                String sheBaoUser = getFormContent(formSheBao.getId(), constr("field1", order.getBuyerNick()));
                String formGeUser = getFormContent(formGeShui.getId(), constr("field1", order.getBuyerNick()));
                String formGongJiJingUser = getFormContent(fromGongJiJing.getId(), constr("field1", order.getBuyerNick()));

                JSONObject sheBaoObject = JSONObject.parseObject(sheBaoUser);
                JSONObject formGeObject = JSONObject.parseObject(formGeUser);
                JSONObject formGongJiJingObject = JSONObject.parseObject(formGongJiJingUser);
                //判断总价是否相等
                //获取这个用户boss表单的服务费和基数费
                double countMoney=0;
                //获取社保用户服务费和基数费
                JSONObject sheBaoJsonObject = sheBaoObject.getJSONArray("rows").getJSONObject(0);
                //获取服务费和基数费
                String tempStr = speciMoney(sheBaoJsonObject.getString("field11"));
                double sheBaoFuwuMoney = Double.parseDouble(tempStr);
                countMoney+=sheBaoFuwuMoney;
                //获取社保服务费到期时间
                long endTime = sheBaoJsonObject.getLong("field25");

                if(endTime<addMonth(new Date(),1))
                {
                    double sheBaoJiShuMoney = Double.parseDouble(sheBaoJsonObject.getString("field12"));
                    countMoney+=sheBaoJiShuMoney;
                }


                //系统处理不
                if(formGeObject.getJSONArray("rows").size() != 0)
                {
                    JSONObject geShuiJsonObject = formGeObject.getJSONArray("rows").getJSONObject(0);
                    double geShuiFuwuMoney = Double.parseDouble(geShuiJsonObject.getString("field11"));
                    countMoney+=geShuiFuwuMoney;
                    endTime = geShuiJsonObject.getLong("field25");
                    if(endTime<addMonth(new Date(),1))
                    {
                        double geShuiJiShuMoney = Double.parseDouble(geShuiJsonObject.getString("field12"));
                        countMoney+=geShuiJiShuMoney;
                    }

                }
                if(formGongJiJingObject.getJSONArray("rows").size() != 0)
                {
                    JSONObject gongJiJIngJsonObject = formGongJiJingObject.getJSONArray("rows").getJSONObject(0);
                    tempStr = speciMoney(gongJiJIngJsonObject.getString("field11"));
                    double gongJiJIngFuwuMoney = Double.parseDouble(tempStr);
                    countMoney+=gongJiJIngFuwuMoney;
                    endTime = gongJiJIngJsonObject.getLong("field25");
                    if(endTime<addMonth(new Date(),1)) {
                        double gongJiJingJiShuMoney = Double.parseDouble(gongJiJIngJsonObject.getString("field12"));
                        countMoney += gongJiJingJiShuMoney;
                    }
                }
                //相等
                if(map.get(order.getBuyerNick())==countMoney)
                {
                    succeesList.add(new ResultMsg().setTid(order.getTid()).setResultMsg("总价相等").setUserName(order.getBuyerNick()).setSellerName(order.getSellerNick()));
                    //成功的更改发货状态
                   // modify(order.getTid());
                }else{
                    falseMsg.add(new ResultMsg().setTid(order.getTid()).setResultMsg("总价不相等").setUserName(order.getBuyerNick()).setSellerName(order.getSellerNick()));
                }
                System.out.println(order.getBuyerNick()+"       "+countMoney);
            }
            falseMsg = removeDouble(falseMsg);
            succeesList = removeDouble(succeesList);
            send(falseMsg, "发货不成功的记录", "wucanwu123@gmail.com");
            send(falseMsg, "发货不成功的记录", "1274189606@qq.com");
            send(falseMsg, "发货不成功的记录", "386818779@qq.com");
            send(succeesList, "发货成功的记录", "wucanwu123@gmail.com");
            send(succeesList, "发货成功的记录", "1274189606@qq.com");
            send(succeesList, "发货成功的记录", "386818779@qq.com");
        }
    }

    public List<Order> selectDouble(List<Order> order) {
        List<Order> resultOrder = new LinkedList<>();
        Map<String, Integer> map = new HashMap<>();
        //找寻
        for (int i = 0; i < order.size(); i++) {
            if (map.get(order.get(i).getBuyerNick()) == null) {
                map.put(order.get(i).getBuyerNick(), 1);
            } else {
                Integer count = map.get(order.get(i).getBuyerNick());
                count++;
                map.put(order.get(i).getBuyerNick(), count);
            }
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 2) {
                //搜索里面的order订单
                for (int i = 0; i < order.size(); i++) {
                    if (order.get(i).getBuyerNick().equals(entry.getKey())) {
                        resultOrder.add(order.get(i));
                    }
                }
            }
        }
        //连续的订单放在一起
        return resultOrder;
    }
    //构造过滤字段
    public JSONObject constr(String field, String user) {
        //quboss表单查询这对应淘宝用户的消息
        JSONObject filedJson = new JSONObject();
        //"field1"
        filedJson.put("field", field);
        filedJson.put("compare_type", "eq");
        filedJson.put("data_type", "string");
        filedJson.put("value", user);
        return filedJson;
    }

    //规范数字里面的小数点
    public String speciMoney(String errorMoney) {
        int j = 0;
        StringBuilder trueMoney = new StringBuilder();

        for (int i = 0; i < errorMoney.length(); i++) {
            if (errorMoney.charAt(i) < '0' || errorMoney.charAt(i) > '9') {
                trueMoney.append(".");
                j++;
            } else {
                trueMoney.append(errorMoney.charAt(i));
            }
        }
        if (j <= 1) {
            return trueMoney.toString();
        }
        return "";
    }
    public void send(List<ResultMsg> falseMsg, String title, String toEmail) {

        SimpleMailMessage simpleFalseMailMessage = new SimpleMailMessage();
        simpleFalseMailMessage.setSubject(title);
        StringBuilder falseBuilder = new StringBuilder();
        for (ResultMsg temp : falseMsg) {
            falseBuilder.append(temp.getSellerName()).append("     ").append(temp.getUserName()).append("   ").append(temp.getResultMsg()).append("\n");
        }
        simpleFalseMailMessage.setText(falseBuilder.toString());
        simpleFalseMailMessage.setTo(toEmail);
        simpleFalseMailMessage.setFrom("1793629207@qq.com");
        mailSender.send(simpleFalseMailMessage);
    }
    //进行集合去重
    public List<ResultMsg> removeDouble(List<ResultMsg> doubleList)
    {
        List<ResultMsg> oneList = new LinkedList<>();
        for(ResultMsg temp:doubleList)
        {
            if(oneList.size()==0)
            {
                oneList.add(temp);
            }else {
                int temp1 = oneList.size();
                int i=0;
                for (i = 0; i < temp1; i++) {
                    if (temp.getUserName().equals(oneList.get(i).getUserName())) {
                        break;
                    }
                }
                if(i==temp1)
                {
                    oneList.add(temp);
                }
            }
        }
        return oneList;
    }

    //得到需要几个月
    public int getMonth(double money) {
        if (money == month||money==60) {
            return 1;
        } else if (money == threeMonth) {
            return 3;
        } else if (money == sixMonth) {
            return 6;
        } else if (money == oneYear) {
            return 12;
        } else {
            return 0;
        }
    }
    //月份相加
    public long addMonth(Date time, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, num);
        Date newTime = calendar.getTime();
        return newTime.getTime();
    }
    public void getAllSales(String token) {
        try {
            Map<String, String> map = getMap();
            map.put("fields", "num_iid,title,nick");
            String sign = AsigoUtil.Sign(map, appSecret);
            System.out.println(sign);
            String param = "timestamp=" + map.get("timestamp") + "&sign=" + sign + "&fields=" + map.get("fields");
            System.out.println(param);
            String str = HttpRequestUtil.sendPostByAsigh("http://gw.api.agiso.com/alds/Item/OnSaleGet", param, token);
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Result select(int pageNo,String token) {
        Map<String, String> map = getMap();
        String date = dateFormat();
        String secondDayBefore = date.substring(0, date.length() - 1) + (Integer.parseInt(String.valueOf(date.charAt(9))) - 2);
        System.out.println(secondDayBefore);
        map.put("modifyTimeStart", secondDayBefore);
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", "100");
        try {
            String sign = AsigoUtil.Sign(map, appSecret);
            System.out.println(sign);
            String param = "timestamp=" + map.get("timestamp") + "&sign=" + sign + "&pageNo=" + map.get("pageNo") + "&pageSize=" + map.get("pageSize") + "&modifyTimeStart=" + map.get("modifyTimeStart");
            System.out.println(param);
            String str = HttpRequestUtil.sendPostByAsigh("http://gw.api.agiso.com/alds/Trade/TradesBuyerPay", param, token);
            Result result = (Result) JSON.toJavaObject(JSON.parseObject(str), Result.class);
            if (result.getData().size() < 100) {
                //没有数据了
                flag = false;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        return map;
    }

    public void modify(String tid,String token) {

        Map<String, String> map = getMap();
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("tids", tid);
        try {
            String sign = AsigoUtil.Sign(map, appSecret);
            System.out.println(sign);
            //拼接参数
            String param = "timestamp=" + map.get("timestamp") + "&sign=" + sign + "&tids=" + map.get("tids");
            System.out.println(HttpRequestUtil.sendPostByAsigh("http://gw.api.agiso.com/alds/Trade/LogisticsDummySend", param, token));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String dateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;
        try {
            date = format.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public Form getAllForm(String title) {
        //生成base64编码
        String code = BaseEnAndDeUtil.encode(username + ":" + password);

        String url = "http://api.jsform.com/api/v1/formlist/all";

        String str = HttpRequestUtil.sendGet(url, code);

        JSONObject jsonObject = JSONObject.parseObject(str);

        JSONArray jsonArray = jsonObject.getJSONArray("rows");

        int i = 0;
        Form form = null;
        while (jsonArray.getJSONObject(i) != null) {
            if (jsonArray.getJSONObject(i).getString("form_name").equals(title)) {
                form = JSON.toJavaObject(jsonArray.getJSONObject(i), Form.class);
                System.out.println(form);
                break;
            }
            i++;
        }
        return form;
    }


    public String getFormContent(String id, JSONObject fidldJson) {
        System.out.println(id);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("form_id", id);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(fidldJson);
        jsonObject.put("filters", jsonArray);
        String code = BaseEnAndDeUtil.encode(username + ":" + password);

        String url = "http://api.jsform.com/api/v1/entry/query";
        System.out.println(jsonObject.toJSONString());
        String str = HttpRequestUtil.sendPost(url, jsonObject.toJSONString(), code);
        System.out.println(str);
        return str;

    }


}
