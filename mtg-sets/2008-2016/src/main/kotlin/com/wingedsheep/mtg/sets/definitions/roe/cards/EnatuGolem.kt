package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Enatu Golem
 * {6}
 * Artifact Creature — Golem
 * 3 / 5
 *
 * When this creature dies, you gain 4 life.
 *
 * Modeling notes:
 *  - [Triggers.Dies] is precisely the battlefield → graveyard zone change Assay compiles this line
 *    to (`ZoneChangeEvent` from Battlefield to Graveyard) with the default `SELF` binding, so no
 *    filter and no binding override are needed.
 *  - No `triggerZone` override: on a dies trigger, setting `triggerZone = GRAVEYARD` *replaces* the
 *    default `{BATTLEFIELD}` active-zone set rather than adding to it, which stops the ability
 *    seeing the death at all. The rest of the corpus's dies-triggers (Guardian Automaton, Polluted
 *    Dead, Maalfeld Twins) all leave it alone, and so does this one.
 *  - "you gain 4 life" is the untargeted [Effects.GainLife], whose default recipient is already the
 *    ability's controller — writing `EffectTarget.Controller` here would restate a default.
 */
val EnatuGolem = card("Enatu Golem") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 5
    oracleText = "When this creature dies, you gain 4 life."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Daniel Ljunggren"
        flavorText = "Golems conjured from the debris of Enatu Temple provided a sturdy but expendable first line of defense against the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74b2e63d-61c6-46e4-9a6f-56653c49b2ea.jpg?1783941957"
    }
}
