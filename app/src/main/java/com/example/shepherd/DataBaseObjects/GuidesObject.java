package com.example.shepherd.DataBaseObjects;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class GuidesObject extends UserBasicObject {
    private UserBasicObject basicUser;
    private String seniority;
    private String spokenTang;
}
