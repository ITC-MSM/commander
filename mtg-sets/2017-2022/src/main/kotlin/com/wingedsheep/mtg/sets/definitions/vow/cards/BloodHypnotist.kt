package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CantBlockEffect

/**
 * Blood Hypnotist
 * {2}{R}
 * Creature — Vampire
 * 3/3
 *
 * This creature can't block.
 * Whenever you sacrifice one or more Blood tokens, target creature can't block this turn.
 * This ability triggers only once each turn.
 *
 * "This creature can't block" is the static [CantBlock]. The batch trigger uses
 * [Triggers.YouSacrificeOneOrMore] filtered to Blood tokens (artifacts with subtype
 * "Blood") — it fires once per sacrifice event regardless of how many Blood were
 * sacrificed at once. The "only once each turn" clause is `oncePerTurn = true`; the
 * targeted [CantBlockEffect] defaults to Duration.EndOfTurn ("this turn").
 */
val BloodHypnotist = card("Blood Hypnotist") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 3
    oracleText = "This creature can't block.\n" +
        "Whenever you sacrifice one or more Blood tokens, target creature can't block this " +
        "turn. This ability triggers only once each turn."

    staticAbility {
        ability = CantBlock()
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Artifact.withSubtype("Blood"))
        oncePerTurn = true
        val creature = target("creature", Targets.Creature)
        effect = CantBlockEffect(target = creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Marie Magny"
        flavorText = "Arvon was ecstatic to offer his blood to such a captivating creature."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d85e0477-9176-4a3e-badc-f1c1c734e59c.jpg?1783924842"
    }
}
