package com.openjoyer.order_service.feign_clients;

import com.openjoyer.order_service.model.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("profile-service")
public interface ProfileServiceClient {

    @GetMapping("/api/profile/email/get")
    String getProfileEmail(@RequestHeader("X-User-Id") String id);

    @GetMapping("/api/profile/address")
    Address getAddress(@RequestHeader("X-User-Id") String id);
}
