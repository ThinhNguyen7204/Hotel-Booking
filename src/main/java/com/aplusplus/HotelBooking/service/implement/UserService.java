package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.LoginRequest;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.UserDTO;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.UserMapper;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.FirebaseStorageService;
import com.aplusplus.HotelBooking.service.interf.IUserService;
import com.aplusplus.HotelBooking.utils.JwtUtils;
import com.aplusplus.HotelBooking.utils.Utils;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try{
            if(user.getRole() == null || user.getRole().isBlank()){
                user.setRole("USER");
            }

            if(userRepository.existsByEmail(user.getUsername())){
                throw new OurException("This username already existed, please choose another username");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = userMapper.userToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Register successfully");
        } catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException("Username not found"));
            String jwt = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setExpirationTime("1 day");
            response.setMessage("Login successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurs During User Login" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllCustomers(Pageable pageable) {
        Response response = new Response();
        try{
//            List<User> userList = userRepository.getAllCustomers();
//            List<UserDTO> userDTOList = userMapper.userListToUserDTOList(userList);
            Page<UserDTO> userDTOPage = userRepository.findAllCustomers(pageable).map(userMapper::userToUserDTO);
            response.setUserList(userDTOPage.getContent());
            response.setCurrentPage(userDTOPage.getNumber());
            response.setTotalPages(userDTOPage.getTotalPages());
            response.setTotalElements(userDTOPage.getTotalElements());
            response.setStatusCode(200);
            response.setMessage("Get all customers successfully");
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        return null;
    }

    @Override
    public Response deleteUser(String username) {
        Response response = new Response();
        try{
            User user = userRepository.findByEmail(username).orElseThrow(() -> new OurException("Username not found"));

            userRepository.deleteByEmail(username);
            response.setStatusCode(200);
            response.setMessage("Delete successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = userMapper.userToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting user by id" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String username) {
        Response response = new Response();
        try{
            User user = userRepository.findByEmail(username).orElseThrow(() -> new OurException("User not found"));
            UserDTO userDTO = userMapper.userToUserDTO(user);

            response.setStatusCode(200);
            response.setMessage("Get information successfully");
            response.setUser(userDTO);
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateInfo(String username, UserDTO userDTO) {
        Response response = new Response();
        try{
            User user = userRepository.findByEmail(username).orElseThrow(() -> new OurException("User not found"));
            if(userDTO.getName() != null) user.setName(userDTO.getName());
            if(userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
            if(userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
            userRepository.save(user);
            response.setStatusCode(200);
            response.setMessage("Update information successfully");
        } catch (OurException e){
             response.setStatusCode(404);
             response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response uploadImage(String username, MultipartFile image) {
        Response response = new Response();
        try{
            User user = userRepository.findByEmail(username).orElseThrow(() -> new OurException("User not found"));
            if(image != null){
                String fileUrl = firebaseStorageService.uploadFile(image);
                user.setImageUrl(fileUrl);
            }
            userRepository.save(user);
            response.setStatusCode(200);
            response.setMessage("Upload avatar successfully");
        } catch(OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


}

