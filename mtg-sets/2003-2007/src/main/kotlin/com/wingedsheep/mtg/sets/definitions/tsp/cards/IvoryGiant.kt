package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ivory Giant
 * {5}{W}{W}
 * Creature — Giant
 * 3 / 4
 * When this creature enters, tap all nonwhite creatures.
 * Suspend 5—{W} (Rather than cast this card from your hand, you may pay {W} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * "Tap all nonwhite creatures" is symmetric — no controller predicate — so it is
 * [Effects.ForEachInGroup] over `Creature.notColor(WHITE)` with [Effects.Tap] on
 * [EffectTarget.Self], which inside the loop resolves to the current iteration entity.
 */
val IvoryGiant = card("Ivory Giant") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, tap all nonwhite creatures.\n" +
        "Suspend 5—{W} (Rather than cast this card from your hand, you may pay {W} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.notColor(Color.WHITE)),
            effect = Effects.Tap(EffectTarget.Self)
        )
    }

    keywordAbility(KeywordAbility.suspend("{W}", 5))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72eb30d9-a826-4f29-ae2a-6873997674a7.jpg"
    }
}
