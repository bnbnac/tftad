package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.RefreshRequest;
import com.tftad.request.Login;
import com.tftad.request.Signup;

public interface AuthService {
    void check(AuthenticatedMember authenticatedMember);

    Long login(Login login);

    Long signup(Signup signup);

    void logout(RefreshRequest refreshRequest);
}
