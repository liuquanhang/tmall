tmall_springboot
===============
简介
--------------------
    使用springboot模仿天猫网站
    采用spring,springMVC,springboot框架。数据层采用JPA。
    数据库采用mysql。
    redis作为缓存服务器。
    前端采用html, CSS, Javascript, JSON, AJAX, JQuery ,Bootstrap, Vue等。
    restful风格设计，采用axios实现前后端数据传输。
    采用shiro实现登陆功能.
    elasticsearch实现搜索功能，可实现重复条件搜索无需访问数据库。
    预计加入nginx反向代理。
    实现后台分类展示商品，添加和修改商品，包括上传商品图片。添加和修改商品属性，查看用户列表等。
-----------------------------
Result类用来向前端返回json对象信息，实现了restful的设计风格,使代码更加整齐。
```java
public class Result {

    private static int SUCCESS_CODE = 0;
    private static int FAIL_CODE = 1;

    private int code;
    private String message;
    private Object data;

    private Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(SUCCESS_CODE, null, null);
    }

    public static Result success(Object data) {
        return new Result(SUCCESS_CODE, "", data);
    }

    public static Result fail(String message) {
        return new Result(FAIL_CODE, message, null);
    }

```
本网站的登陆功能包括前台用户和后台管理员登陆。

管理员realm（这里我没有加入登陆功能,而是在数据库中直接加入管理员账号后再登陆时生成salt，加密密码后覆盖原来的数据库中密码）
```java
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
```
实现用户登录的realm
```java
public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        CustomizedToken customizedToken = (CustomizedToken) authenticationToken;
        String userName = customizedToken.getPrincipal().toString();
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        String realmName = getName();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt), realmName);
        return authenticationInfo;
    }
```
继承ModularRealmAuthenticator,实现根据登陆令牌不同选择不同的realm。
```java
public class CustomizedModularRealmAuthenticator extends ModularRealmAuthenticator {
    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        // 强制转换回自定义的CustomizedToken
        CustomizedToken customizedToken = (CustomizedToken) authenticationToken;
        // 登录类型
        String loginType = customizedToken.getLoginType();
        // 所有Realm
        Collection<Realm> realms = getRealms();
        // 登录类型对应的所有Realm
        Collection<Realm> typeRealms = new ArrayList<>();
        for(Realm realm:realms) {
            if (realm.getName().contains(loginType)) {
                typeRealms.add(realm);
            }
        }
            if(typeRealms.size()==1){
                return doSingleRealmAuthentication(typeRealms.iterator().next(),customizedToken);
            }else{
                return doMultiRealmAuthentication(typeRealms,customizedToken);
            }
    }
}
```
继承UsernamePasswordToken类,实现自己的令牌类 
```java 
public class CustomizedToken extends UsernamePasswordToken {

    private String loginType;

    public CustomizedToken(final String name,final String password,String loginType){
        super(name,password);
        this.loginType =loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
```
为不同登陆类型赋值的枚举类
```java
public enum LoginType {
    /**
     * 用户类型登录
     */
    USER("User"),
    /**
     * 管理员类型登陆
     */
    ADMIN("Admin");

    private String type;

    private LoginType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
```
在shiro配置类中配置多realm
```java
@Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(modularRealmAuthenticator());
        List<Realm> realms = new ArrayList<>();
        realms.add(getUserRealm());
        realms.add(getAdminRealm());
        securityManager.setRealms(realms);
        return securityManager;
    }
```

我选择的策略为FirstSuccessfulStrategy，只有一个realm生效即可。

```java
    @Bean
    public CustomizedModularRealmAuthenticator modularRealmAuthenticator(){
        CustomizedModularRealmAuthenticator modularRealmAuthenticator = new CustomizedModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return modularRealmAuthenticator;
    }
```

在Controller中就可以根据登陆类型不同进行处理,以AdminController为例：

```java
    private static final String USER_LOGIN_TYPE = LoginType.USER.toString();
    
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        String password = userParam.getPassword();
        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new CustomizedToken(name, password, USER_LOGIN_TYPE);
            try {
                currentUser.login(token);
                User user = userService.getByName(name);
                session.setAttribute("user", user);
                return Result.success("登陆成功");
            } catch (IncorrectCredentialsException e) {
                return Result.fail("账号密码错误");
            }catch (LockedAccountException e){
                return Result.fail("账号已被锁定");
            }
        }else {
            return Result.success("已登陆");
        }
    }
```
