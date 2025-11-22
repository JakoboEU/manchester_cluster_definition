package jakoboeu

import jakoboeu.data.ClusterRepository
import jakoboeu.data.ImageVisionRepository
import jakoboeu.data.PlotHabitatRepository
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
        worker.classifyClusters()
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
    val imageVisionRepository: ImageVisionRepository,
    val clusterRepository: ClusterRepository,
) {
    fun classifyClusters() {
        println("Read ${plotHabitatRepository.loadPlotsHabitatData("./insect-plant-predictors.csv").size} habitat plots")
        println("Read ${clusterRepository.loadClusters("./insect-plant-clusters.csv", K_CHOICE).size} cluster definitions")
        println("Read ${imageVisionRepository.loadImageVision("./image_classification.json").size} image vision definitions")
    }
}
