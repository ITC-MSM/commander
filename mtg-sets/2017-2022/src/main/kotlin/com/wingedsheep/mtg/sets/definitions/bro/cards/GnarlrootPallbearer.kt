package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarlroot Pallbearer
 * {4}{G}{G}
 * Creature — Treefolk Druid
 * 5/5
 * Trample
 * When this creature enters, target creature gets +X/+X until end of turn, where X is the number of creature cards in your graveyard.
 *
 * The Ghoultree count in a pump slot: [DynamicAmounts.creatureCardsInYourGraveyard] fills both
 * modifiers of [Effects.ModifyStats], which is until end of turn by default.
 */
val GnarlrootPallbearer = card("Gnarlroot Pallbearer") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Druid"
    power = 5
    toughness = 5
    oracleText = "Trample\n" +
        "When this creature enters, target creature gets +X/+X until end of turn, where X is the " +
        "number of creature cards in your graveyard."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(
            DynamicAmounts.creatureCardsInYourGraveyard(),
            DynamicAmounts.creatureCardsInYourGraveyard(),
            creature,
        )
        description = "When this creature enters, target creature gets +X/+X until end of turn, " +
            "where X is the number of creature cards in your graveyard."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Nino Is"
        flavorText = "The flames of war consumed most of Argoth's treefolk. Vengeance consumed the rest."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7382154-ac8f-40ca-94e4-2f0533c0cf20.jpg?1783920043"
    }
}
