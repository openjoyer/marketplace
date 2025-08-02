package com.openjoyer.marketplace.profile_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

import static com.openjoyer.marketplace.profile_service.model.Permission.*;

@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE,
                    SELLER_CREATE,
                    SELLER_READ,
                    SELLER_DELETE,
                    SELLER_UPDATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    SELLER(
            Set.of(
                    SELLER_CREATE,
                    SELLER_READ,
                    SELLER_DELETE,
                    SELLER_UPDATE
            )
    );

    @Getter
    private final Set<Permission> permissions;
}
