package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Furnace Hellkite — Modern Horizons 3 #121
 * {5}{R}{R} · Artifact Creature — Dragon · 5/5
 *
 * Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)
 * Flying
 * {R}: This creature gets +1/+0 until end of turn.
 *
 * Pure composition: [KeywordAbility.Affinity] for [CardType.ARTIFACT] (cf. Broodstar, Myr Enforcer)
 * plus the standard firebreathing activated ability (cf. Shivan Dragon). Affinity only shaves the
 * generic portion of the cost, so seven artifacts floors this at {R}{R} — the coloured pips are
 * never reduced away. The Hellkite is itself an artifact, but it counts artifacts *you control*
 * while it is still a spell on the stack, so it never counts itself.
 */
val FurnaceHellkite = card("Furnace Hellkite") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Dragon"
    power = 5
    toughness = 5
    oracleText = "Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)\n" +
        "Flying\n" +
        "{R}: This creature gets +1/+0 until end of turn."

    keywordAbility(KeywordAbility.Affinity(CardType.ARTIFACT))
    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Fang Xinyu"
        flavorText = "Some mornings it wakes up determined to consume the red sun itself."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f9d91b5-7c09-4a9c-9dc8-fdd4c049009c.jpg?1783911271"
    }
}
