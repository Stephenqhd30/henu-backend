package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.Family;
import generator.service.FamilyService;
import com.henu.registration.mapper.FamilyMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【family(家庭关系表)】的数据库操作Service实现
* @createDate 2025-03-23 00:15:52
*/
@Service
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, Family>
    implements FamilyService{

}




