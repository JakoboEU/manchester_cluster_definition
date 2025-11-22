package jakoboeu.data

import com.fasterxml.jackson.databind.ObjectMapper
import jakoboeu.model.ImageVision
import org.springframework.stereotype.Component

@Component
class ImageVisionRepository(val objectMapper: ObjectMapper) {

    fun loadImageVision(filename: String): List<ImageVision> =
        objectMapper.readValue(javaClass.classLoader.getResourceAsStream(filename), Array<ImageVision>::class.java).toList()
}