package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.MessageNotice;
import generator.service.MessageNoticeService;
import com.henu.registration.mapper.MessageNoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【message_notice(消息通知表)】的数据库操作Service实现
* @createDate 2025-03-24 00:56:26
*/
@Service
public class MessageNoticeServiceImpl extends ServiceImpl<MessageNoticeMapper, MessageNotice>
    implements MessageNoticeService{

}




