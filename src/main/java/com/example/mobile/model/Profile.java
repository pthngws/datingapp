package com.example.mobile.model;

import com.example.mobile.model.enums.*;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "profiles")
public class Profile {
    @Id
    private ObjectId id;

    private ObjectId addressId;
    private ObjectId albumId;

    private String firstName;
    private String lastName;
    private List<Hobbies> hobbies;
    private Gender gender;
    private int age;
    private int height;
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
