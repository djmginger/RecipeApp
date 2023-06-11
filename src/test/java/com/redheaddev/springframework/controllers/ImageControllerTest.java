package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.services.ImageService;
import com.redheaddev.springframework.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ImageController.class)
@Import(ThymeleafAutoConfiguration.class)
public class ImageControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ImageService imageService;

    @MockBean
    RecipeService recipeService;

    ImageController controller;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new ImageController(imageService, recipeService);
    }

    @Test
    public void getImageForm() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId("1");

        //when
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

        //then
        webTestClient.get()
                .uri("/recipe/1/image")
                .exchange()
                .expectStatus().isOk();

        verify(recipeService, times(1)).findCommandById(anyString());

    }

    @Test
    public void handleImagePost() throws Exception {
        //given
        byte[] fakeImage = "RedHead Dev".getBytes();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("imagefile", fakeImage, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "form-data; name=imagefile; filename=image.jpg");

        //when
        when(imageService.saveImageFile(anyString(), any())).thenReturn(Mono.empty());

        //then
        webTestClient.post()
                .uri("/recipe/1/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/recipe/1/show");

        verify(imageService, times(1)).saveImageFile(anyString(), any());
    }


    @Test
    public void renderImageFromDB() throws Exception {

//        //given
//        RecipeCommand command = new RecipeCommand();
//        command.setId("1");
//
//        String s = "fake image text";
//        Byte[] bytesBoxed = new Byte[s.getBytes().length];
//
//        int i = 0;
//
//        for (byte primByte : s.getBytes()){
//            bytesBoxed[i++] = primByte;
//        }
//
//        command.setImage(bytesBoxed);
//
//        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));
//
//        //when
//        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//
//        byte[] reponseBytes = response.getContentAsByteArray();
//
//        assertEquals(s.getBytes().length, reponseBytes.length);
    }
}