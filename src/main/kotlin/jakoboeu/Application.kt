package jakoboeu

import jakoboeu.ai.ClusterHabitatClassifier
import jakoboeu.data.ClusterRepository
import jakoboeu.data.ImageVisionRepository
import jakoboeu.data.PlotHabitatRepository
import jakoboeu.service.ClusterService
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import kotlin.system.exitProcess

const val K_CHOICE = 9

@SpringBootApplication
class Application {
    @Bean
    fun runner(worker: Worker) = ApplicationRunner {
        println("---------------------")
        println("Insect/Plant Clusters")
        println("---------------------")
        worker.classifyClusters("./insect-plant-predictors.csv", "./insect-plant-clusters.csv")

        println("-------------")
        println("Bird Clusters")
        println("-------------")
        worker.classifyClusters("./bird-predictors.csv", "./bird-clusters.csv")
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
) {
    val imageVisionData = imageVisionRepository.loadImageVision("./image_classification.json")

    fun classifyClusters(predictorFilename: String, clusterFilename: String) {
        val plotsHabitatData = plotHabitatRepository.loadPlotsHabitatData(predictorFilename)
        val clusterAssignmentData = clusterRepository.loadClusters(clusterFilename, K_CHOICE)

        println("Read ${plotsHabitatData.size} habitat plots")
        println("Read ${clusterAssignmentData.size} cluster definitions")
        println("Read ${imageVisionData.size} image vision definitions")

        val clusters = clusterService.createClusterData(plotsHabitatData, clusterAssignmentData)
        val clusterDefinitions = clusterHabitatClassifier.classifyClusters(clusters)

        clusterDefinitions.forEach {
            println(it)
        }
    }
}
