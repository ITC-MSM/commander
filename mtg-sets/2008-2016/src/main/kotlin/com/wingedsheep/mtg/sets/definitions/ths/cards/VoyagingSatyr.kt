package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Voyaging Satyr
 * {1}{G}
 * Creature — Satyr Druid
 * 1 / 2
 *
 * {T}: Untap target land.
 */
val VoyagingSatyr = card("Voyaging Satyr") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Satyr Druid"
    power = 1
    toughness = 2
    oracleText = "{T}: Untap target land."

    activatedAbility {
        cost = Costs.Tap
        val land = target("target", Targets.Land)
        effect = Effects.Untap(land)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Tyler Jacobson"
        flavorText = "\"None can own the land's bounty. The gods made this world for all to share its riches. And I'm not just saying that because you caught me stealing your fruit.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/155ae6c0-5085-45f1-ba9f-508d501fee2c.jpg"
    }
}
