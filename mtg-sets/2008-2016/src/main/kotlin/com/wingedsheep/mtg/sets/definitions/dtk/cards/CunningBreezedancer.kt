package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cunning Breezedancer
 * {4}{W}{U}
 * Creature — Dragon
 * 4 / 4
 *
 * Flying
 * Whenever you cast a noncreature spell, this creature gets +2/+2 until end of turn.
 *
 * A prowess-shaped trigger that is deliberately *not* prowess — the printed line is its own
 * ability with a +2/+2 bonus, so it is a plain [Triggers.YouCastNoncreature] trigger. Writing
 * `prowess()` would lower to the keyword plus a second +1/+1 trigger and double-count the pump.
 * The pump is [EffectTarget.Self]; `Duration.EndOfTurn` is the facade default that spells
 * "until end of turn".
 */
val CunningBreezedancer = card("Cunning Breezedancer") {
    manaCost = "{4}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Todd Lockwood"
        flavorText = "\"That which is beautiful in form can also be deadly.\"\n—Ishai, Ojutai dragonspeaker"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdbfe3b0-3b1d-4b0c-9d98-07f1cecce4b7.jpg?1783938573"
    }
}
