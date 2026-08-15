package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Summon: Fat Chocobo
 * {4}{G}
 * Enchantment Creature — Saga Bird
 * 4/4
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)
 * I — Wark — Create a 2/2 green Bird creature token with "Whenever a land you control enters,
 *   this token gets +1/+0 until end of turn."
 * II, III, IV — Kerplunk — Creatures you control gain trample until end of turn.
 *
 * Four-chapter Saga (sacrifice after IV; the engine derives the final chapter from the highest
 * declared chapter, matching Summon: G.F. Ifrit). Chapter I creates the Chocobo Bird token, which
 * carries its own land-enters trigger giving it +1/+0 until end of turn — the same token Call the
 * Mountain Chocobo makes. Chapters II–IV grant trample to creatures you control until end of turn
 * via [Patterns.Group.grantKeywordToAll].
 */
val SummonFatChocobo = card("Summon: Fat Chocobo") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Saga Bird"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)\n" +
        "I — Wark — Create a 2/2 green Bird creature token with \"Whenever a land you control enters, " +
        "this token gets +1/+0 until end of turn.\"\n" +
        "II, III, IV — Kerplunk — Creatures you control gain trample until end of turn."
    power = 4
    toughness = 4

    // I — Wark — create the 2/2 green Bird token with a land-enters self-pump trigger.
    val wark = CreateTokenEffect(
        power = 2,
        toughness = 2,
        colors = setOf(Color.GREEN),
        creatureTypes = setOf("Bird"),
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fbc471d-5948-47fc-b7cc-81cc13a4cd15.jpg?1748704082",
        triggeredAbilities = listOf(
            TriggeredAbility.create(
                trigger = Triggers.entersBattlefield(
                    filter = GameObjectFilter.Land.youControl(),
                    binding = TriggerBinding.ANY
                ).event,
                binding = Triggers.entersBattlefield(
                    filter = GameObjectFilter.Land.youControl(),
                    binding = TriggerBinding.ANY
                ).binding,
                effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
            )
        )
    )

    // II, III, IV — Kerplunk — creatures you control gain trample until end of turn.
    val kerplunk = Patterns.Group.grantKeywordToAll(Keyword.TRAMPLE, Filters.Group.creaturesYouControl)

    sagaChapter(1) { effect = wark }
    sagaChapter(2) { effect = kerplunk }
    sagaChapter(3) { effect = kerplunk }
    sagaChapter(4) { effect = kerplunk }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "202"
        artist = "Joseph Weston"
        flavorText = "A pleasingly plump summon."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32eb192b-de6b-4814-8077-628d343d014e.jpg?1748706518"
    }
}
