package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCreatureTypeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sarkhan, Dragon Ascendant — Tarkir: Dragonstorm #118
 * {1}{R} · Legendary Creature — Human Druid · 2/2 · Rare
 *
 * When Sarkhan enters, you may behold a Dragon. If you do, create a Treasure token.
 * (To behold a Dragon, choose a Dragon you control or reveal a Dragon card from your hand.)
 * Whenever a Dragon you control enters, put a +1/+1 counter on Sarkhan. Until end of turn,
 * Sarkhan becomes a Dragon in addition to its other types and gains flying.
 *
 * The ETB uses the resolution-time [Effects.Behold] effect ("you may behold a Dragon; if you do,
 * create a Treasure"). The second ability triggers on any Dragon you control entering
 * ([Triggers.entersBattlefield] filtered to `Dragon` creatures you control, binding ANY): it adds
 * a +1/+1 counter to Sarkhan, then until end of turn makes him a Dragon in addition to his other
 * types ([AddCreatureTypeEffect] with [Duration.EndOfTurn]) and grants flying.
 */
val SarkhanDragonAscendant = card("Sarkhan, Dragon Ascendant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Druid"
    power = 2
    toughness = 2
    oracleText = "When Sarkhan enters, you may behold a Dragon. If you do, create a Treasure token. " +
        "(To behold a Dragon, choose a Dragon you control or reveal a Dragon card from your hand.)\n" +
        "Whenever a Dragon you control enters, put a +1/+1 counter on Sarkhan. Until end of turn, " +
        "Sarkhan becomes a Dragon in addition to its other types and gains flying."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Behold(
            filter = GameObjectFilter.Any.withSubtype(Subtype.DRAGON),
            ifBeheld = Effects.CreateTreasure()
        )
        description = "When Sarkhan enters, you may behold a Dragon. If you do, create a Treasure token."
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().withSubtype(Subtype.DRAGON),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(listOf(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            AddCreatureTypeEffect("Dragon", EffectTarget.Self, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self, Duration.EndOfTurn)
        ))
        description = "Whenever a Dragon you control enters, put a +1/+1 counter on Sarkhan. " +
            "Until end of turn, Sarkhan becomes a Dragon in addition to its other types and gains flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "118"
        artist = "Billy Christian"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2200646-7b7c-489d-bbae-16b03e1d7fb2.jpg?1760616486"
    }
}
