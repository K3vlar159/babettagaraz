package sk.babettagaraz.domain.listing.dto;

import sk.babettagaraz.domain.listing.ListingStatus;

import java.time.LocalDateTime;

public record ListingResponse(

        Long id,
        Long sellerId,
        String sellerUsername,
        String title,
        String description,
        Integer price,
        String contactNumber,
        String contactEmail,
        ListingStatus status,
        LocalDateTime createdAt
) {
}
