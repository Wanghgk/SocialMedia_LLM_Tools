package com.wanghgk.crawlsever.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ZhiHuMapper {
    @Insert("insert into question(id,content,information,labels,update_time) values(#{id},#{content},#{information},#{labels},now())")
    void addQuestion(BigInteger id, String content, String information, String labels);

    @Insert("insert into answer(id,question_id,content,support,thanks,create_time,update_time) values(#{id},#{questionId},#{content},#{support},#{thanks},#{createTime},#{updateTime})")
    void addAnswer(BigInteger id, BigInteger questionId, String content, Integer support, Integer thanks, Integer createTime, Integer updateTime);

    @Select("select content from question where id=#{id}")
    String getContentById(BigInteger id);

    @Select("select to_update from question where id=#{id}")
    boolean getToUpdateById(BigInteger id);

    @Update("update question set content=#{content},information=#{information},update_time=now() where id=#{id}")
    void updateQuestion(BigInteger id, String content, String information);

    @Update("update question set update_time=now() where id=#{id}")
    void updateQuestionUpdateTime(BigInteger id);

    @Select("select update_time from answer where id=#{id}")
    Integer getUpdateTime(BigInteger id);

    @Update("UPDATE answer SET content=#{content},support=#{support},thanks = #{thanks},update_time=#{updateTime} WHERE (id = #{id}) and (question_id = #{questionId})")
    void updateAnswer(BigInteger id, BigInteger questionId, String content, Integer support, Integer thanks, Integer updateTime);

    @Select("select id from question where to_update=1")
    List<BigInteger> getQuestionIds();

}
