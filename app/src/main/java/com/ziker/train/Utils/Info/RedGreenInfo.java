package com.ziker.train.Utils.Info;

public class RedGreenInfo {
    private int id;
    private int YellowTime;
    private int GreenTime;
    private int RedTime;
    private String RESULT;
    private String ErrorMSG;

    public int getYellowTime() {
        return YellowTime;
    }

    public void setYellowTime(int yellowTime) {
        YellowTime = yellowTime;
    }

    public int getGreenTime() {
        return GreenTime;
    }

    public void setGreenTime(int greenTime) {
        GreenTime = greenTime;
    }

    public int getRedTime() {
        return RedTime;
    }

    public void setRedTime(int redTime) {
        RedTime = redTime;
    }

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public String getErrorMSG() {
        return ErrorMSG;
    }

    public void setErrorMSG(String errorMSG) {
        this.ErrorMSG = errorMSG;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

//    List<Integer> list=new ArrayList <>();
//    list.add(1);
//    list.add(2);
//    list.add(3);
//    System.out.println(list.get(2));
//    Collections.sort(list,new Comparator<Integer>(){
//        public int compare(Integer i1, Integer i2)
//        {
//            return i1.compareTo( i2);
//        }
//    }); //lambda表达式写
//     Collections.sort(list,(Integer i1, Integer i2)->{return i1.compareTo( i2);}); for(Integer u : list){ System.out.println(u);
