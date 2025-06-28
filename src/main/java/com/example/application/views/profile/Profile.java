package com.example.application.views.profile;

import com.example.application.security.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@PageTitle("Profile")
@Route(value = "profile", layout = MainLayout.class)
public class Profile extends VerticalLayout {
    private final UserService userService;

    private String username;
    private Paragraph usernameParagraph;
    private String firstName;
    private String lastName;
    private TextField firstNameField;
    private TextField lastNameField;
    private byte[] image;
    private Avatar profileImage;
    private Upload upload;

    @Autowired
    public Profile(UserService userService) {
        this.userService = userService;
        username = (String) VaadinSession.getCurrent().getAttribute("username");
        org.json.JSONObject user = userService.findUserByUsername(username);
        try {
            firstName = user.getString("firstName");
        } catch (org.json.JSONException e) {
            firstName = null;
        }
        try {
            lastName = user.getString("lastName");
        } catch (org.json.JSONException e) {
            lastName = null;
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

        HorizontalLayout nameFields = new HorizontalLayout();

        firstNameField = new TextField("First Name");
        lastNameField = new TextField("Last Name");

        if (firstName != null) {
            firstNameField.setValue(firstName);
        }

        if (lastName != null) {
            lastNameField.setValue(lastName);
        }

        nameFields.add(firstNameField, lastNameField);

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
            userService.updateUser(username, firstNameField.getValue(), lastNameField.getValue(), image);

            Notification.show("Profile updated successfully");
        });

        add(usernameParagraph, nameFields, profileImage, upload, saveButton);
    }
}
