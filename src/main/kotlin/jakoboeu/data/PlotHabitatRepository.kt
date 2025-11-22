package jakoboeu.data

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import jakoboeu.model.HabitatPlot
import org.springframework.stereotype.Component

val plotHabitatSchema: CsvSchema = CsvSchema.builder()
    .addColumn("title")
    .addNumberColumn("herb")
    .addNumberColumn("shrb")
    .addNumberColumn("smll_tree")
    .addNumberColumn("lrg_tree")
    .addNumberColumn("habitat_complexity")
    .addNumberColumn("ndvi")
    .addNumberColumn("connected_greenspace_point6_area")
    .addNumberColumn("connected_greenspace_point8_area")
    .addNumberColumn("connected_tree_5m_area")
    .addNumberColumn("days_into_survey")
    .addNumberColumn("year")
    .addNumberColumn("hour_of_day")
    .addColumn("survey_square")
    .addColumn("land_use")
    .build()

@Component
class PlotHabitatRepository {
    private val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }

    fun loadPlotsHabitatData(filename: String): List<HabitatPlot> =
        csvMapper.readerFor(HabitatPlot::class.java)
            .with(plotHabitatSchema.withSkipFirstDataRow(true))
            .readValues<HabitatPlot>(javaClass.classLoader.getResourceAsStream(filename))
            .readAll()
}