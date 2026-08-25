package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lightning Mare
 * {R}{R}
 * Creature — Elemental Horse
 * 3/1
 * This spell can't be countered.
 * This creature can't be blocked by blue creatures.
 * {1}{R}: This creature gets +1/+0 until end of turn.
 *
 * - "This spell can't be countered" is a property of the *card*, not a static ability of the
 *   permanent, so it is the top-level `cantBeCountered` var the stack consults while the spell is
 *   still on it.
 * - The evasion is a single [CantBeBlockedBy] over a coloured *creature* filter, evaluated against
 *   projected state so a blocker's current colour decides, not its printed one.
 */
val LightningMare = card("Lightning Mare") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Horse"
    power = 3
    toughness = 1
    oracleText = "This spell can't be countered.\n" +
        "This creature can't be blocked by blue creatures.\n" +
        "{1}{R}: This creature gets +1/+0 until end of turn."

    cantBeCountered = true

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withColor(Color.BLUE))
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Lucas Graciano"
        flavorText = "When it passes, storm clouds bolt across the land."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2ca822a3-4793-4cbc-a2f3-f43985e7ce8f.jpg"
    }
}
