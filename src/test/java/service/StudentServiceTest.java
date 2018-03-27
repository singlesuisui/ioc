package service;
import com.sofia.annotation.Autowired;
import com.sofia.core.DefaultServlet;
import com.sofia.service.StudentService;
import com.sofia.service.Teacher;
import com.sofia.service.impl.TeacherServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by yingbo.gu on 2018-03-21.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class StudentServiceTest {

    @Test
    public void selfIntroduction() throws Exception{

        DefaultServlet configResolver = new DefaultServlet();
        configResolver.init();
//        configResolver.getBean("studentService");
        configResolver.printBeanFactory();
        Teacher teacher = new TeacherServiceImpl();
        teacher.callStudent();
    }
}
