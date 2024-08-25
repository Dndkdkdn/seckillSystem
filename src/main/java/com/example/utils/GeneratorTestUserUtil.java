package com.example.utils;

import com.example.controller.request.LoginRequest;
import com.example.pojo.User;
import com.example.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: GeneratorTestUserUtil
 * Package: com.example.utils
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/17 15:22
 * @Version 1.0
 */
public class GeneratorTestUserUtil {
    private static void createUser(int count) throws Exception{
        //插入数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        //3.获取连接
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "root";
        Connection con = DriverManager.getConnection(url,user,password);

        String sql = "insert into t_user (id, nickname, password, salt) values(?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        List<User> users = new ArrayList<>();
        for(int i = 0;i < count;++i){
            Long id = 18856010000L + i;
            String nickname = "test" + i;
            User newUser = User.init(id, nickname, "b7797cce01b4b131b433b6acf4add449", "1a2b3c4d");
//            ps.setLong(1, newUser.getId());
//            ps.setString(2, newUser.getNickname());
//            ps.setString(3, newUser.getPassword());
//            ps.setString(4, newUser.getSalt());
//            ps.addBatch();
            users.add(newUser);
        }
//        ps.executeBatch();
//
//        ps.clearParameters();
//        con.close();

        //登录，获取userTicket
        String urlString = "http://localhost:10418/login/doLogin";
        File file = new File("E:\\Learning\\JAVA\\JAVA_project_ideafile\\java_redis_HighConcurrency_seckill\\yuyu-seckill - videoVersion\\config.txt");

        if(file.exists()) file.delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0;i < count;++i) {
            User userInfo = users.get(i);
            URL urll = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)urll.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            co.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStream out = co.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            String jsonParams = mapper.writeValueAsString(new LoginRequest(String.valueOf(userInfo.getId()), MD5Util.inputPassToFromPass("123456")));
//            String params = "mobile="+userInfo.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
            out.write(jsonParams.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0 ,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper1 = new ObjectMapper();
            RespBean respBean = mapper1.readValue(response, RespBean.class);
            System.out.println(respBean);
            String userTicket = (String) respBean.getObj();
            System.out.println(userTicket);
            String row = userInfo.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + userInfo.getId());
        }
        raf.close();
    }
    public static void main(String[] args) throws Exception {
//        System.out.println(idTemp.substring(0, 11));

        createUser(5000);
    }
}
