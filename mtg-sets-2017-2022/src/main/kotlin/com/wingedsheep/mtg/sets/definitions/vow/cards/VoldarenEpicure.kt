package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Voldaren Epicure
 * {R}
 * Creature — Vampire
 * 1/1
 *
 * When this creature enters, it deals 1 damage to each opponent. Create a Blood token.
 */
val VoldarenEpicure = card("Voldaren Epicure") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, it deals 1 damage to each opponent. Create a Blood " +
        "token. (It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.CreateBlood(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Martina Fačková"
        flavorText = "\"If you're not refining your tastes, what's the point of living forever?\""
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae154e64-f626-45fb-bd52-840c1c27b2d3.jpg?1782703063"
    }
}
