package com.ldap.demo.enums;

/**
 * @author yh128
 * @version 1.0.0
 * @ClassName AdObjectClassEnum.java
 * @Description AD域ObjectClass类型
 * @Param
 * @createTime 2019年10月28日 14:40:00
 */
public enum AdObjectClassEnum {
    ORGANIZATIONAL_UNIT("organizationalUnit"),
    ORGANIZATIONAL_PERSON("organizationalPerson"),
    PERSON("person"),
    USER("user");

    private String value;

    AdObjectClassEnum(String value){
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
