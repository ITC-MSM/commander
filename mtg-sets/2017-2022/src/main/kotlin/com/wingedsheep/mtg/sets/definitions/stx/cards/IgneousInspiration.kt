package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Igneous Inspiration — Strixhaven: School of Mages #107 (canonical printing)
 * {2}{R} · Sorcery
 *
 * Igneous Inspiration deals 3 damage to any target.
 * Learn.
 *
 * "Any target" is creature, player, or planeswalker/battle ([Targets.Any]). The damage names the
 * spell as its source, so `damageSource = EffectTarget.Self` — that is what lets lifelink,
 * "whenever a source you control deals damage" payoffs, and damage-redirection read the right
 * source.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val IgneousInspiration = card("Igneous Inspiration") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Igneous Inspiration deals 3 damage to any target.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, anyTarget, damageSource = EffectTarget.Self) then
            Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "PINDURSKI"
        flavorText = "Prismari fosters a burning need to create."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/5781ad7b-dc1b-4cc1-9e72-6e714b9ba1de.jpg?1783927354"
    }
}
