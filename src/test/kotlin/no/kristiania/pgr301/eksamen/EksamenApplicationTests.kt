package no.kristiania.pgr301.eksamen

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.kristiania.pgr301.eksamen.dto.VehicleDto
import no.kristiania.pgr301.eksamen.dto.WrappedResponse
import no.kristiania.pgr301.eksamen.hateos.HalPage
import no.kristiania.pgr301.eksamen.repository.VehicleRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EksamenApplicationTests {

    @LocalServerPort private var port = 0
    @Autowired private lateinit var repo: VehicleRepository

    @Before
    fun clean() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        repo.deleteAll()

        given().get("/vehicles").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {
        val dto = VehicleDto(name = "Test Vehicle", model = "Test Model")

        val resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(dto.name))
                .body("data.model", equalTo(dto.model))
    }

    @Test
    fun testCreateFailsOnInvalidData() {
        val dto = VehicleDto(name = "", model = "")

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(400)
    }

    @Test
    fun testReplaceVehicle() {
        val dto = VehicleDto(name = "Vehicle1", model = "Vehicle2")

        val resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(dto.name))
                .body("data.model", equalTo(dto.model))
                .extract().response().body()
                .jsonPath().getObject("data", VehicleDto::class.java)

        val newName = "Vehicle1Updated"
        dtoWithId.name = newName
        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(204)

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(newName))
                .body("data.model", equalTo(dto.model))
    }

    @Test
    fun testReplaceVehicleFailsOnInvalidId() {
        val dto = VehicleDto(name = "Vehicle1", model = "Vehicle2")

        val resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(dto.name))
                .body("data.model", equalTo(dto.model))
                .extract().response().body()
                .jsonPath().getObject("data", VehicleDto::class.java)

        dtoWithId.id = "99999999999999"

        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(409)
    }

    @Test
    fun testReplaceVehicleInvalidBody() {
        val dto = VehicleDto(name = "Vehicle1", model = "Vehicle2")

        val resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dtoWithId = given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(dto.name))
                .body("data.model", equalTo(dto.model))
                .extract().response().body()
                .jsonPath().getObject("data", VehicleDto::class.java)

        dtoWithId.name = ""
        dtoWithId.model = ""

        given().contentType(ContentType.JSON)
                .body(dtoWithId)
                .put(resourcePath)
                .then()
                .statusCode(400)
    }

    @Test
    fun testDeleteVehicle() {
        val dto = VehicleDto(name = "Vehicle1", model = "Vehicle2")

        val resourcePath = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/vehicles")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/vehicles")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))

        given().delete(resourcePath)
                .then()
                .statusCode(204)

        given().get("/vehicles")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testGetAllVehicles() {
        val dtos = mutableListOf<VehicleDto>()

        for (i in 0..22) {
            dtos.add(i, VehicleDto(name = "Vehicle$i", model = "Model$i"))
            given().contentType(ContentType.JSON)
                    .body(dtos[i])
                    .post("/vehicles")
                    .then()
                    .statusCode(201)
        }

        given().get("/vehicles")
                .then()
                .statusCode(200)
                .body("data.count", equalTo(dtos.size))
                .extract().body().jsonPath()
                .getMap<String, HalPage<VehicleDto>>("data")


    }


}
