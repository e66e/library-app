package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.MessageRepository;
import com.ebbe3000.spring_boot_library.dto.MessageDTO;
import com.ebbe3000.spring_boot_library.entity.Message;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import com.ebbe3000.spring_boot_library.requestmodel.AdminQuestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void postMessage(Message messageRequest, String userEmail) {
        Message message = new Message(messageRequest.getTitle(), messageRequest.getQuestion());
        message.setUserEmail(userEmail);
        this.messageRepository.save(message);
    }

    @Override
    public Page<MessageDTO> getUserMessages(String userMail, int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<Message> messages = this.messageRepository.findByUserEmail(userMail, pageable);

        return new PageImpl<>(messages.map(Message::mapToDTO).toList(),
                              messages.getPageable(), messages.getTotalElements());
    }

    @Override
    public Page<MessageDTO> getMessagesByClosed(boolean closed, int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<Message> messages = this.messageRepository.findByClosed(closed, pageable);

        return new PageImpl<>(messages.map(Message::mapToDTO).toList(),
                              messages.getPageable(), messages.getTotalElements());
    }

    @Override
    @Transactional
    public void putMessage(AdminQuestionRequest adminQuestionRequest, String userEmail) {
        Optional<Message> message = this.messageRepository.findById(adminQuestionRequest.id());

        if (message.isEmpty()) {
            throw new ResourceNotFoundException("Message not found.");
        }

        message.get().setAdminEmail(userEmail);
        message.get().setResponse(adminQuestionRequest.response());
        message.get().setClosed(true);
        this.messageRepository.save(message.get());
    }
}
