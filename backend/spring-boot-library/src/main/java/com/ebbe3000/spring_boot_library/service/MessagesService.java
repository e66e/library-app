package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dto.MessageDTO;
import com.ebbe3000.spring_boot_library.entity.Message;
import com.ebbe3000.spring_boot_library.requestmodel.AdminQuestionRequest;
import org.springframework.data.domain.Page;

public interface MessagesService {

    void postMessage(Message messageRequest, String userEmail);

    Page<MessageDTO> getUserMessages(String userMail, int pageNo, int pageSize);

    Page<MessageDTO> getMessagesByClosed(boolean closed, int pageNo, int pageSize);

    void putMessage(AdminQuestionRequest adminQuestionRequest, String userEmail);
}
