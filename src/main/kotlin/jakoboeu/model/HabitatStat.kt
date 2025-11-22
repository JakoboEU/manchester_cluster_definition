package jakoboeu.model

enum class HabitatStat(val statTransfrom: (Habitat) -> Double) {
    NDVI({it.ndvi}),
    HABITAT_COMPLEXITY({it.habitatComplexity}),
    HERB_COVER({it.herbaceousCover}),
    SHRUB_COVER({it.shrubCover}),
    SMALL_TREE_COVER({it.smallTreeCover}),
    LARGE_TREE_COVER({it.largeTreeCover}),
}