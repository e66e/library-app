package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.PaymentRepository;
import com.ebbe3000.spring_boot_library.dto.PaymentDTO;
import com.ebbe3000.spring_boot_library.entity.Payment;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import com.ebbe3000.spring_boot_library.requestmodel.PaymentInfoRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.amount());
        params.put("currency", paymentInfoRequest.currency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    @Override
    @Transactional
    public ResponseEntity<String> stripePayment(String userEmail) {
        Optional<Payment> payment = this.paymentRepository.findByUserEmail(userEmail);

        if (payment.isEmpty()) {
            throw new ResourceNotFoundException("Payment information is missing.");
        }

        payment.get().setAmount(BigDecimal.ZERO);
        this.paymentRepository.save(payment.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public PaymentDTO getUserPayment(String userEmail) {
        Optional<Payment> userPayment = this.paymentRepository.findByUserEmail(userEmail);

        if (userPayment.isEmpty()) {
            throw new ResourceNotFoundException("User payments not found.");
        }

        return userPayment.get().mapToDTO();
    }
}
