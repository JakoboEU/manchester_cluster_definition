package jakoboeu

import com.fasterxml.jackson.databind.ObjectMapper
import jakoboeu.ai.ClusterHabitatClassifier
import jakoboeu.ai.ClusterImageSelector
import jakoboeu.data.ClusterRepository
import jakoboeu.data.ImageVisionRepository
import jakoboeu.data.PlotHabitatRepository
import jakoboeu.model.DescribedCluster
import jakoboeu.service.ClusterService
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.system.exitProcess

const val K_CHOICE = 9

@SpringBootApplication
class Application {
    @Bean
    fun runner(worker: Worker) = ApplicationRunner {
        println("---------------------")
        println("Insect/Plant Clusters")
        println("---------------------")
        val insectPlantClusters = worker.classifyClusters("./insect-plant-predictors.csv", "./insect-plant-clusters.csv", "insect-plant-clusters.json")

        println("-------------")
        println("Bird Clusters")
        println("-------------")
        worker.classifyClusters("./bird-predictors.csv", "./bird-clusters.csv", "bird-clusters.json", insectPlantClusters)
    }
}

fun main(args: Array<String>) {

    if (System.getenv("GS_MANC_CLASS_OPENAI_API_KEY") == null) {
        throw IllegalStateException("Missing KEY in env vars");
    }
    val app = SpringApplication(Application::class.java)
    app.setWebApplicationType(WebApplicationType.NONE)
    val ctx: ConfigurableApplicationContext? = app.run(*args)
    val code = SpringApplication.exit(ctx);
    exitProcess(code);
}

@Service
class Worker(
    val plotHabitatRepository: PlotHabitatRepository,
    val clusterRepository: ClusterRepository,
    imageVisionRepository: ImageVisionRepository,
    val clusterService: ClusterService,
    val clusterHabitatClassifier: ClusterHabitatClassifier,
    val clusterImageSelector: ClusterImageSelector,
    val objectMapper: ObjectMapper,
) {
    val imageVisionData = imageVisionRepository.loadImageVision("./image_classification.json")

    fun classifyClusters(predictorFilename: String, clusterFilename: String, outputFilename: String, previouslyDescribedClusters: List<DescribedCluster> = listOf()): List<DescribedCluster> {
        val plotsHabitatData = plotHabitatRepository.loadPlotsHabitatData(predictorFilename)
        val clusterAssignmentData = clusterRepository.loadClusters(clusterFilename, K_CHOICE)

        println("Read ${plotsHabitatData.size} habitat plots")
        println("Read ${clusterAssignmentData.size} cluster definitions")
        println("Read ${imageVisionData.size} image vision definitions")

        val clusters = clusterService.createClusterData(plotsHabitatData, clusterAssignmentData)

        val namedClusters = if(previouslyDescribedClusters.isEmpty())
            clusterHabitatClassifier.classifyClusters(clusters)
        else
            clusterHabitatClassifier.classifyClusters(clusters, previouslyDescribedClusters)

        val clusterDefinitions = namedClusters.map {
            clusterImageSelector.selectAndDescribeImages(it, imageVisionData)
        }

        save(outputFilename, clusterDefinitions)
        return clusterDefinitions
    }

    fun save(outputFilename: String, output: List<DescribedCluster>) {
        val file = java.io.File(outputFilename)

        Files.newBufferedWriter(
            file.toPath(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).use { writer ->
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, output)
        }
    }
}


