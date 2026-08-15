package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Pierce the Sky
 * {1}{G}
 * Instant
 * Pierce the Sky deals 7 damage to target creature with flying.
 */
val PierceTheSky = card("Pierce the Sky") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Pierce the Sky deals 7 damage to target creature with flying."

    spell {
        val t = target("target", TargetCreature(
            filter = TargetFilter.Creature.withKeyword(Keyword.FLYING)
        ))
        effect = Effects.DealDamage(7, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Slawomir Maniak"
        flavorText = "\"Llanowar elves conceal their ballistae in the upper canopy of the forest, ready to clear the skies of any intruder.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df491512-ba8a-4ba5-ad42-338190201170.jpg?1562744164"
    }
}
