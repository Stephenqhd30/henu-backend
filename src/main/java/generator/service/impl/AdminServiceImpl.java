package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.Admin;
import generator.service.AdminService;
import com.henu.registration.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【admin】的数据库操作Service实现
* @createDate 2025-03-20 23:25:33
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




