package com.ldap.demo.utils;

import com.ldap.demo.bean.LdapEntityReq;
import lombok.extern.slf4j.Slf4j;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * @author yh128
 * @version 1.0.0
 * @ClassName LdapUtil.java
 * @Description Ldap工具类
 * @Param
 * @createTime 2019年10月28日 14:20:00
 */
@Slf4j
public class LdapUtil {

    private LdapUtil() {
    }

    private static final String LDAP_URL = "LDAP://172.162.60.190:389";
    private static final String LDAP_SSL_URL = "LDAP://172.162.60.190:636";
    private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    // LDAP访问安全级别："none","simple","strong"
    private static final String SECURITY_AUTHENTICATION = "simple";
    // 管理员账号
    private static final String SECURITY_PRINCIPAL = "CN=testit,CN=Users,DC=test,DC=com";
    // 管理员密码
    private static final String SECURITY_CREDENTIALS = "Yh128123";
    // 证书管理名称
    private static final String SOCKET_FACTORY_KEY = "java.naming.ldap.factory.socket";
    // 自定义证书管理器所在路径
    private static final String SOCKET_FACTORY_VALUE = "com.ldap.demo.common.MySSLSocketFactory";
    // 端点表示
    private static final String DISABLE_ENDPOINT_IDENTIFICATION = "com.sun.jndi.ldap.objectdisableEndpointIdentification";
    private static final String TRUST_STORE_KEY = "javax.net.ssl.trustStore";
    // JDK证书所在位置
    private static final String TRUST_STORE_VALUE = "C:\\Program Files\\Java\\jdk1.8.0_211\\jre\\lib\\security\\cacerts";
    private static final String TRUST_STORE_PASSWORD_KEY = "javax.net.ssl.trustStorePassword";
    private static final String TRUST_STORE_PASSWORD_VALUE = "changeit";
    private static final String SECURITY_PROTOCOL = "ssl";
    // LDAP的根节点的DC
    public static final String ROOT = "DC=test,DC=com";
    private static DirContext dirContext = null;

