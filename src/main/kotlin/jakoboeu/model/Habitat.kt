package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Habitat(
    @field:JsonProperty("herb") val herbaceousCover: Double,
    @field:JsonProperty("shrb") val shrubCover: Double,
    @field:JsonProperty("smll_tree") val smallTreeCover: Double,
    @field:JsonProperty("lrg_tree") val largeTreeCover: Double,
    @field:JsonProperty("ndvi") val ndvi: Double,
    @field:JsonProperty("habitatComplexity") val habitatComplexity: Double,
) {
    constructor() : this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}
