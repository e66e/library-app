package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dto.HistoryDTO;
import org.springframework.data.domain.Page;

public interface HistoryService {

    Page<HistoryDTO> getUserHistories(String userEmail, int pageNo, int pageSize);
}
