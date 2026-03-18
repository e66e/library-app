package com.ebbe3000.spring_boot_library.controller;

import com.ebbe3000.spring_boot_library.dto.HistoryDTO;
import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/histories")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/secure/search/findBooksByUserEmail")
    public PagedModel<HistoryDTO> getUserBookLoanHistory(JwtAuthenticationToken jwtAuthenticationToken,
                                                         @RequestParam(name = "page", required = false, defaultValue = "0")
                                                         int pageNo,
                                                         @RequestParam(name = "size", required = false, defaultValue = "5")
                                                         int pageSize) {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("Email not found.");
        }
        Page<HistoryDTO> histories = this.historyService.getUserHistories(userEmail, pageNo, pageSize);

        return new PagedModel<>(histories);
    }
}
