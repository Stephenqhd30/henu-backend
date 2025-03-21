package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.SchoolType;
import generator.service.SchoolTypeService;
import com.henu.registration.mapper.SchoolTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【school_type(高校类型)】的数据库操作Service实现
* @createDate 2025-03-21 11:30:25
*/
@Service
public class SchoolTypeServiceImpl extends ServiceImpl<SchoolTypeMapper, SchoolType>
    implements SchoolTypeService{

}




