package no.kristiania.pgr301.eksamen;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.kristiania.pgr301.eksamen.dto.VehicleDto;
import no.kristiania.pgr301.eksamen.repository.VehicleRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExamApplicationTests {
    @LocalServerPort private int port = 0;
    @Autowired private VehicleRepository repo;

    @Before
    public void clean() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        repo.deleteAll();

        given().get("/vehicles")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(0));
    }

    @Test
    public void testCreateAndGet() {
        VehicleDto dto = new VehicleDto(null, "TestVehicle", "TestModel");
        String resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location");

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("name", equalTo(dto.getName()))
                .body("model", equalTo(dto.getModel()));
    }

    @Test
    public void testCreateFailsOnInvalidData() {
        VehicleDto dto = new VehicleDto(null, "", "not invalid");
        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(400);

        dto.setModel(null);
        dto.setName("not invalid");

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(400);
    }

    @Test
    public void testReplaceVehicle() {
        VehicleDto dto = new VehicleDto(null, "TestVehicle", "TestModel");
        String resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location");

        VehicleDto dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("name", equalTo(dto.getName()))
                .body("model", equalTo(dto.getModel()))
                .extract().as(VehicleDto.class);

        String newName = "TestVehicleUpdatedName";
        dtoWithId.setName(newName);

        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(204);

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("name", equalTo(newName))
                .body("model", equalTo(dto.getModel()));
    }

    @Test
    public void testReplaceVehicleFailsOnInvalidId() {
        VehicleDto dto = new VehicleDto(null, "Test Vehicle", "Test Model");
        String resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location");

        VehicleDto dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("name", equalTo(dto.getName()))
                .body("model", equalTo(dto.getModel()))
                .extract().as(VehicleDto.class);

        dtoWithId.setId(String.valueOf(Long.parseLong(dtoWithId.getId()) + 1));
        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(409);
    }

    @Test
    public void testReplaceVehicleFailsOnInvalidData() {
        VehicleDto dto = new VehicleDto(null, "Test Vehicle", "Test Model");
        String resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location");

        VehicleDto dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("name", equalTo(dto.getName()))
                .body("model", equalTo(dto.getModel()))
                .extract().as(VehicleDto.class);

        dtoWithId.setModel("");
        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(400);

        dtoWithId.setModel("ok");
        dtoWithId.setName("");
        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(400);

        dtoWithId.setName("ok");
        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteVehicle() {
        VehicleDto dto = new VehicleDto(null, "VehicleName", "VehicleModel");
        String resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location");

        given().get(resourcePath)
                .then()
                .statusCode(200);

        given().delete(resourcePath)
                .then()
                .statusCode(204);

        given().get(resourcePath)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllVehicles() {
        List<VehicleDto> dtos = new ArrayList<>();

        for (int i = 0; i < 22; i++) {
            dtos.add(new VehicleDto(null, String.valueOf(i), String.valueOf(i)));
            given().contentType(ContentType.JSON)
                    .body(dtos.get(i))
                    .post("/vehicles")
                    .then()
                    .statusCode(201);
        }

        given().get("/vehicles")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(dtos.size()));
    }
}
