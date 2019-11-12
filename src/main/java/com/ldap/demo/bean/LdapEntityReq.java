package com.ldap.demo.bean;


import com.ldap.demo.enums.AdObjectClassEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yh128
 * @version 1.0.0
 * @ClassName LdapEntityReq.java
 * @Description AD域新增实体
 * @Param
 * @createTime 2019年10月28日 10:52:08
 */
@Setter
@Getter
@ToString
public class LdapEntityReq {
    private String objectClass = AdObjectClassEnum.USER.getValue();
    private String samAccountName;
    private String displayName;
    private String userPrincipalName;
    private String url;
    private String employeeID;
    private String name;
    private String physicalDeliveryOfficeName;
    private String departmentNumber;
    private String telephoneNumber;
    private String homePhone;
    private String mobile;
    private String department;
    private String mail;
    private String userAccountControl;
    private String description;

    public LdapEntityReq() {
    }

    public LdapEntityReq(AdObjectClassEnum adObjectClassEnum){
        this.objectClass = adObjectClassEnum.getValue();
    }

    public LdapEntityReq(AdObjectClassEnum adObjectClassEnum, String name, String departmentNumber) {
        this.objectClass = adObjectClassEnum.getValue();
        this.name = name;
        this.description = departmentNumber;
    }

    public LdapEntityReq(String mail, String mobile, String nameCn, String employeeID) {
        String names = mail;
        int index = 0;
        if ((index = mail.indexOf("@")) >= 0) {
            names = mail.substring(0, index);
        }
        this.samAccountName = names;
        this.displayName = nameCn;
        this.userPrincipalName = names + "@test.com";
        this.employeeID = employeeID;
        this.name = nameCn;
        this.physicalDeliveryOfficeName = nameCn;
        this.telephoneNumber = mobile;
        this.homePhone = mobile;
        this.mobile = mobile;
        this.mail = mail;
        // 544为启用
        this.userAccountControl = "544";
    }

}
