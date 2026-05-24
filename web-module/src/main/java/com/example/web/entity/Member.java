package com.example.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@TableName("ums_member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long levelId;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String email;
    private String header;
    private Integer gender;
    private LocalDate birth;
    private String city;
    private String job;
    private String sign;
    private Integer sourceType;
    private Integer integration;
    private Integer growth;
    private Integer status;
    private OffsetDateTime createTime;
    private String socialUid;
    private String accessToken;
    private String expiresIn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public LocalDate getBirth() { return birth; }
    public void setBirth(LocalDate birth) { this.birth = birth; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }
    public String getSign() { return sign; }
    public void setSign(String sign) { this.sign = sign; }
    public Integer getSourceType() { return sourceType; }
    public void setSourceType(Integer sourceType) { this.sourceType = sourceType; }
    public Integer getIntegration() { return integration; }
    public void setIntegration(Integer integration) { this.integration = integration; }
    public Integer getGrowth() { return growth; }
    public void setGrowth(Integer growth) { this.growth = growth; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public OffsetDateTime getCreateTime() { return createTime; }
    public void setCreateTime(OffsetDateTime createTime) { this.createTime = createTime; }
    public String getSocialUid() { return socialUid; }
    public void setSocialUid(String socialUid) { this.socialUid = socialUid; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getExpiresIn() { return expiresIn; }
    public void setExpiresIn(String expiresIn) { this.expiresIn = expiresIn; }
}
