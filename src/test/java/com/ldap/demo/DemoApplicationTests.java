package com.ldap.demo;

import com.ldap.demo.bean.LdapEntityReq;
import com.ldap.demo.enums.AdObjectClassEnum;
import com.ldap.demo.enums.LdapFieldEnum;
import com.ldap.demo.utils.LdapUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

@SpringBootTest
@Slf4j
public class DemoApplicationTests {

    @Test
    /**
     * 新增部门
     */
    void addDept() {
        String dn = LdapFieldEnum.getOuPattern();
        LdapEntityReq ldapEntityReq = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT, "研发中心", "dept001");
        dn = String.format(dn, "研发中心");
        LdapUtil.add(ldapEntityReq, dn);
    }

    @Test
    /**
     * 新增个人
     */
    void addPerson() {
        String dn = LdapFieldEnum.getCnPattern();
        LdapEntityReq ldapEntityReq = new LdapEntityReq("yh128@163.com", "155555555", "天雨流芳", "001");
        dn = String.format(dn, "天雨流芳");
        LdapUtil.add(ldapEntityReq, String.format(dn + "," + LdapFieldEnum.getOuPattern(), "研发中心"));
    }

    @Test
    /**
     * 重置密码
     */
    void resetPwd() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("001");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        String dn = searchResult.getNameInNamespace();
        LdapUtil.updateAdPwdByReplace(dn, "Test@123");
    }

    @Test
    /**
     * 密码修改
     */
    void updatePwd() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("001");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        String dn = searchResult.getNameInNamespace();
        LdapUtil.updatePwdByRemoveToAdd(dn, "Test@123", "Test@123456");
    }

    @Test
    /**
     * 单个部门和个人查找
     */
    void searchOne() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("001");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        System.out.println("查询个人数据:" + searchResult);

        LdapEntityReq ldapEntityReq1 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq1.setDescription("dept001");
        SearchResult searchResult1 = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq1, true);
        System.out.println("查询部门数据：" + searchResult1);

    }

    @Test
    /**
     * 查找部门下面所用的员工
     */
    void searchMany() throws Exception {
        LdapEntityReq ldapEntityReq1 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq1.setDescription("dept001");
        SearchResult searchResult1 = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq1, true);
        NamingEnumeration<SearchResult> searchResultNamingEnumeration = LdapUtil.searchByBaseDn(searchResult1.getNameInNamespace());
        while (searchResultNamingEnumeration.hasMore()) {
            SearchResult next = searchResultNamingEnumeration.next();
            System.out.println(next.getNameInNamespace());
        }
    }

    @Test
    /**
     * 重命名
     */
    void reName() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("001");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        String oldDn = searchResult.getNameInNamespace();
        System.out.println(searchResult.getAttributes());
        String newDn = oldDn.replace(searchResult.getName(), String.format(LdapFieldEnum.getCnPattern(), "智能小辉"));
        LdapUtil.reName(oldDn, newDn);

        LdapEntityReq ldapEntityReq1 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq1.setDescription("dept002");
        SearchResult searchResult1 = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq1, true);
        String oldDeptDn = searchResult1.getNameInNamespace();
        String newDeptDn = oldDeptDn.replace(searchResult1.getName(), String.format(LdapFieldEnum.getOuPattern(),
                "测试中心"));
        LdapUtil.reName(oldDeptDn, newDeptDn);
    }

    /**
     * 修改字段，（name和密码等一些字段是不能直接这么修改的）
     */
    @Test
    void update() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("001");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        String dn = searchResult.getNameInNamespace();
        LdapEntityReq ldapEntityReq1 = new LdapEntityReq();
        ldapEntityReq1.setEmployeeID("002");
        LdapUtil.update(dn, ldapEntityReq1);

        LdapEntityReq ldapEntityReq2 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq2.setDescription("dept002");
        SearchResult searchResult1 = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq2, true);
        String deptDn = searchResult1.getNameInNamespace();
        LdapEntityReq ldapEntityReq3 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq3.setDescription("dept001");
        LdapUtil.update(deptDn, ldapEntityReq3);

    }

    @Test
    /**
     * 删除
     */
    void delete() {
        LdapEntityReq ldapEntityReq = new LdapEntityReq();
        ldapEntityReq.setEmployeeID("002");
        SearchResult searchResult = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq, true);
        String dn = searchResult.getNameInNamespace();
        LdapUtil.delete(dn);

        LdapEntityReq ldapEntityReq2 = new LdapEntityReq(AdObjectClassEnum.ORGANIZATIONAL_UNIT);
        ldapEntityReq2.setDescription("dept001");
        SearchResult searchResult1 = LdapUtil.searchByBaseDnAndUserName(ldapEntityReq2, true);
        String deptDn = searchResult1.getNameInNamespace();
        LdapUtil.delete(deptDn);
    }


}
