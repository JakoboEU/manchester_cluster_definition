package jakoboeu.ai

import com.fasterxml.jackson.databind.ObjectMapper
import jakoboeu.model.DescribedCluster
import jakoboeu.model.ImageVision
import jakoboeu.model.NamedCluster
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.stereotype.Component

data class DescribedClusterResult(
    val habitatDescription: String,
    val representativeImage: String,
)

fun describedClusterSchema(): String {
    val converter = BeanOutputConverter(DescribedClusterResult::class.java)
    return converter.jsonSchema
}

@Component
class ClusterImageSelector(
    private val chatModel: ChatModel,
    private val objectMapper: ObjectMapper) {

    private val chat = ChatClient.create(this.chatModel)


    fun selectAndDescribeImages(cluster: NamedCluster, plotVision: List<ImageVision>): DescribedCluster {
        val prompt = """
            You are an urban ecology and remote-sensing expert.
            I will give you a name and a description of a type of habitat.
            I will provide descriptions of images of habitat plots of that type.
            
            Your Task:
            ----------
            1. Select a single image from the described images that best illustrates that habitat type:
                a) select clear images that are not blurred
                b) images should be free of clutter
                c) there should be some depth to the image
            2. Write a short description of the habitat type based on the images provided: 
                a) use only information that is provided, do not guess from the vegetation type description.
                b) should be written in a format suitable for an academic journal. 
                c) the description should refer to plots rather than images or scenes.
                d) the description should only include visible habitat rather than clutter or objects in the images
            
            Habitat Type:
            -------------
            Name: ${cluster.clusterName}
            Description: ${cluster.clusterDescription}
            
            Images:
            -------
            ${plotVision.filter { it.title in cluster.plots }}
        """.trimIndent()

        val json = chat.prompt()
            .options(
                OpenAiChatOptions.builder()
                    .model("gpt-5-mini")
                    .responseFormat(ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, describedClusterSchema()))
                    .temperature(1.0)
                    .build())
            .user { u -> u.text(prompt) }
            .call()
            .content()

        val strippedJson = json!!.substring(json.indexOf('{'))
        val result = objectMapper.readValue(strippedJson, DescribedClusterResult::class.java)

        require(cluster.plots.contains(result.representativeImage)) {
            "The selected representative image (${result.representativeImage} is not present in the cluster ${cluster.plots}"
        }

        return DescribedCluster(cluster.clusterId, cluster.clusterName, cluster.clusterDescription, result.habitatDescription, result.representativeImage, cluster.plots)
    }
}