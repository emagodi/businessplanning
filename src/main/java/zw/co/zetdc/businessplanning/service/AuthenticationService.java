package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.User;
import zw.co.zetdc.businessplanning.payload.request.AuthenticationRequest;
import zw.co.zetdc.businessplanning.payload.request.RegisterRequest;
import zw.co.zetdc.businessplanning.payload.request.UserUpdateRequest;
import zw.co.zetdc.businessplanning.payload.response.AuthenticationResponse;


public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request, boolean createdByAdmin, String token);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    public User getUserById(Long id);


    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    public void changePassword(String email, String currentPassword, String newPassword);

}
