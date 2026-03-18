package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.HistoryRepository;
import com.ebbe3000.spring_boot_library.dto.HistoryDTO;
import com.ebbe3000.spring_boot_library.entity.Book;
import com.ebbe3000.spring_boot_library.entity.History;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public Page<HistoryDTO> getUserHistories(String userEmail, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<History> histories = this.historyRepository.findBooksByUserEmail(userEmail, pageable);

        return new PageImpl<>(histories.stream().map(History::mapToDTO).toList(),
                              histories.getPageable(),
                              histories.getTotalElements());
    }
}
