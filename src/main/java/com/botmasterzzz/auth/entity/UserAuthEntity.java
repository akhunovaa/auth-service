package com.botmasterzzz.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sys_auth_log")
public class UserAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private User user;

    @JsonIgnore
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "referer")
    private String referer;

    @Column(name = "full_url")
    private String fullUrl;

    @Column(name = "client_os")
    private String clientOs;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "client_browser")
    private String clientBrowser;

    @Column(name = "note")
    private String note;

    @Column(name = "token")
    private String token;

    @Column(name = "aud_when_create")
    private Timestamp audWhenCreate;

    @Column(name = "aud_when_update")
    private Timestamp audWhenUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getClientOs() {
        return clientOs;
    }

    public void setClientOs(String clientOs) {
        this.clientOs = clientOs;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientBrowser() {
        return clientBrowser;
    }

    public void setClientBrowser(String clientBrowser) {
        this.clientBrowser = clientBrowser;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getAudWhenCreate() {
        return audWhenCreate;
    }

    public void setAudWhenCreate(Timestamp audWhenCreate) {
        this.audWhenCreate = audWhenCreate;
    }

    public Timestamp getAudWhenUpdate() {
        return audWhenUpdate;
    }

    public void setAudWhenUpdate(Timestamp audWhenUpdate) {
        this.audWhenUpdate = audWhenUpdate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "id=" + id +
                ", user=" + user +
                ", ipAddress='" + ipAddress + '\'' +
                ", referer='" + referer + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                ", clientOs='" + clientOs + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", clientBrowser='" + clientBrowser + '\'' +
                ", note='" + note + '\'' +
                ", token='" + token + '\'' +
                ", audWhenCreate=" + audWhenCreate +
                ", audWhenUpdate=" + audWhenUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthEntity that = (UserAuthEntity) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(user, that.user) &&
                Objects.equal(ipAddress, that.ipAddress) &&
                Objects.equal(referer, that.referer) &&
                Objects.equal(fullUrl, that.fullUrl) &&
                Objects.equal(clientOs, that.clientOs) &&
                Objects.equal(userAgent, that.userAgent) &&
                Objects.equal(clientBrowser, that.clientBrowser) &&
                Objects.equal(note, that.note) &&
                Objects.equal(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, user, ipAddress, referer, fullUrl, clientOs, userAgent, clientBrowser, note, token);
    }
}
