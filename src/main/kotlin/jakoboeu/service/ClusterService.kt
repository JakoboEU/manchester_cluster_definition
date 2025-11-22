package jakoboeu.service

import jakoboeu.model.Cluster
import jakoboeu.model.HabitatPlot
import jakoboeu.model.Stats
import org.springframework.stereotype.Component

@Component
class ClusterService {

    fun createClusterData(habitatData: List<HabitatPlot>, clusterAssignment: Map<String,Int>): List<Cluster> {
        val plotsIndexedByCluster = clusterAssignment.entries.groupBy(keySelector = { it.value }, valueTransform = { it.key })

        return plotsIndexedByCluster.keys.map { clusterId ->
            val plotsInCluster = plotsIndexedByCluster[clusterId]?.toSet()
            val habitatDataInCluster = habitatData.filter { plotsInCluster?.contains(it.identity) == true  }
            Cluster(clusterId, plotsInCluster!!, mapOf(
                "NDVI" to Stats.from(habitatDataInCluster.map { it.habitat.ndvi }),
                "habitat complexity (shdi of cover)" to Stats.from(habitatDataInCluster.map { it.habitat.habitatComplexity }),
                "proportion herbaceous cover (< 0.5 m)" to Stats.from(habitatDataInCluster.map { it.habitat.herbaceousCover }),
                "proportion shrub cover (0.5 - 2 m)" to Stats.from(habitatDataInCluster.map { it.habitat.shrubCover }),
                "proportion small tree cover (2 - 5 m)" to Stats.from(habitatDataInCluster.map { it.habitat.smallTreeCover }),
                "proportion large tree cover (> 5 m)" to Stats.from(habitatDataInCluster.map { it.habitat.largeTreeCover }),
            ))
        }
    }
}