    //获取连接
    private static DirContext getDirContext() throws Exception {
        if (dirContext == null) {
            synchronized (LdapUtil.class) {
                if (dirContext == null) {
                    try {
//                        dirContext = sslInit();
                        dirContext = certinit();
                        log.info("AD域初始化成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("AD域初始化失败");
                    }
                }
            }
        }
        return dirContext;
    }

    /**
     * 初始化AD域服务连接(389端口）
     *
     * @param userName
     * @param password
     * @param isValid
     */
    public static Boolean init(String userName, String password, boolean isValid) {
        Hashtable env = getBaseParams(false);
        try {
            if (isValid) {
                // 校验用户密码是否有效
                env.put(Context.SECURITY_PRINCIPAL, userName);
                env.put(Context.SECURITY_CREDENTIALS, password);
                new InitialDirContext(env);
                log.info("userName:[{}]在AD服务器中密码校验成功", userName);
            } else {
                dirContext = new InitialLdapContext(env, null);
                log.info("AD域服务连接认证成功:{}", SECURITY_PRINCIPAL);
            }
            return true;
        } catch (AuthenticationException e) {
            log.error("userName:[{}]身份验证失败:{}", userName, e.getMessage());
        } catch (javax.naming.CommunicationException e) {
            log.error("userName:[{}]AD域连接失败:{}", userName, e.getMessage());
        } catch (Exception e) {
            log.error("userName:[{}]身份验证未知异常:{}", userName, e.getMessage());
        }
        return false;
    }

    /**
     * ssl登录，使用证书通过证书管理器去获取
     *
     * @return
     */
    public static DirContext sslInit() throws Exception {
        Hashtable env = getBaseParams(true);
        // 指定java.naming.ldap.factory.socket
        env.put(SOCKET_FACTORY_KEY, SOCKET_FACTORY_VALUE);
        InitialLdapContext initialLdapContext = new InitialLdapContext(env, null);
        return initialLdapContext;
    }

    /**
     * SSL方式登录（636端口）(使用指定证书路径)
     */
    public static DirContext certinit() throws Exception {
        Hashtable env = getBaseParams(true);
        env.put(DISABLE_ENDPOINT_IDENTIFICATION, true);
        System.setProperty(TRUST_STORE_KEY, TRUST_STORE_VALUE);
        System.setProperty(TRUST_STORE_PASSWORD_KEY, TRUST_STORE_PASSWORD_VALUE);
        return new InitialLdapContext(env, null);
    }

    /**
     * 获取连接基础通用数据
     *
     * @param isSsl
     * @return
     */
    private static Hashtable getBaseParams(Boolean isSsl) {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        env.put(Context.PROVIDER_URL, LDAP_URL);
        // 登录账号
        env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
        // 登录密码
        env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        if (isSsl) {
            env.put(Context.PROVIDER_URL, LDAP_SSL_URL);
            env.put(Context.AUTHORITATIVE, Boolean.TRUE.toString());
            // 认证协议
            env.put(Context.SECURITY_PROTOCOL, SECURITY_PROTOCOL);
        }
        return env;
    }

    /**
     * 关闭AD域服务连接
     */
    public static void closeDirContext() {
        if (dirContext != null) {
            synchronized (dirContext) {
                if (dirContext != null) {
                    try {
                        dirContext.close();
                        log.info("AD域连接已经关闭");
                        dirContext = null;
                    } catch (NamingException e) {
                        log.error("AD域连接关闭失败", e);
                    }
                }
            }
        }
    }

    /**
     * 新增AD域用户
     *
     * @param ldapEntityReq
     * @param dn
     */
    public static Boolean add(LdapEntityReq ldapEntityReq, String dn) {
        try {

            Attributes attrs = new BasicAttributes(true);
            Field[] declaredFields = ldapEntityReq.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object obj = declaredField.get(ldapEntityReq);
                if (obj != null) {
                    attrs.put(declaredField.getName(), obj);
                }
            }
            getDirContext().createSubcontext(dn + "," + ROOT, attrs);
            return true;
        } catch (Exception e) {
            log.error("[{}]新增AD域用户/部门失败:{}", ldapEntityReq, e);
            return false;
        }
    }


    /**
     * 删除AD域用户/部门,(如果部门下面有数据是不法删除的，只能从叶子节点往上级删除)
     *
     * @param dn
     */
    public static void delete(String dn) {
        try {
            getDirContext().destroySubcontext(dn);
            log.info("dn=[{}]删除AD域用户/部门成功", dn);
        } catch (Exception e) {
            log.error("dn=[{}]删除AD域用户/部门失败:{}", dn, e);
        }
    }

    /**
     * 重命名AD域用户/部门
     *
     * @param oldDn
     * @param newDn
     * @return
     */
    public static boolean reName(String oldDn, String newDn) {
        try {
            getDirContext().rename(oldDn, newDn);
            log.info("oldDn=[{}],newDn[{}]重命名AD域用户/部门成功", oldDn, newDn);
            return true;
        } catch (Exception e) {
            log.error("oldDn=[{}],newDn[{}]重命名AD域用户/部门失败：{}", oldDn, newDn, e);
            return false;
        }
    }

