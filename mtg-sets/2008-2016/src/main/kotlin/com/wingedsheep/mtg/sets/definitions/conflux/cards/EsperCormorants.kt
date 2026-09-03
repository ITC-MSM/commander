package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Esper Cormorants
 * {2}{W}{U}
 * Artifact Creature — Bird
 * 3/3
 * Flying
 *
 * A printed [Keyword.FLYING] and nothing else — the artifact half of the type line is parsed
 * type-line data, not a script.
 */
val EsperCormorants = card("Esper Cormorants") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Artifact Creature — Bird"
    power = 3
    toughness = 3
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Warren Mahy"
        flavorText = "\"The smiths of this land must be mad to reach so far and so high for another creature to decorate.\" —Cagen Vargan, Jhessian sea scout"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c5068ed-8477-4d8d-9e37-c72474208e2d.jpg"
    }
}
