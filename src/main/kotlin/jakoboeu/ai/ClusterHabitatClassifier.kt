package jakoboeu.ai

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakoboeu.model.Cluster
import jakoboeu.model.HabitatStat
import jakoboeu.model.NamedCluster
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Component

data class ClusterDefinition(
    @param:JsonProperty("cluster_id")
    val clusterId: Int,
    @param:JsonProperty("cluster_name")
    val clusterName: String,
    @param:JsonProperty("cluster_description")
    val clusterDescription: String,
    @param:JsonProperty("short_notes")
    val shortNotes: String,
)

data class ClusterDefinitions(val definitions: List<ClusterDefinition>)

fun clusterDefinitionSchema(): String {
    val mapper = jacksonObjectMapper()
        .registerModule(
            KotlinModule.Builder().build()
        )

    val visitor = SchemaFactoryWrapper()
    mapper.acceptJsonFormatVisitor(ClusterDefinitions::class.java, visitor)
    val schema: JsonSchema = visitor.finalSchema()
    schema.id = "urn:cluster-definition:v1"
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema)
}

@Component
class ClusterHabitatClassifier(
    private val chatModel: ChatModel,
    private val objectMapper: ObjectMapper) {

    private val chat = ChatClient.create(this.chatModel)

    fun classifyClusters(clusters: List<Cluster>) : List<NamedCluster> {
        val prompt = """
            You are an urban ecology and remote-sensing expert.

            I will give you descriptive statistics for several CLUSTERS of urban habitat plots. 
            For each cluster, I will provide variables ${HabitatStat.entries.joinToString(", ") { it.value }}
            For each variable within each cluster, I will give the mean, median, mode, standard deviation, and lower/upper quartiles across all plots in that cluster.
            
            YOUR TASK
            ---------
            For each cluster:
            1. infer what type of habitat or vegetation structure it most likely represents.
            2. Propose a SHORT, MEANINGFUL NAME for each cluster that an urban ecologist or planner would immediately understand.
            3. Use ecological / land-cover terms rather than numeric labels. Examples:
               - High shrub cover, low tree cover → “scrub” or “shrub-dominated”.
               - Very low NDVI, very low vegetation cover, high impervious cover → “impervious / hard-standing”.
               - High NDVI, high short herb/grass cover, very low shrubs and trees → “amenity grassland / lawn”.
               - High NDVI, high tree cover, moderate shrub cover → “woodland” or “tree-dominated”.
            4. Interpret “high” and “low” primarily RELATIVE TO THE OTHER CLUSTERS (not just in absolute terms).
            5. The name for clusters must be unique, and allow clusters to be clearly, meaningfully, and uniquely identified.
            6. Provide a concise description suitable for a methods/results section in a scientific journal.
            7. Provide any notes about why a name and description was chosen, or how additional data might allow you to improve the naming.
            
            OUTPUT FORMAT
            --------------
            The response must match this JSON schema:
            ${clusterDefinitionSchema()}
            
            NAMING GUIDELINES
            ------------------
            - Prefer concise, ecologically meaningful terms that are common in urban ecology / landscape ecology, such as:
              “amenity grassland”, “lawn”, “scrub”, “young woodland”, “mature woodland”, 
              “tree-lined street verge”, “impervious hard-standing”, “mixed shrub–tree”, “parkland trees”, etc.
            - Only use such terms when they match the statistics.
            - If a cluster is ambiguous, still propose the BEST-FIT name, but state briefly why it is ambiguous in the short-notes field.
            - Do NOT invent extra clusters; use only the cluster IDs I provide.
            
            VARIABLES:
            ----------
            ${HabitatStat.NDVI.value}: The NDVI of the plot
            ${HabitatStat.HABITAT_COMPLEXITY.value}: The SHDI of the different land cover types.
            ${HabitatStat.HERB_COVER.value}: The proportion of the plot covered by vegetation under 0.5 m
            ${HabitatStat.SHRUB_COVER.value}: The proportion of the plot covered by vegetation between 0.5 and 2 m
            ${HabitatStat.SMALL_TREE_COVER.value}: The proportion of the plot covered by vegetation between 2 and 5 m
            ${HabitatStat.LARGE_TREE_COVER.value}: The proportion of the plot covered by vegetation over 5 m
            ${HabitatStat.BUILT_COVER.value}: The proportion of the plot not covered by vegetation
            
            CLUSTER DATA
            ------------
            ${objectMapper.writeValueAsString(clusters)}
        """.trimIndent()

        val json = chat.prompt()
            .options(
                OpenAiChatOptions.builder()
                    .model("gpt-4.1-mini")
                    .temperature(0.0)
                    .build())
            .user { u -> u.text(prompt) }
            .call()
            .content()

        val strippedJson = json!!.substring(json.indexOf('{'))
        val result = objectMapper.readValue(strippedJson, ClusterDefinitions::class.java)

        require(result.definitions.size == clusters.size) {
            "Classification result size ${result.definitions.size} does not match input size ${clusters.size}"
        }
        require(result.definitions.map { it.clusterId } == clusters.map { it.clusterId }) {
            "Classification result ${result.definitions.map { it.clusterId }} does not contain the same cluster IDs as the input ${clusters.map { it.clusterId }}"
        }

        val indexedClusters = clusters.associateBy { it.clusterId }
        return result.definitions.map {
            println("Notes on ${it.clusterId}: ${it.shortNotes}")
            val cluster = indexedClusters[it.clusterId]!!
            NamedCluster(it.clusterId, it.clusterName, it.clusterDescription, cluster.plots)
        }
    }
}