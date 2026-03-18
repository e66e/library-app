package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dto.PaymentDTO;
import com.ebbe3000.spring_boot_library.requestmodel.PaymentInfoRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface PaymentService {

    PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException;

    ResponseEntity<String> stripePayment(String userEmail);

    PaymentDTO getUserPayment(String userEmail);
}
