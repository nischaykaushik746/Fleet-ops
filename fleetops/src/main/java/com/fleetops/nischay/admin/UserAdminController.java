package com.fleetops.nischay.admin;

import com.fleetops.nischay.dto.response.UserResponse;
import com.fleetops.nischay.mapper.UserMapper;
import com.fleetops.nischay.role.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final UserMapper userMapper;

    @PutMapping("/{id}/role")
    public UserResponse assignRole(@PathVariable Long id,
                                   @RequestParam RoleType role) {
        return userMapper.toResponse(userAdminService.assignRole(id, role));
    }

    @DeleteMapping("/{id}/role")
    public UserResponse removeRole(@PathVariable Long id,
                                   @RequestParam RoleType role) {
        return userMapper.toResponse(userAdminService.removeRole(id, role));
    }

    @PutMapping("/{id}/disable")
    public UserResponse disableUser(@PathVariable Long id) {
        return userMapper.toResponse(userAdminService.disableUser(id));
    }

    @PutMapping("/{id}/enable")
    public UserResponse enableUser(@PathVariable Long id) {
        return userMapper.toResponse(userAdminService.enableUser(id));
    }
}