package com.example.mobile.dto.request;

import com.example.mobile.model.enums.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private List<Hobbies> hobbies;
    private Gender gender;
    @Min(value = 18, message = "Tuổi phải lớn hơn hoặc bằng 18")
    private Integer age;

    @Min(value = 100, message = "Chiều cao phải lớn hơn 100 cm")
    private Integer height;

    @Size(max = 50, message = "Tiểu sử không được quá 50 ký tự")
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
