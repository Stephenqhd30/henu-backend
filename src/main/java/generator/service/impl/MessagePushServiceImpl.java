package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.model.entity.MessagePush;
import generator.service.MessagePushService;
import com.henu.registration.mapper.MessagePushMapper;
import org.springframework.stereotype.Service;

/**
* @author stephenqiu
* @description 针对表【message_push(消息推送表)】的数据库操作Service实现
* @createDate 2025-03-28 00:11:11
*/
@Service
public class MessagePushServiceImpl extends ServiceImpl<MessagePushMapper, MessagePush>
    implements MessagePushService{

}




