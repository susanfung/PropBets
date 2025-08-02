package com.example.application.security;

import com.example.application.supabase.SupabaseService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.approvaltests.Approvals.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    @Mock
    private SupabaseService mockSupabaseService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(mockSupabaseService);
    }

    @Test
    void saveUser() throws Exception {
        String username = "john_doe";
        when(mockSupabaseService.post(eq(UserService.USER_PROFILE_TABLE), any())).thenReturn("{\"username\":\"john_doe\"}");

        userService.saveUser(username);

        ArgumentCaptor<String> tableCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService).post(tableCaptor.capture(), jsonCaptor.capture());
        verify(jsonCaptor.getValue());
    }

    @Test
    void updateUser() throws Exception {
        String username = "john_doe";
        String name = "John Doe";
        byte[] profileImage = new byte[]{1, 2, 3};

        when(mockSupabaseService.patch(eq(UserService.USER_PROFILE_TABLE), any(), any())).thenReturn(
                "{\"username\":\"john_doe\",\"firstName\":\"John\",\"lastName\":\"Doe\"}");

        userService.updateUser(username, name, profileImage);

        ArgumentCaptor<String> tableCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService).patch(tableCaptor.capture(), queryCaptor.capture(), jsonCaptor.capture());

        verify(jsonCaptor.getValue());
    }

    @Test
    void findUserByUsername() throws Exception {
        String username = "JohnDoe";
        String expectedResponse = "[{\"username\":\"JohnDoe\"}]";
        when(mockSupabaseService.get(eq(UserService.USER_PROFILE_TABLE), any())).thenReturn(expectedResponse);

        JSONObject user = userService.findUserByUsername(username.toLowerCase());
        verify(user.toString());
    }
}
