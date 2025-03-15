package com.example.mobile.dto.response;

import com.example.mobile.model.enums.PersonalityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {

    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private int height;
    private String bio;
    private String zodiacSign;
    private PersonalityType personalityType;
    private String communicationStyle;
    private String loveLanguage;
    private String petPreference;
    private String drinkingHabit;
    private String smokingHabit;
    private String sleepingHabit;
    private List<String> hobbies;

    private String street;
    private String district;
    private String province;

    private String pic1;
    private String pic2;
    private String pic3;
    private String pic4;
    private String pic5;
    private String pic6;
    private String pic7;
    private String pic8;
    private String pic9;

}
