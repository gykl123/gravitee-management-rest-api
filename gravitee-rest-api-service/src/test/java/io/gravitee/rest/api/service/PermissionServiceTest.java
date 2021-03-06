/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service;

import io.gravitee.repository.management.model.MembershipReferenceType;
import io.gravitee.repository.management.model.RoleScope;
import io.gravitee.rest.api.model.RoleEntity;
import io.gravitee.rest.api.model.UserEntity;
import io.gravitee.rest.api.model.UserRoleEntity;
import io.gravitee.rest.api.model.permissions.ApiPermission;
import io.gravitee.rest.api.service.impl.PermissionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService = new PermissionServiceImpl();

    @Mock
    protected UserService userService;

    @Mock
    protected MembershipService membershipService;

    private static final String USER_NAME = "username";
    
    @Test
    public void shouldGetConfigurationWithManagementOrPortalRoles() {
        reset(userService);
        reset(membershipService);
        UserEntity user = new UserEntity();
        user.setRoles(Collections.singleton(new UserRoleEntity()));
        doReturn(user).when(userService).findByIdWithRoles(USER_NAME);
        
        assertTrue(permissionService.hasManagementRights(USER_NAME));
    }

    @Test
    public void shouldGetConfigurationWithAPIRolesWithCUDPermissions() {
        reset(userService);
        reset(membershipService);
        UserEntity user = new UserEntity();
        user.setRoles(Collections.emptySet());
        doReturn(user).when(userService).findByIdWithRoles(USER_NAME);
        
        Map<String, char[]> perms = new HashMap<>();
        perms.put(ApiPermission.ALERT.name(), new char[] {'R'});
        perms.put(ApiPermission.ANALYTICS.name(), new char[] {'C', 'D'});

        RoleEntity apiRole = new RoleEntity();
        apiRole.setPermissions(perms);
        doReturn(Collections.singleton(apiRole)).when(membershipService).getRoles(eq(MembershipReferenceType.API),any(), eq(USER_NAME), eq(RoleScope.API));

        assertTrue(permissionService.hasManagementRights(USER_NAME));
    }

    @Test
    public void shouldGetConfigurationWithoutManagementPartBecauseOfRatingPermission() {
        reset(userService);
        reset(membershipService);
        UserEntity user = new UserEntity();
        user.setRoles(Collections.emptySet());
        doReturn(user).when(userService).findByIdWithRoles(USER_NAME);
        
        Map<String, char[]> perms = new HashMap<>();
        perms.put(ApiPermission.RATING.name(), new char[] {'C', 'U'});
        RoleEntity apiRole = new RoleEntity();
        apiRole.setPermissions(perms);
        doReturn(Collections.singleton(apiRole)).when(membershipService).getRoles(eq(MembershipReferenceType.API),any(), eq(USER_NAME), eq(RoleScope.API));
        
        assertFalse(permissionService.hasManagementRights(USER_NAME));
    }

    @Test
    public void shouldGetConfigurationWithoutManagementPartBecauseOfReadPermission() {
        reset(userService);
        reset(membershipService);
        UserEntity user = new UserEntity();
        user.setRoles(Collections.emptySet());
        doReturn(user).when(userService).findByIdWithRoles(USER_NAME);
        
        Map<String, char[]> perms = new HashMap<>();
        perms.put(ApiPermission.ALERT.name(), new char[] {'R'});
        RoleEntity apiRole = new RoleEntity();
        apiRole.setPermissions(perms);
        doReturn(Collections.singleton(apiRole)).when(membershipService).getRoles(eq(MembershipReferenceType.API),any(), eq(USER_NAME), eq(RoleScope.API));
        
        assertFalse(permissionService.hasManagementRights(USER_NAME));
    }

}