package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.awt.print.PrinterGraphics;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkUsername(String username);

    int checkEmail(String email);

    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);

    int checkAnswerByQuestion(@Param("username")String username, @Param("question") String question, @Param("answer") String answer);

    int checkPassword(@Param("passwordOld")String passwordOld,@Param("userId") Integer userId);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUsername(String username);

}