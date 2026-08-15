package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shocking Sharpshooter — Tarkir: Dragonstorm #121
 * {1}{R}
 * Creature — Human Archer
 * 1/3
 *
 * Reach
 * Whenever another creature you control enters, this creature deals 1 damage to
 * target opponent.
 *
 * The damage is dealt by this creature itself (damageSource = Self).
 */
val ShockingSharpshooter = card("Shocking Sharpshooter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Archer"
    power = 1
    toughness = 3
    oracleText = "Reach\nWhenever another creature you control enters, this creature deals 1 damage " +
        "to target opponent."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        val t = target("target", Targets.Opponent)
        effect = Effects.DealDamage(1, t, damageSource = EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a10342d-ca04-4d1e-bca9-79f531951a16.jpg?1743204450"
    }
}
