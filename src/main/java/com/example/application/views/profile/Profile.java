package com.example.application.views.profile;

import com.example.application.security.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@PageTitle("Profile")
@Route(value = "profile", layout = MainLayout.class)
public class Profile extends VerticalLayout {

    private String username;
    private Paragraph usernameParagraph;
    private String name;
    private TextField nameField;
    private byte[] image;
    private Avatar profileImage;
    private Upload upload;

    @Autowired
    public Profile(UserService userService) {
        username = (String) VaadinSession.getCurrent().getAttribute("username");
        JSONObject user = userService.findUserByUsername(username);
        try {
            name = user.getString("name");
        } catch (JSONException e) {
            name = null;
        }
        if (user.has("profileImage")) {
            String base64 = user.optString("profileImage", null);
            image = base64 != null ? java.util.Base64.getDecoder().decode(base64) : null;
        } else {
            image = null;
        }

        usernameParagraph = new Paragraph();
        usernameParagraph.getElement().setProperty("innerHTML", "<b>Username:</b> " + username);
        usernameParagraph.getStyle().set("margin", "0px");

        nameField = new TextField("Name");

        if (name != null) {
            nameField.setValue(name);
        }

        profileImage = new Avatar();
        profileImage.setHeight("256px");
        profileImage.setWidth("256px");

        if (image != null) {
            StreamResource resource = new StreamResource("profile-image", () -> new ByteArrayInputStream(image));
            profileImage.setImageResource(resource);
        }

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            try {
                final byte[] uploadedImage = buffer.getInputStream().readAllBytes();
                StreamResource resource = new StreamResource("profile-image", () -> new ByteArrayInputStream(uploadedImage));
                profileImage.setImageResource(resource);
                image = uploadedImage;
            } catch (IOException e) {
                Notification.show("Failed to upload file: " + e.getMessage());
            }
        });
        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> {
            userService.updateUser(username, nameField.getValue(), image);

            Notification.show("Profile updated successfully");
        });

        add(usernameParagraph, nameField, profileImage, upload, saveButton);
    }
}
