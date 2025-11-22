package jakoboeu.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.springframework.stereotype.Component

@Component
class ClusterRepository {
    private val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }
    private val mapTypeRef = object : TypeReference<Map<String, String>>() {}
    private val rowK = "k"

    fun loadClusters(filename: String, cluster: Int) : Map<String,Int> {
        openResourceCsv(filename).use { input ->
            val schema = CsvSchema.emptySchema().withHeader()

            val reader = csvMapper
                .readerFor(mapTypeRef)
                .with(schema)

            val rows = reader.readValues<Map<String, String>>(input).readAll()

            val matchingRow = rows.firstOrNull { row: Map<String,String> ->
                row[rowK] == cluster.toString()
            } ?: throw NoSuchElementException(
                "No row found where k == $cluster"
            )

            return matchingRow
                .filterKeys { it !in setOf(rowK) }
                .mapValues { (_, value) ->
                    value.toIntOrNull()
                        ?: throw IllegalArgumentException("Non-integer value '$value' in CSV")
                }
        }
    }

    private fun openResourceCsv(resourceName: String) =
        requireNotNull(object {}.javaClass.classLoader.getResourceAsStream(resourceName)) {
            "Resource $resourceName not found on classpath"
        }
}