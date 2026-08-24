package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Executioner's Capsule
 * {B}
 * Artifact
 * {1}{B}, {T}, Sacrifice this artifact: Destroy target nonblack creature.
 *
 * The black member of the Alara capsule cycle — Dispeller's Capsule with a different filter. The
 * cost is a [Costs.Composite] of mana, [Costs.Tap] and [Costs.SacrificeSelf], so the shell is spent
 * to fire it. "Nonblack creature" is [TargetFilter.Creature]`.notColor(BLACK)`, evaluated against
 * projected state, and [Effects.Destroy] is the `byDestruction` graveyard move so indestructible and
 * regeneration still apply.
 */
val ExecutionersCapsule = card("Executioner's Capsule") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "{1}{B}, {T}, Sacrifice this artifact: Destroy target nonblack creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK)))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Warren Mahy"
        flavorText = "There is always a moment of trepidation before opening a message capsule, for fear of the judgment that might be contained within."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48185059-fff9-47b0-9774-ad37dee345ed.jpg"
    }
}
