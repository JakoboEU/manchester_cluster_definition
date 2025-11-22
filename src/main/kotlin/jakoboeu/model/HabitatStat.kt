package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonValue

enum class HabitatStat(@JsonValue val value: String, val statTransfrom: (Habitat) -> Double) {
    NDVI("ndvi", {it.ndvi}),
    HABITAT_COMPLEXITY("habitat_complexity", {it.habitatComplexity}),
    HERB_COVER("herb_cover", {it.herbaceousCover}),
    SHRUB_COVER("shrub_cover", {it.shrubCover}),
    SMALL_TREE_COVER("small_tree_cover", {it.smallTreeCover}),
    LARGE_TREE_COVER("large_tree_cover", {it.largeTreeCover}),
    BUILT_COVER("built_cover", {1 - (it.herbaceousCover + it.shrubCover + it.smallTreeCover + it.largeTreeCover)})
}