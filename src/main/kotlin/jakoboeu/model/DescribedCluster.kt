package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DescribedCluster(
    @param:JsonProperty("cluster_id")
    val clusterId: Int,
    @param:JsonProperty("cluster_name")
    val clusterName: String,
    @param:JsonProperty("cluster_description")
    val clusterDescription: String,
    @param:JsonProperty("habitat_description_from_photos")
    val habitatDescription: String,
    @param:JsonProperty("representative_plot_image")
    val representativeImage: String,
    @param:JsonProperty("plots")
    val plots: Set<String>
)
