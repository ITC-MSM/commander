package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blades of Velis Vel
 * {1}{R}
 * Kindred Instant — Shapeshifter
 *
 * Changeling (This card is every creature type.)
 * Up to two target creatures each get +2/+0 and gain all creature types until end of turn.
 *
 * "Up to two target creatures **each** get …" is a plural requirement, so the pump runs once per
 * chosen target ([ForEachTargetEffect] over `ContextTarget(0)`) rather than once against the
 * requirement as a whole. "Gains all creature types" is modelled by granting Changeling — the
 * engine expands that keyword into every creature type (CR 702.73).
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val BladesOfVelisVel = card("Blades of Velis Vel") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Instant — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Up to two target creatures each get +2/+0 and gain all creature types until end of turn."

    keywords(Keyword.CHANGELING)

    spell {
        target("target creature", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            listOf(
                Effects.ModifyStats(2, 0, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.CHANGELING, EffectTarget.ContextTarget(0)),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Ron Spencer"
        flavorText = "\"The changing kind suffers as we do. We must join as one to quench our tyrants!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a3ac629-a8c9-4b84-a8ea-b775d7913238.jpg?1783942882"
    }
}
