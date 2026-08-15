package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect

/**
 * Skycoach Waypoint — Secrets of Strixhaven #261
 * Land
 *
 * {T}: Add {C}.
 * {3}, {T}: Target creature becomes prepared. (Only creatures with prepare spells can become prepared.)
 *
 * Prepare (Secrets of Strixhaven): the second ability targets any creature, but only a creature whose
 * card has the [com.wingedsheep.sdk.model.CardLayout.PREPARE] layout can actually become prepared —
 * [Effects.BecomePrepared] is a no-op against any other creature (matching the reminder text). Becoming
 * prepared creates a copy of that creature's prepare spell in its controller's exile that they may cast;
 * casting the copy unprepares the creature.
 */
val SkycoachWaypoint = card("Skycoach Waypoint") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{3}, {T}: Target creature becomes prepared. (Only creatures with prepare spells can become prepared.)"

    // {T}: Add {C}.
    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {3}, {T}: Target creature becomes prepared.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val t = target("target creature", Targets.Creature)
        effect = Effects.BecomePrepared(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "261"
        artist = "Jorge Jacinto"
        flavorText = "\"Do you have all your books? And your stylus? And your extra uniform? And your lucky scarf?\"\n—Overheard at Hookiver Station"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/6747657b-5ce4-4dbd-b924-ca1f7119faf7.jpg?1775938823"
    }
}
