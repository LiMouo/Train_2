package com.ziker.train.Utils.Info;

public class AccountInfo<E>{
    private String Car_id;
    private String Car_user;
    private Integer Money;

    public Integer getWarningMoney() {
        return WarningMoney;
    }

    public void setWarningMoney(Integer warningMoney) {
        WarningMoney = warningMoney;
    }

    private Integer WarningMoney;
    private E Icon;

    public String getCar_id() { return Car_id; }

    public void setCar_id(String car_id) { Car_id = car_id; }

    public String getCar_user() { return Car_user; }

    public void setCar_user(String car_user) { Car_user = car_user; }

    public Integer getMoney() { return Money; }

    public void setMoney(Integer money) { Money = money; }

    public E getIcon() { return Icon; }

    public void setIcon(E icon) { Icon = icon; }

}
