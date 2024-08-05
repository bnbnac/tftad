package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.RefreshRequest;
import com.tftad.request.Login;
import com.tftad.request.Signup;

public interface AuthService {
    Long login(Login login);

    Long signup(Signup signup);

    void logout(RefreshRequest refreshRequest);
}
