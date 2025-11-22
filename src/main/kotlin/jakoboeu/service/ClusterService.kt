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
            val clusterStats = HabitatStat.entries.associateBy(keySelector = {it}, valueTransform = {
                stat -> Stats.from(habitatDataInCluster.map { habitatData -> stat.statTransfrom(habitatData.habitat) })
            })

            Cluster(clusterId, plotsInCluster!!, clusterStats)
        }
    }
}