package com.ebbe3000.spring_boot_library.controller;

import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.requestmodel.AddBookRequest;
import com.ebbe3000.spring_boot_library.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/secure/add/book")
    public void postBook(JwtAuthenticationToken jwtAuthenticationToken,
                         @RequestBody AddBookRequest addBookRequest) {
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");
        System.out.println("Roles:\n" + roles);
        if (roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No admin credentials.");
        }
        this.adminService.postBook(addBookRequest);
    }

    @PutMapping("/secure/increase/book/quantity")
    public void increaseBookQuantity(JwtAuthenticationToken jwtAuthenticationToken,
                                     @RequestParam Long bookId) {
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");
        if (roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No admin credentials.");
        }

        this.adminService.increaseBookQuantity(bookId);
    }

    @PutMapping("/secure/decrease/book/quantity")
    public void decreaseBookQuantity(JwtAuthenticationToken jwtAuthenticationToken,
                                     @RequestParam Long bookId) {
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");
        if (roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No admin credentials.");
        }

        this.adminService.decreaseBookQuantity(bookId);
    }

    @DeleteMapping("/secure/delete/book")
    public void deleteBook(JwtAuthenticationToken jwtAuthenticationToken,
                           @RequestParam Long bookId) {
        List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("userRoles");
        if (roles == null || !roles.contains("admin")) {
            throw new NoCredentials("No admin credentials.");
        }

        this.adminService.deleteBook(bookId);  
    }
}
