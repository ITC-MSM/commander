package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Beastbond Outcaster
 * {2}{G}
 * Creature — Human Druid
 * 3/3
 *
 * When this creature enters, if you control a creature with power 4 or greater, draw a card.
 * Plot {1}{G} (You may pay {1}{G} and exile this card from your hand. Cast it as a sorcery on a
 * later turn without paying its mana cost. Plot only as a sorcery.)
 *
 * Intervening-if ETB (CR 603.4, ruling 2024-04-12): the draw trigger checks "you control a
 * creature with power 4 or greater" both when it would fire and as it resolves; if it's false at
 * either point, no card is drawn. Power is read through projected state, so counters and lords
 * count. Plot is the named keyword ([KeywordAbility.plot]).
 */
val BeastbondOutcaster = card("Beastbond Outcaster") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, if you control a creature with power 4 or greater, " +
        "draw a card.\nPlot {1}{G} (You may pay {1}{G} and exile this card from your hand. Cast it " +
        "as a sorcery on a later turn without paying its mana cost. Plot only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = Effects.DrawCards(1)
        description = "When this creature enters, if you control a creature with power 4 or greater, draw a card."
    }

    keywordAbility(KeywordAbility.plot("{1}{G}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "154"
        artist = "Viko Menezes"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/073b9ae8-8ac3-4824-aec4-84a80531aa23.jpg?1712355883"

        ruling("2024-04-12", "When Beastbond Outcaster enters the battlefield, its triggered ability will check to see if you control a creature with power 4 or greater. If you don't, the ability won't trigger at all. If the ability does trigger, it will check again as it tries to resolve. If you no longer control a creature with power 4 or greater, the ability won't do anything.")
    }
}
