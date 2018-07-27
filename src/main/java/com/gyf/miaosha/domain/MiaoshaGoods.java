package com.gyf.miaosha.domain;


import java.util.Date;

public class MiaoshaGoods {

  private long id;
  private int classId;
  private long goodsId;
  private double miaoshaPrice;
  private long stockCount;
  private Date startDate;
  private Date endDate;

  public int getClassId()
  {
    return classId;
  }

  public void setClassId(int classId)
  {
    this.classId = classId;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(long goodsId) {
    this.goodsId = goodsId;
  }


  public double getMiaoshaPrice() {
    return miaoshaPrice;
  }

  public void setMiaoshaPrice(double miaoshaPrice) {
    this.miaoshaPrice = miaoshaPrice;
  }


  public long getStockCount() {
    return stockCount;
  }

  public void setStockCount(long stockCount) {
    this.stockCount = stockCount;
  }


  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(java.sql.Timestamp startDate) {
    this.startDate = startDate;
  }


  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(java.sql.Timestamp endDate) {
    this.endDate = endDate;
  }

}