    /**
     * 修改AD域用户属性/部门
     *
     * @param dn
     * @param ldapEntityReq
     * @return
     */
    public static boolean update(String dn, LdapEntityReq ldapEntityReq) {
        try {
            List<ModificationItem> list = new ArrayList();
            Field[] declaredFields = ldapEntityReq.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object obj = declaredField.get(ldapEntityReq);
                if (obj != null) {
                    list.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                            new BasicAttribute(declaredField.getName(), obj)));
                }
            }
            ModificationItem[] mods = new ModificationItem[list.size()];
            for (int i = 0; i < list.size(); i++) {
                mods[i] = list.get(i);
            }
            getDirContext().modifyAttributes(dn, mods);
            log.info("dn=[{}]修改AD域用户/部门属性成功", dn);
            return true;
        } catch (Exception e) {
            log.error("dn=[{}]修改AD域用户/部门属性失败:{}", dn, e);
            return false;
        }
    }

    /**
     * 搜索指定节点下的所有AD域用户/部门
     *
     * @param baseDn
     */
    public static NamingEnumeration<SearchResult> searchByBaseDn(String baseDn) throws Exception {
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "objectClass=user";
            String[] returnedAtts = {"memberOf", "url", "whenChanged", "employeeID", "name", "userPrincipalName",
                    "physicalDeliveryOfficeName", "departmentNumber", "telephoneNumber",
                    "homePhone", "mobile", "department", "sAMAccountName", "whenChanged",
                    "mail"};
            searchCtls.setReturningAttributes(returnedAtts);
            return getDirContext().search(baseDn, searchFilter, searchCtls);
    }

    /**
     * 指定搜索节点搜索指定域用户/部门
     *
     * @param ldapEntityReq
     * @param isAnd
     * @return
     */
    public static SearchResult searchByBaseDnAndUserName(LdapEntityReq ldapEntityReq, Boolean isAnd) {
        try {
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            Field[] declaredFields = ldapEntityReq.getClass().getDeclaredFields();
            String orAndStr = isAnd ? "(&" : "(|";
            StringBuilder str = new StringBuilder(orAndStr);
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object obj = declaredField.get(ldapEntityReq);
                if (obj != null) {
                    str.append("(").append(declaredField.getName()).append("=").append(obj).append(")");
                }
            }
            String searchFilter = str.append(")").toString();
            String[] returnedAtts = {"memberOf"};
            searchCtls.setReturningAttributes(returnedAtts); //设置返回属性集
            NamingEnumeration<SearchResult> answer = getDirContext().search(ROOT, searchFilter, searchCtls);
            return answer.next();
        } catch (Exception e) {
            log.error("指定搜索节点搜索指定域用户失败:{}", e);
        }
        return null;
    }

    /**
     * 重置密码
     * 直接替换密码字段里面的值
     *
     * @param dn
     * @param password
     * @return
     */
    public static Boolean updateAdPwdByReplace(String dn, String password) {

        ModificationItem[] mods = new ModificationItem[2];
        try {
            password = "\"" + password + "\"";
            byte[] newpasswordBytes = password.getBytes("UTF-16LE");
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("unicodePwd", newpasswordBytes));
            // 首次登录必须修改密码
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("pwdLastSet", "0"));
            getDirContext().modifyAttributes(dn, mods);
            log.info("dn=[{}]重置密码成功", dn);
            return true;
        } catch (Exception e) {
            log.error("dn=[{}]重置密码失败:{}", dn, e);
            return false;
        }
    }

    /**
     * 修改AD域用户密码
     * 移除密码字段后新增密码字段来修改密码
     *
     * @param dn
     */
    public static Boolean updatePwdByRemoveToAdd(String dn, String oldPassword, String newPassword) {
        try {
            oldPassword = "\"" + oldPassword + "\"";
            byte[] oldPasswordBytes = oldPassword.getBytes("UTF-16LE");
            newPassword = "\"" + newPassword + "\"";
            byte[] newPasswordBytes = newPassword.getBytes("UTF-16LE");

            ModificationItem[] mods = new ModificationItem[2];
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd",
                    oldPasswordBytes));
            mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd",
                    newPasswordBytes));

            getDirContext().modifyAttributes(dn, mods);
            log.info("dn=[{}]修改密码成功！", dn);
            return true;
        } catch (Exception e) {
            log.info("dn=[{}]修改密码失败：{}", dn, e);
            return false;
        }
    }


}
