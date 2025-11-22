package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped

@JsonIgnoreProperties(ignoreUnknown = true)
data class HabitatPlot(
    @field:JsonProperty("title") val identity: String,
    @field:JsonUnwrapped
    val habitat: Habitat
) {
    constructor() : this("", Habitat())
}
