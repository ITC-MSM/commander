package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Surtland Frostpyre
 * Land
 * This land enters tapped.
 * {T}: Add {R}.
 * {2}{U}{U}{R}, {T}, Sacrifice this land: Scry 2. This land deals 2 damage to each creature. Activate only as a sorcery.
 *
 * The Izzet realm land: a scry stapled to a symmetric two-point sweeper. The damage hits *each*
 * creature including your own, and the land is already sacrificed when it resolves — the damage
 * source is a permanent that has left the battlefield, which is why the printed text says "This land
 * deals" rather than naming a creature.
 */
val SurtlandFrostpyre = card("Surtland Frostpyre") {
    manaCost = ""
    colorIdentity = "RU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R}.\n" +
        "{2}{U}{U}{R}, {T}, Sacrifice this land: Scry 2. This land deals 2 damage to each creature. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    // {T}: Add {R}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED, 1)
        manaAbility = true
    }

    // {2}{U}{U}{R}, {T}, Sacrifice this land: Scry 2. This land deals 2 damage to each creature.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{U}{U}{R}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.Composite(
            Effects.Scry(2),
            Patterns.Group.dealDamageToAll(2, GroupFilter(GameObjectFilter.Creature))
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "271"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b02149a-4a8f-4be9-9944-3d216248b549.jpg"
    }
}
