package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pitiless Pontiff — Ravnica Allegiance #194
 * {W}{B} · Creature — Vampire Cleric · 2 / 2
 *
 * "Sacrifice another creature" is [Costs.SacrificeAnother] — the `excludeSelf` sibling of
 * [Costs.Sacrifice], which is what keeps the Pontiff from eating itself. Both keywords are
 * granted by the same resolution, so they are one [CompositeEffect] rather than two abilities.
 */
val PitilessPontiff = card("Pitiless Pontiff") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Vampire Cleric"
    power = 2
    toughness = 2
    oracleText = "{1}, Sacrifice another creature: This creature gains deathtouch and indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.Composite(listOf(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "194"
        artist = "Yongjae Choi"
        flavorText = "\"Pay in gold. Pay in blood. Pay with the servitude of your spirit kin. But pay you must.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98f66c03-cf79-4de0-be55-b921cbdc5038.jpg"
    }
}
