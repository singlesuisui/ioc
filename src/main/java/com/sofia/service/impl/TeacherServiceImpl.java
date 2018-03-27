package com.sofia.service.impl;

import com.sofia.annotation.Autowired;
import com.sofia.annotation.Comment;
import com.sofia.service.StudentService;
import com.sofia.service.Teacher;
import org.springframework.stereotype.Component;

/**
 * Created by yingbo.gu on 2018-03-25.
 */
@Comment(name = "teacherService")
public class TeacherServiceImpl implements Teacher{

    @Autowired
    private StudentService studentService;

    public void callStudent() {
        studentService.selfIntroduction();
    }
}
