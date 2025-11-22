package jakoboeu.service

import jakoboeu.model.Cluster
import jakoboeu.model.HabitatPlot
import jakoboeu.model.HabitatStat
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
                HabitatStat.NDVI to Stats.from(habitatDataInCluster.map { it.habitat.ndvi }),
                HabitatStat.HABITAT_COMPLEXITY to Stats.from(habitatDataInCluster.map { it.habitat.habitatComplexity }),
                HabitatStat.HERB_COVER to Stats.from(habitatDataInCluster.map { it.habitat.herbaceousCover }),
                HabitatStat.SHRUB_COVER to Stats.from(habitatDataInCluster.map { it.habitat.shrubCover }),
                HabitatStat.SMALL_TREE_COVER to Stats.from(habitatDataInCluster.map { it.habitat.smallTreeCover }),
                HabitatStat.LARGE_TREE_COVER to Stats.from(habitatDataInCluster.map { it.habitat.largeTreeCover }),
            ))
        }
    }
}