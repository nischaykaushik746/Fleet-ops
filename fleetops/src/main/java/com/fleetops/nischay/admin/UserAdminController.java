package com.fleetops.nischay.admin;

import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PutMapping("/{id}/role")
    public User assignRole(@PathVariable Long id,
                           @RequestParam RoleType role) {
        return userAdminService.assignRole(id, role);
    }
}