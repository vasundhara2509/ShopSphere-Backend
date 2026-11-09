
package com.shopsphere.address.controller;

import com.shopsphere.address.dto.AddressRequest;
import com.shopsphere.address.dto.AddressResponse;
import com.shopsphere.address.service.AddressService;
import com.shopsphere.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService,
                             UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    // 1. Add Address
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        AddressResponse response =
                addressService.addAddress(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get All Addresses
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        return ResponseEntity.ok(
                addressService.getMyAddresses(userId)
        );
    }

    // 3. Update Address
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        AddressResponse response =
                addressService.updateAddress(userId, addressId, request);

        return ResponseEntity.ok(response);
    }

    // 4. Delete Address
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        addressService.deleteAddress(userId, addressId);

        return ResponseEntity.noContent().build();
    }
}
