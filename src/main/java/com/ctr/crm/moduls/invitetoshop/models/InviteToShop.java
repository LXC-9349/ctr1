package com.ctr.crm.moduls.invitetoshop.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 到店信息
 *
 * @author DoubleLi
 * @date 2019-06-27
 */
public class InviteToShop implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private Integer id;
    @ApiParam(required = true, value = "客户ID")
    private Long memberId;
    @ApiParam(required = true, value = "预约日期")
    private Date inviteDate;
    @ApiParam(required = true, value = "预约时间段 1(8:00-10:00) 2(10:00-12:00) 3(13:00-15:00) 4(15:00-17:00) 5(17:00-19:00) 6(19:00-21:00)")
    private Integer inviteTime;
    @ApiParam(hidden = true)
    private Integer inviteWorkerId;
    @ApiParam(hidden = true)
    private Integer shopStatus;
    @ApiParam(hidden = true)
    private Date shopTime;
    @ApiParam(required = true, value = "到店类型 1预约到店 2主动到店")
    private Integer inviteType;
    @ApiParam(hidden = true)
    private Date createTime;


    public InviteToShop() {
    }

    public InviteToShop(Long memberId, Integer shopStatus) {
        this.memberId = memberId;
        this.shopStatus = shopStatus;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setInviteDate(Date inviteDate) {
        this.inviteDate = inviteDate;
    }

    public Date getInviteDate() {
        return inviteDate;
    }

    public void setInviteTime(Integer inviteTime) {
        this.inviteTime = inviteTime;
    }

    public Integer getInviteTime() {
        return inviteTime;
    }

    public void setInviteWorkerId(Integer inviteWorkerId) {
        this.inviteWorkerId = inviteWorkerId;
    }

    public Integer getInviteWorkerId() {
        return inviteWorkerId;
    }

    public void setShopStatus(Integer shopStatus) {
        this.shopStatus = shopStatus;
    }

    public Integer getShopStatus() {
        return shopStatus;
    }

    public void setShopTime(Date shopTime) {
        this.shopTime = shopTime;
    }

    public Date getShopTime() {
        return shopTime;
    }

    public void setInviteType(Integer inviteType) {
        this.inviteType = inviteType;
    }

    public Integer getInviteType() {
        return inviteType;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

}