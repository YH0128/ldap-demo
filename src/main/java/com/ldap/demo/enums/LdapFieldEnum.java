package com.ldap.demo.enums;

/**
 * @author yh128
 * @version 1.0.0
 * @ClassName LdapField.java
 * @Description Ldap字段
 * @Param
 * @createTime 2019年10月28日 14:35:00
 */
public enum LdapFieldEnum {
    DC("DC="),
    CN("CN="),
    OU("OU=");
    private String value;

    LdapFieldEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static String getOuPattern() {
        return OU.getValue() + "%s";
    }

    public static String getCnPattern() {
        return CN.getValue() + "%s";
    }

    public static String getCnPatterns() {
        return CN.getValue() + "%s-%s-%s,%s";
    }

}
