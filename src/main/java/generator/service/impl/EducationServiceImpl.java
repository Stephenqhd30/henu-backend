package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.Education;
import generator.service.EducationService;
import com.henu.registration.mapper.EducationMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【education(教育经历表)】的数据库操作Service实现
* @createDate 2025-03-22 13:05:52
*/
@Service
public class EducationServiceImpl extends ServiceImpl<EducationMapper, Education>
    implements EducationService{

}




