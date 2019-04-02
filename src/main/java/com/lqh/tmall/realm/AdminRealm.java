package com.lqh.tmall.realm;

import com.lqh.tmall.pojo.Administrator;
import com.lqh.tmall.service.AdministratorService;
import com.lqh.tmall.util.CustomizedToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminRealm extends AuthorizingRealm {

    @Autowired
    AdministratorService administratorService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        CustomizedToken customizedToken = (CustomizedToken) authenticationToken;
        String adminName = customizedToken.getPrincipal().toString();
        Administrator admin = administratorService.getByName(adminName);
        if(admin == null){
            throw new UnknownAccountException("账号不存在");
        }
        String password = admin.getPassword();
        String salt = admin.getSalt();
        String realmName = getName();
        if (salt == null) {
            salt = new SecureRandomNumberGenerator().nextBytes().toHex();
            password = new SimpleHash("md5", password, salt, 2).toString();
            admin.setPassword(password);
            admin.setSalt(salt);
            administratorService.update(admin);
        }
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(adminName, password, ByteSource.Util.bytes(salt), realmName);
            return authenticationInfo;

    }
}
