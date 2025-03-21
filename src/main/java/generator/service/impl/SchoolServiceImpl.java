package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.School;
import generator.service.SchoolService;
import com.henu.registration.mapper.SchoolMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【school(高校信息)】的数据库操作Service实现
* @createDate 2025-03-21 11:09:15
*/
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
    implements SchoolService{

}




