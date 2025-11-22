package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonValue

enum class GreenspaceType(@JsonValue val value: String) {
    FORMAL_PARK("formal_park"),
    INFORMAL_PARK_OR_COMMON("grassland_or_informal_park_or_common"),
    STREET_VERGE_OR_ROUNDABOUT("street_verge_or_roundabout"),
    RESIDENTIAL_GARDEN_OR_COURTYARD("residential_garden_or_courtyard"),
    PLAYING_FIELD_OR_SPORTS_PITCH("playing_field_or_sports_pitch"),
    GOLF_COURSE("golf_course"),
    PLAYGROUND("playground"),
    ALLOTMENT_OR_COMMUNITY_GARDEN("allotment_or_community_garden"),
    CEMETERY_OR_CHURCHYARD("cemetery_or_churchyard"),
    WOODLAND_OR_COPSE("woodland_or_copse"),
    RIPARIAN_OR_CANAL_EDGE("riparian_or_canal_edge"),
    DERELICT_OR_VACANT_LAND("derelict_or_vacant_land"),
    WETLANDS("swamp_or_fen_or_wetlands"),
    CAR_PARK("car_park"),
    PUBLIC_SQUARE_OR_COMMERCIAL_SPACE("public_square_or_commercial_space"),
    AGRICULTURAL_FIELD_OR_PASTURE("agricultural_field_or_pasture"),
    OTHER("other")
}