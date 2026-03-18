package com.ebbe3000.spring_boot_library.controller;

import com.ebbe3000.spring_boot_library.dto.MessageDTO;
import com.ebbe3000.spring_boot_library.entity.Message;
import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.requestmodel.AdminQuestionRequest;
import com.ebbe3000.spring_boot_library.service.MessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessagesController {

    private final MessagesService messagesService;

    @PostMapping("/secure/add/message")
    public void postMessage(JwtAuthenticationToken jwtAuthenticationToken,
                            @RequestBody Message messageRequest) {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("No jwt token passed");
        }
        this.messagesService.postMessage(messageRequest, userEmail);
    }

    @GetMapping("/secure/findUserMessages")
    public PagedModel<MessageDTO> getAuthenticatedUserMessages(JwtAuthenticationToken jwtAuthenticationToken,
                                                               @RequestParam(name = "page", required = false, defaultValue = "0")
                                                               int pageNo,
                                                               @RequestParam(name = "size", required = false, defaultValue = "5")
                                                               int pageSize) {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("No jwt token passed.");
        }
        Page<MessageDTO> userMessages = this.messagesService.getUserMessages(userEmail, pageNo, pageSize);

        return new PagedModel<>(userMessages);
    }

    @GetMapping("/search/findByClosed")
    public PagedModel<MessageDTO> getClosedMessages(JwtAuthenticationToken jwtAuthenticationToken,
                                                    @RequestParam(name = "closed", required = false, defaultValue = "true")
                                                    boolean closed,
                                                    @RequestParam(name = "page", required = false, defaultValue = "0")
                                                    int pageNo,
                                                    @RequestParam(name = "size", required = false, defaultValue = "5")
                                                    int pageSize) {
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");
        if (roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No user credentials or no admin privileges.");
        }

        Page<MessageDTO> closedMessages = this.messagesService.getMessagesByClosed(closed, pageNo, pageSize);

        return new PagedModel<>(closedMessages);
    }

    @PutMapping("/secure/admin/message")
    public void putMessage(JwtAuthenticationToken jwtAuthenticationToken,
                           @RequestBody AdminQuestionRequest adminQuestionRequest) {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");

        if (userEmail == null || roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No credentials or no admin privileges.");
        }

        this.messagesService.putMessage(adminQuestionRequest, userEmail);
    }

}
