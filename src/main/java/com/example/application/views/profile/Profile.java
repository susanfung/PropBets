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
    private String name;
    private TextField nameField;
    private Binary image;
    private Avatar profileImage;
    private Upload upload;

    @Autowired
    public Profile(UserService userService) {
        this.userService = userService;
        username = (String) VaadinSession.getCurrent().getAttribute("username");
        Document user = userService.findUserByUsername(username);
        name = user.getString("name");
        image = user.get("profileImage", Binary.class);

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

        add(usernameParagraph, nameField, profileImage, upload, saveButton);
    }

    private void saveProfile() {
        userService.updateUser(username, nameField.getValue(), image.getData());

        Notification.show("Profile updated successfully");
    }
}
