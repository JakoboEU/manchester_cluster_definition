package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ClusterDefinition(
    @param:JsonProperty("cluster_id")
    val clusterId: Int,
    @param:JsonProperty("cluster_name")
    val clusterName: String,
    @param:JsonProperty("cluster_description")
    val clusterDescription: String,
    @param:JsonProperty("short_notes")
    val shortNotes: String,
)
