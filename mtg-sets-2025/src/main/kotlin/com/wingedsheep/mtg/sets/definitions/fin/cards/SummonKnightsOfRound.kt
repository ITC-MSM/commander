package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Summon: Knights of Round
 * {6}{W}{W}
 * Enchantment Creature — Saga Knight
 * 3/3
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after V.)
 * I, II, III, IV — Create three 2/2 white Knight creature tokens.
 * V — Ultimate End — Other creatures you control get +2/+2 until end of turn. Put an
 *     indestructible counter on each of them.
 * Indestructible
 *
 * A five-chapter Summon Saga whose own body is Indestructible — the creature keyword is
 * independent of its chapter symbols (CR 714.1a).
 */
val SummonKnightsOfRound = card("Summon: Knights of Round") {
    manaCost = "{6}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Saga Knight"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after V.)\n" +
        "I, II, III, IV — Create three 2/2 white Knight creature tokens.\n" +
        "V — Ultimate End — Other creatures you control get +2/+2 until end of turn. Put an " +
        "indestructible counter on each of them.\n" +
        "Indestructible"
    power = 3
    toughness = 3

    keywords(Keyword.INDESTRUCTIBLE)

    val makeKnights = Effects.CreateToken(
        power = 2,
        toughness = 2,
        colors = setOf(Color.WHITE),
        creatureTypes = setOf("Knight"),
        count = 3,
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7758a0b-9e85-4b4a-bf1c-ffcc6761dbad.jpg?1748704059",
    )

    sagaChapter(1) { effect = makeKnights }
    sagaChapter(2) { effect = makeKnights }
    sagaChapter(3) { effect = makeKnights }
    sagaChapter(4) { effect = makeKnights }
    sagaChapter(5) {
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
            effect = Effects.Composite(
                Effects.ModifyStats(2, 2, EffectTarget.Self),
                Effects.AddCounters(Counters.INDESTRUCTIBLE, 1, EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "36"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44d23652-077e-4c1f-b640-b284685db911.jpg?1768375967"
    }
}
