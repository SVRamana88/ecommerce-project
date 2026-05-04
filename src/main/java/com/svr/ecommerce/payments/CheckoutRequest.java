package com.svr.ecommerce.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutRequest {
    @NotNull(message = "CartId is Required")
    private UUID cartId;
}
