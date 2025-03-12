package com.example.mobile.dto.request;

import com.example.mobile.model.Address;
import com.example.mobile.model.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private List<Hobbies> hobbies;
    private Gender gender;
    private Integer age;
    private Integer height;
    private Address address;
    private String bio;
    private ZodiacSign zodiacSign;
    private PersonalityType personalityType;
    private CommunicationStyle communicationStyle;
    private LoveLanguage loveLanguage;
    private PetPreference petPreference;
    private DrinkingHabit drinkingHabit;
    private SmokingHabit smokingHabit;
    private SleepingHabit sleepingHabit;
}
