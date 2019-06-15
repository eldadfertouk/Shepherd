package com.example.shepherd.DataBaseObjects;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import lombok.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class UserBasicObject {

    private String OID;
    private String firstName;
    private String lastName;

    private String Gender;
    private String phoneNumber;
    private String email;

    private double longitude;
    private double latitude;

    private DateFormat birthDate;
    private Date startMemberDate;
    private Date lastLogin;

    private Boolean isUnder18;

    private GeofencingClient lastLocation;


    }


