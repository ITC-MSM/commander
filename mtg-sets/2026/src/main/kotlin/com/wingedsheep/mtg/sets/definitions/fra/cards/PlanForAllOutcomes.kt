package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Plan for All Outcomes
 * {3}{U}
 * Enchantment
 *
 * When this enchantment enters, the owner of up to one other target nonland permanent puts it on
 * their choice of the top or bottom of their library.
 * Whenever you cast your first noncreature spell each turn, empower Jace 1.
 *
 * [Effects.PutOnTopOrBottomOfLibrary] asks the target's **owner** for the position (Diver Skaab).
 * The second ability is [Triggers.NthSpellCast] with a noncreature filter, which counts casts —
 * a countered first noncreature spell still closes the window for the turn.
 */
val PlanForAllOutcomes = card("Plan for All Outcomes") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, the owner of up to one other target nonland permanent " +
        "puts it on their choice of the top or bottom of their library.\n" +
        "Whenever you cast your first noncreature spell each turn, empower Jace 1. (Put a loyalty " +
        "counter on a Jace token you control. If you don't control one, first create a blue Jace " +
        "planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "up to one other target nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent.other())
        )
        effect = Effects.PutOnTopOrBottomOfLibrary(permanent)
    }

    triggeredAbility {
        trigger = Triggers.NthSpellCast(1, Player.You, GameObjectFilter.Noncreature)
        effect = Patterns.Mechanic.empowerJace(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Chris Cold"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4effc17-0d0e-423a-b5f2-597ea6c71f67.jpg?1789576786"
        inBooster = false
    }
}
