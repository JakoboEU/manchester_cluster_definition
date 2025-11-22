package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

enum class DominantVegetationType(@JsonValue val value: String) {
    NONE_OR_PAVED("none_or_paved"),
    SHORT_MOWN_GRASS("short_mown_grass"),
    TALL_HERB_MEADOW("tall_herb_meadow"),
    SHRUB_DOMINATED("shrub_dominated"),
    TREE_DOMINATED_OPEN_UNDERSTOREY("tree_dominated_open_understorey"),
    CLOSED_WOODLAND("closed_woodland"),
    WATER_DOMINATED("water_dominated"),
    MIXED("mixed")
}

enum class PathType(@JsonValue val value: String) {
    NONE("none"),
    INFORMAL_EARTH_OR_MUD("informal_earth_or_mud"),
    MOWN_GRASS_TRACK("mown_grass_track"),
    GRAVEL("gravel"),
    PAVED_FOOTPATH("paved_footpath"),
    SHARED_USE_CYCLE_PATH("shared_use_cycle_path"),
    ROAD_FOR_MOTOR_VEHICLES("road_for_motor_vehicles")
}

enum class PathWidthCategory(@JsonValue val value: String) {
    NONE("none"),
    NARROW_UNDER_1M("narrow_under_1m"),
    MEDIUM_1_3M("medium_1_3m"),
    WIDE_OVER_3M("wide_over_3m")
}

enum class ApparentPublicAccess(@JsonValue val value: String) {
    CLEARLY_PUBLIC("clearly_public"),
    UNCERTAIN("uncertain"),
    LIKELY_PRIVATE_OR_GARDEN("likely_private_or_garden")
}

enum class VisibilityDistanceCategory(@JsonValue val value: String) {
    SHORT_LESS_10M("short_less_10m"),
    MEDIUM_10_30M("medium_10_30m"),
    LONG_OVER_30M("long_over_30m")
}

enum class MaintenanceIntensity(@JsonValue val value: String) {
    VERY_LOW_WILD("very_low_wild"),
    LOW_SEMI_WILD("low_semi_wild"),
    MODERATE("moderate"),
    HIGH_FORMAL("high_formal")
}

enum class MowingEvidence(@JsonValue val value: String) {
    NONE("none"),
    RECENT_MOW("recent_mow"),
    REGULARLY_MOWN_LAWN("regularly_mown_lawn")
}

data class VegetationStructure(
    @param:JsonProperty("dominant_vegetation_type")
    val dominantVegetationType: DominantVegetationType,
)

data class AdjacentInfrastructure(
    @param:JsonProperty("buildings_visible")
    val buildingsVisible: Boolean,

    @param:JsonProperty("road_or_parking_visible")
    val roadOrParkingVisible: Boolean,

    @param:JsonProperty("railway_infrastructure_visible")
    val railwayInfrastructureVisible: Boolean,

    @param:JsonProperty("water_body_visible")
    val waterBodyVisible: Boolean,

    @param:JsonProperty("play_equipment_present")
    val playEquipmentPresent: Boolean,
)

data class AccessibilityAndExperience(
    @param:JsonProperty("path_type")
    val pathType: PathType,

    @param:JsonProperty("path_width_category")
    val pathWidthCategory: PathWidthCategory,

    @param:JsonProperty("open_space_presence")
    val openSpacePresence: Boolean,

    @param:JsonProperty("visibility_distance_category")
    val visibilityDistanceCategory: VisibilityDistanceCategory,
)

data class ClutterAndCondition(
    @param:JsonProperty("visual_clutter_score_1_5")
    val visualClutterScore1To5: Int,

    @param:JsonProperty("litter_or_dumping_visible")
    val litterOrDumpingVisible: Boolean,

    @param:JsonProperty("parked_cars_prominent")
    val parkedCarsProminent: Boolean,

    @param:JsonProperty("street_furniture_visible")
    val streetFurnitureVisible: Boolean,

    @param:JsonProperty("number_of_people_visible")
    val numberOfPeopleVisible: Int,

    @param:JsonProperty("maintenance_intensity")
    val maintenanceIntensity: MaintenanceIntensity,

    @param:JsonProperty("mowing_evidence")
    val mowingEvidence: MowingEvidence
)


data class ImageContents(
    @param:JsonProperty("vegetation_structure")
    val vegetationStructure: VegetationStructure,

    @param:JsonProperty("accessibility_and_experience")
    val accessibilityAndExperience: AccessibilityAndExperience,

    @param:JsonProperty("clutter_and_condition")
    val clutterAndCondition: ClutterAndCondition,

    @param:JsonProperty("adjacent_infrastructure")
    val adjacentInfrastructure: AdjacentInfrastructure,

    @param:JsonProperty("photographic_clarity_score_1_5")
    val photographicClarityScore1To5: Int,

    @param:JsonProperty("short_notes")
    val shortNotes: String
)

data class ImageVision(
    @param:JsonProperty("title")
    val title: String,

    @param:JsonProperty("imageContents")
    val imageContents: ImageContents,

    @param:JsonProperty("apparentGreenspaceType")
    val apparentGreenspaceType: GreenspaceType,

    @param:JsonProperty("apparentGreenspaceTypeCertainty1to5")
    val apparentGreenspaceTypeCertainty1to5: Int,
)

class ImageVisionVisitorContext : VisitorContext() {
    override fun javaTypeToUrn(jt: JavaType): String {
        // You can choose any scheme / prefix you like here
        val simpleName = jt.rawClass.simpleName
        return "plot-image-vision:$simpleName:v1"
    }
}

fun imageVisionSchemaJson(): String {
    val mapper = jacksonObjectMapper()
        .registerModule(
            KotlinModule.Builder().build()
        )

    val visitor = SchemaFactoryWrapper()
    visitor.setVisitorContext(ImageVisionVisitorContext())
    mapper.acceptJsonFormatVisitor(ImageVision::class.java, visitor)
    val schema: JsonSchema = visitor.finalSchema()

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema)
}
