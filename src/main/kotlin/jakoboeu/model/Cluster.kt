package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Cluster(
    @param:JsonProperty("cluster_id")
    val clusterId: Int,
    @JsonIgnore
    val plots: Set<String>,
    @param:JsonProperty("habitat")
    val habitat: Map<HabitatStat,Stats>
)
