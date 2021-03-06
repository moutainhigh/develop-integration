package org.cisiondata.modules.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.auth.entity.Group;
import org.cisiondata.modules.auth.entity.Permission;
import org.cisiondata.modules.auth.entity.Role;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IAuthService;
import org.cisiondata.modules.auth.service.IGroupService;
import org.cisiondata.modules.auth.service.IPermissionService;
import org.cisiondata.modules.auth.service.IResourceService;
import org.cisiondata.modules.auth.service.IRoleService;
import org.cisiondata.modules.auth.service.IUserService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements IAuthService {
	
	@Resource(name = "userService")
	private IUserService userService = null;

	@Resource(name = "roleService")
	private IRoleService roleService = null;
	
	@Resource(name = "groupService")
	private IGroupService groupService = null;
	
	@Resource(name = "resourceService")
	private IResourceService resourceService = null;
	
	@Resource(name = "permissionService")
	private IPermissionService permissionService = null;
	
	@Override
	public User readUserByAccount(String account) throws BusinessException {
		return userService.readUserByAccount(account);
	}

	@Override
	public User readUserByAccountAndPassword(String account, String password) throws BusinessException {
		return userService.readUserByAccountAndPassword(account, password);
	}

	@Override
	public Set<String> readRoleIdentitiesByUserId(Long userId) throws BusinessException {
		Set<String> roleIdentities = roleService.readRoleIdentitiesByUserId(userId);
		List<Group> groups = groupService.readGroupsByUserId(userId);
		List<Role> roles = null;
		for (int i = 0, iLen = groups.size(); i < iLen; i++) {
			roles = roleService.readRolesByGroupId(groups.get(i).getId());
			for (int j = 0, jLen = roles.size(); j < jLen; j++) {
				roleIdentities.add(roles.get(i).getIdentity());
			}
		}
		return roleIdentities;
	}
	
	@Override
	public Set<String> readPermissionIdentitiesByUserId(Long userId) throws BusinessException {
		Set<Permission> allPermissions = new HashSet<Permission>();
		
		List<Permission> userPermissions = permissionService
				.readPermissionsByPrincipalTypeAndPrincipalId(Permission.PRINCIPAL_TYPE_USER, userId);
		allPermissions.addAll(userPermissions);
		
		Set<Role> allRoles = new HashSet<Role>();
		
		List<Group> groups = groupService.readGroupsByUserId(userId);
		List<Role> groupRoles = null;
		List<Permission> groupPermissions = null;
		for (int i = 0, len = groups.size(); i < len; i++) {
			Long groupId = groups.get(i).getId();
			groupRoles = roleService.readRolesByGroupId(groupId);
			allRoles.addAll(groupRoles);
			groupPermissions = permissionService.readPermissionsByPrincipalTypeAndPrincipalId(
					Permission.PRINCIPAL_TYPE_GROUP, groupId);
			allPermissions.addAll(groupPermissions);
		}
		
		List<Role> roles = roleService.readRolesByUserId(userId);
		allRoles.addAll(roles);
		List<Permission> rolePermissions = null;
		for (int i = 0, len = roles.size(); i < len; i++) {
			rolePermissions = permissionService.readPermissionsByPrincipalTypeAndPrincipalId(
					Permission.PRINCIPAL_TYPE_ROLE, roles.get(i).getId());
			allPermissions.addAll(rolePermissions);
		}
		
		Set<String> permissions = new HashSet<String>();
		for (Permission permission : allPermissions) {
			String resourceIdentity = resourceService.readIdentityById(permission.getResourceId());
			List<String> operateIdentities = permission.obtainOperateIdentities();
			for (int i = 0, len = operateIdentities.size(); i < len; i++) {
				permissions.add(resourceIdentity + ":" + operateIdentities.get(i));
			}
		}
		return permissions;
	}
	
	@Override
	public String readResourceReadIdentityByUrl(String url) throws BusinessException {
		String identity = resourceService.readIdentityByUrl(url);
		return StringUtils.isBlank(identity) ? null : identity + ":" + Permission.READ;
	}
	
}
