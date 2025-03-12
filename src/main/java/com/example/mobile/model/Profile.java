package com.example.mobile.model;

import com.example.mobile.model.enums.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;

    private String userID;
    private List<Photo> photos;
    private List<Hobbies> hobbies;
    private Gender gender;
    private int age;
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
