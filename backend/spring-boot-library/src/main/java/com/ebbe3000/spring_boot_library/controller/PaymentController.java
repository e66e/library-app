package com.ebbe3000.spring_boot_library.controller;

import com.ebbe3000.spring_boot_library.dto.PaymentDTO;
import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.requestmodel.PaymentInfoRequest;
import com.ebbe3000.spring_boot_library.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/search/findByUserEmail")
    public ResponseEntity<PaymentDTO> findPaymentByUserId(@RequestParam("userEmail") String userEmail) {
        PaymentDTO paymentDTO = this.paymentService.getUserPayment(userEmail);

        return new ResponseEntity<>(paymentDTO, HttpStatus.OK);
    }

    @PostMapping("/secure/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest)
            throws StripeException {
        PaymentIntent paymentIntent = this.paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentString = paymentIntent.toJson();

        return new ResponseEntity<>(paymentString, HttpStatus.OK);
    }

    @PutMapping("/secure/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(JwtAuthenticationToken jwtAuthenticationToken) {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("No user credentials for payment.");
        }

        return this.paymentService.stripePayment(userEmail);
    }
}
