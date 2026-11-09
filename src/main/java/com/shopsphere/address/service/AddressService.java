
package com.shopsphere.address.service;

import com.shopsphere.address.Address;
import com.shopsphere.address.dto.AddressRequest;
import com.shopsphere.address.dto.AddressResponse;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.user.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // 1. Add Address
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> existingAddresses =
                addressRepository.findByUserId(userId);

        Address address = new Address();

        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setLandmark(request.getLandmark());

        boolean makeDefault = request.isDefaultAddress()
                || existingAddresses.isEmpty();

        if (makeDefault) {
            existingAddresses.forEach(existing ->
                    existing.setDefaultAddress(false));

            addressRepository.saveAll(existingAddresses);
        }

        address.setDefaultAddress(makeDefault);

        Address savedAddress = addressRepository.save(address);

        return convertToResponse(savedAddress);
    }

    // 2. Get All Addresses
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long userId) {

        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 3. Update Address
    @Transactional
    public AddressResponse updateAddress(
            Long userId, Long addressId, AddressRequest request) {

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        if (request.isDefaultAddress()) {

            List<Address> existingAddresses =
                    addressRepository.findByUserId(userId);

            existingAddresses.forEach(existing ->
                    existing.setDefaultAddress(false));

            addressRepository.saveAll(existingAddresses);
        }

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setLandmark(request.getLandmark());
        address.setDefaultAddress(request.isDefaultAddress());

        Address updatedAddress = addressRepository.save(address);

        return convertToResponse(updatedAddress);
    }

    // 4. Delete Address
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        addressRepository.delete(address);
    }

    // 5. Convert Entity to Response DTO
    private AddressResponse convertToResponse(Address address) {

        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getLandmark(),
                address.isDefaultAddress(),
                address.getCreatedAt()
        );
    }
}
