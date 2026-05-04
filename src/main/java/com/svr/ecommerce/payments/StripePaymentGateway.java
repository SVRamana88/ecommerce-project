package com.svr.ecommerce.payments;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.svr.ecommerce.entities.Order;
import com.svr.ecommerce.entities.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;


    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {

            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId= " + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString());

            order.getItems().forEach(orderItem -> {
                var lineItem = createLineItem(orderItem);
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());

            return new CheckoutSession(session.getUrl());

        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            throw new PaymentException();
        }

    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("Stripe-Signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            System.out.println(event.getType());

            return switch (event.getType()) {
                case "payment_intent.succeeded" -> Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));
                case "payment_intent.payment_failed" -> Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                default -> Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }
    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize Stripe event check the SDK and API version.")
        );
        var paymentIntent = (PaymentIntent) stripeObject;
        var orderId = paymentIntent.getMetadata().get("order_id");
        return Long.valueOf(orderId);
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem orderItem) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(orderItem.getQuantity()))
                .setPriceData(createPriceData(orderItem))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem orderItem) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("inr")
                .setUnitAmountDecimal(orderItem.getTotalPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(
                        createProductData(orderItem)
                ).build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem orderItem) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(orderItem.getProduct().getName())
                .build();
    }
}
