package jakoboeu.model

data class Cluster(
    val clusterId: Int,
    val plots: Set<String>,
    val habitat: Map<String,Stats>
)
