package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bloodstone Goblin
 * {1}{R}
 * Creature — Goblin Warrior
 * 2/2
 * Whenever you cast a spell, if that spell was kicked,
 * Bloodstone Goblin gets +1/+1 and gains menace until end of turn.
 */
val BloodstoneGoblin = card("Bloodstone Goblin") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast a spell, if that spell was kicked, Bloodstone Goblin gets +1/+1 and gains menace until end of turn."

    triggeredAbility {
        trigger = Triggers.youCastSpell(requires = setOf(SpellCastPredicate.WasKicked))
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Magali Villeneuve"
        flavorText = "The stone whispers tales of darkness and fire. She replies, \"Tell me more.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31adbfd4-56aa-4137-aeba-8720233260be.jpg?1562733644"
    }
}
