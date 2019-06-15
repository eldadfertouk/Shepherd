package com.example.shepherd.Data;

import com.example.shepherd.Data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe" );
            return new com.example.shepherd.Data.Result.Success<>( fakeUser );
        } catch (Exception e) {
            return new com.example.shepherd.Data.Result.Error( new IOException( "Error logging in", e ) );
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
