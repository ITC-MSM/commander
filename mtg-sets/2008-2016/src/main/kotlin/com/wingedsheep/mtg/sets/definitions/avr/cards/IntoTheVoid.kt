package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Into the Void
 * {3}{U}
 * Sorcery
 * Return up to two target creatures to their owners' hands.
 *
 * "Up to two target" is one optional multi-target requirement, and the body runs once per chosen
 * target ([ForEachTargetEffect]) so an illegal target on resolution doesn't take the other with it.
 *
 * Canonical printing: Avacyn Restored, the card's earliest real-expansion printing. Reprinted in
 * M15 and ORI as `Printing` rows.
 */
val IntoTheVoid = card("Into the Void") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return up to two target creatures to their owners' hands."

    spell {
        target("up to two target creatures", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0))))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Daarken"
        flavorText = "\"The cathars have their swords, the inquisitors their axes. I prefer the 'diplomatic' approach.\"\n—Terhold, archmage of Drunau"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5ddd1050-8abd-4dfe-9e52-5b56af358653.jpg?1783940717"
    }
}
