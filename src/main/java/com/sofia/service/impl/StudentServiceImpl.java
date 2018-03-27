package com.sofia.service.impl;

import com.sofia.annotation.Comment;
import com.sofia.domain.Student;
import com.sofia.service.StudentService;

/**
 * Created by yingbo.gu on 2018-03-21.
 */
@Comment(name="studentService")
public class StudentServiceImpl implements StudentService{

    /**
     * @author: yingbo.gu
     * @Description:
     * @date 2018-03-21 11:29
     */
    public void selfIntroduction() {
        Student student = new Student();
        student.setName("李明");
        student.setAge(7);
        student.setNationality("中国");
        System.out.println("我叫"+student.getName()+",今年"+student.getAge()+"岁,来自"+student.getNationality());
    }
}
