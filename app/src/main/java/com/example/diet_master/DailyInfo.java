package com.example.diet_master;

import java.util.HashMap;
import java.util.Map;

public class DailyInfo {

    // FoodInfo
    //private String DailyFoodName;   // 음식이름
    private String DailyCalory;          // 일일칼로리
    private String DailyCarb;            // 탄수화물(Carbohydrate)
    private String DailyProtein;         // 단백질
    private String DailyFat;             // 지방

    public String getDailyCalory() {
        return DailyCalory;
    }

    public void setDailyCalory(String DailyCalory) {
        this.DailyCalory = DailyCalory;
    }

    public String getDailyCarb() {
        return DailyCarb;
    }

    public void setDailyCarb(String DailyCarb) {
        this.DailyCarb = DailyCarb;
    }

    public String getDailyProtein() {
        return DailyProtein;
    }

    public void setDailyProtein(String DailyProtein) {
        this.DailyProtein = DailyProtein;
    }

    public String getDailyFat() {
        return DailyFat;
    }

    public void setDailyFat(String DailyFat) {
        this.DailyFat = DailyFat;
    }


    // AddFoodInfo(detail)
    private String BreakfastCalory;
    private String LunchCalory;
    private String DinnerCalory;
    private String Calory;
    private String Carb;
    private String Protein;
    private String Fat;

    public String getBreakfastCalory() {
        return BreakfastCalory;
    }

    public void setBreakfastCalory(String BreakfastCalory) {
        BreakfastCalory = BreakfastCalory;
    }

    public String getLunchCalory() {
        return LunchCalory;
    }

    public void setLunchCalory(String LunchCalory) {
        LunchCalory = LunchCalory;
    }

    public String getDinnerCalory() {
        return DinnerCalory;
    }

    public void setDinnerCalory(String dinnerCalory) {
        DinnerCalory = dinnerCalory;
    }

    public String getCalory() {
        return Calory;
    }

    public void setCalory(String Calory) {
        Calory = Calory;
    }

    public String getCarb() {
        return Carb;
    }

    public void setCarb(String Carb) {
        Carb = Carb;
    }

    public String getProtein() {
        return Protein;
    }

    public void setProtein(String Protein) {
        Protein = Protein;
    }

    public String getFat() {
        return Fat;
    }

    public void setFat(String Fat) {
        Fat = Fat;
    }


    // UserInfo
    private String recoCal;
    private String recoCarb;
    private String recoProtein;
    private String recoFat;
    private String basicRate;

    public String getRecoCal() {
        return recoCal;
    }

    public void setRecoCal(String recoCal) {
        this.recoCal = recoCal;
    }

    public String getRecoCarb() {
        return recoCarb;
    }

    public void setRecoCarb(String recoCarb) {
        this.recoCarb = recoCarb;
    }

    public String getRecoProtein() {
        return recoProtein;
    }

    public void setRecoProtein(String recoProtein) {
        this.recoProtein = recoProtein;
    }

    public String getRecoFat() {
        return recoFat;
    }

    public void setRecoFat(String recoFat) {
        this.recoFat = recoFat;
    }

    public String getBasicRate() {
        return basicRate;
    }

    public void setBasicRate(String basicRate) {
        this.basicRate = basicRate;
    }

    public DailyInfo() {}


    public DailyInfo(String DailyCalory, String DailyCarb, String DailyProtein, String DailyFat, String BreakfastCalory, String LunchCalory,
                     String DinnerCalory, String Calory, String Carb, String Protein, String Fat,
                     String recoCal, String recoCarb, String recoProtein, String recoFat, String basicRate) {
        this.DailyCalory = DailyCalory;
        this.DailyCarb = DailyCarb;
        this.DailyProtein = DailyProtein;
        this.DailyFat = DailyFat;

        this.BreakfastCalory = BreakfastCalory;
        this.LunchCalory = LunchCalory;
        this.DinnerCalory = DinnerCalory;
        this.Calory = Calory;
        this.Carb = Carb;
        this.Protein = Protein;
        this.Fat = Fat;

        this.recoCal = recoCal;
        this.recoCarb = recoCarb;
        this.recoProtein = recoProtein;
        this.recoFat = recoFat;
        this.basicRate = basicRate;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.get(BreakfastCalory);
        result.get(LunchCalory);
        result.get(DinnerCalory);
        result.get(Calory);
        result.get(Carb);
        result.get(Protein);
        result.get(Fat);

        return result;
    }

}
