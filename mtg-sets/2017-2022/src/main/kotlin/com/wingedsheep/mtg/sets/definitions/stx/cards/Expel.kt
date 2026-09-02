package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Expel — Strixhaven: School of Mages #18 (canonical printing)
 * {2}{W} · Instant
 *
 * Exile target tapped creature.
 *
 * A plain [Effects.Exile] whose tapped restriction lives in the target requirement
 * ([Targets.TappedCreature]), so an untapped creature is not a legal target at all rather than
 * being targeted and then spared.
 */
val Expel = card("Expel") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Exile target tapped creature."

    spell {
        val victim = target("target tapped creature", Targets.TappedCreature)
        effect = Effects.Exile(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Billy Christian"
        flavorText = "Quintorius was a daydreamer, far happier digging through history books than practicing battle tactics. He agreed with the military academy on only one thing: he did not belong in their ranks."
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be517a58-b7ee-4213-98a5-8c19e1b2def6.jpg?1783927392"
    }
}
