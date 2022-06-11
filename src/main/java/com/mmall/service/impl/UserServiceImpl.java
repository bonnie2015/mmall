package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.util.TokenCache;
import com.mmall.util.Md5Util;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    // 使用注解注入bean，mybatis扫描时自动扫描dao层，配置时已忽略此处报错
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        // 校验用户是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        // md5密码登录（不可逆）
        String md5Pwd = Md5Util.getMD5(password);

        // 检验用户名密码正误
        User user = userMapper.selectLogin(username,md5Pwd);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);

    }

    @Override
    public ServerResponse<String> register(User user) {
//        // 校验用户名或邮箱是否存在
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0){
//            return ServerResponse.createByErrorMessage("用户名已存在");
//        }
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0){
//            return ServerResponse.createByErrorMessage("邮箱已存在");
//        }

        ServerResponse validResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        // 不存在 可以使用
        // 设置用户身份
        user.setRole(Const.Role.ROLE_CUSTOMER);
        // 密码Md5加密
        String password = user.getPassword();
        String md5Pwd = Md5Util.getMD5(password);
        user.setPassword(md5Pwd);

        // 插入数据
        int resultCount = userMapper.insert(user);
        // 失败
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        // 成功
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        // 判断str或type是否为空
        if(StringUtils.isNoneBlank(type) ){
            // 分别根据type查询是否存在
            // 存在 则 Error 1 用户已存在
            // 不存在 SUCCESS 校验成功
            if(type.equals(Const.USERNAME)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户已存在");
                }else {
                    return ServerResponse.createBySuccessMessage("校验成功");
                }
            }else if(type.equals(Const.EMAIL)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }else {
                    return ServerResponse.createBySuccessMessage("校验成功");
                }
            }else {
                // 输入的type不合法
                return ServerResponse.createByErrorMessage("参数错误");
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }


    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        // 判断用户是否存在
        ServerResponse<String> checkResponse = checkValid(username,Const.USERNAME);
        if(!checkResponse.isSuccess()){
            // 获取问题
            String resultQuestion = userMapper.selectQuestionByUsername(username);
            // 成功 返回问题
            if(StringUtils.isNotBlank(resultQuestion)){
                return ServerResponse.createBySuccess(resultQuestion);
            }
            // 失败 返回提示信息
            return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
        }
        return ServerResponse.createByErrorMessage("用户不存在");

    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question, String answer) {
        // 验证用户是否存在
        ServerResponse<String> checkResponse = checkValid(username,Const.USERNAME);
        if(checkResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        // 验证答案
        int resultCount = userMapper.checkAnswerByQuestion(username,question,answer);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
        // 生成token
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        // 校验token是否为空
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        // 校验用户名是否为空，为空会有危险
        ServerResponse<String> checkResponse = checkValid(username,Const.USERNAME);
        if(checkResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        // 判断token
        String tokenCache = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(tokenCache)){
            return ServerResponse.createByErrorMessage("token已经失效！");
        }
        if (StringUtils.equals(tokenCache ,forgetToken)){
            String md5Password = Md5Util.getMD5(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新设置");
        }
        return ServerResponse.createByErrorMessage("修改密码操作失效");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // 验证密码，防止横向越权
        Integer userId = user.getId();
        String username = user.getUsername();
        String md5Old = Md5Util.getMD5(passwordOld);
        int resultCount = userMapper.checkPassword(md5Old,userId);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误，重置密码失败");
        }

        String md5New = Md5Util.getMD5(passwordNew);
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            user.setPassword(md5New);
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        // 不能直接更新，需要考虑：
        // 1. 用户名不能更新； 2. email是否已存在，并属于其他用户
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("修改失败，邮箱已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> checkAdmin(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccessMessage("用户是管理员");
        }
        return ServerResponse.createByErrorMessage("用户不是管理员");
    }
}
