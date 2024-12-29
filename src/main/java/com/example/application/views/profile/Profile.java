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
import org.bson.Document;
import org.bson.types.Binary;
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
    private Binary image;
    private Avatar profileImage;
    private Upload upload;

    @Autowired
    public Profile(UserService userService) {
        this.userService = userService;
        username = (String) VaadinSession.getCurrent().getAttribute("username");
        Document user = userService.findUserByUsername(username);
        firstName = user.getString("firstName");
        lastName = user.getString("lastName");
        image = user.get("profileImage", Binary.class);

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
            StreamResource resource = new StreamResource("profile-image", () -> new ByteArrayInputStream(image.getData()));
            profileImage.setImageResource(resource);
        }

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            try {
                final byte[] uploadedImage = buffer.getInputStream().readAllBytes();
                StreamResource resource = new StreamResource("profile-image", () -> new ByteArrayInputStream(uploadedImage));
                profileImage.setImageResource(resource);
                image = new Binary(uploadedImage);
            } catch (IOException e) {
                Notification.show("Failed to upload file: " + e.getMessage());
            }
        });
        Button saveButton = new Button("Save", event -> saveProfile());

        add(usernameParagraph, nameFields, profileImage, upload, saveButton);
    }

    private void saveProfile() {
        userService.updateUser(username, firstNameField.getValue(), lastNameField.getValue(), image.getData());

        Notification.show("Profile updated successfully");
    }
}
