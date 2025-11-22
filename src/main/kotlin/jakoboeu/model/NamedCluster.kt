package jakoboeu.model

data class NamedCluster(
    val clusterId: Int,
    val clusterName: String,
    val clusterDescription: String,
    val plots: Set<String>
)
