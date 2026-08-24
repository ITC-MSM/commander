package com.wingedsheep.mtg.sets.definitions.drb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hellkite Overlord
 * {4}{B}{R}{R}{G}
 * Creature — Dragon
 * 8 / 8
 * Flying, trample, haste
 * {R}: This creature gets +1/+0 until end of turn.
 * {B}{G}: Regenerate this creature.
 *
 * Three evergreen keywords plus two bare-mana activated abilities, neither of which taps: the firebreathing
 * is [Effects.ModifyStats] on [EffectTarget.Self] with its default `Duration.EndOfTurn`, and the shield is
 * [RegenerateEffect] on the same target. From the Vault: Dragons is this card's earliest real printing,
 * hence the `drb` package; Shards of Alara carries it as a reprint row.
 */
val HellkiteOverlord = card("Hellkite Overlord") {
    manaCost = "{4}{B}{R}{R}{G}"
    colorIdentity = "BRG"
    typeLine = "Creature — Dragon"
    power = 8
    toughness = 8
    oracleText = "Flying, trample, haste\n" +
        "{R}: This creature gets +1/+0 until end of turn.\n" +
        "{B}{G}: Regenerate this creature."

    keywords(Keyword.FLYING, Keyword.TRAMPLE, Keyword.HASTE)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{B}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Justin Sweet"
        flavorText = "\"The dragon has no pretense of compassion, no false mask of civilization—just hunger, heat, and need.\"\n—Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e3c4f96-4a43-440d-b75f-2cdb7c24b92e.jpg"
    }
}
