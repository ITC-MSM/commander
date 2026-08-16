package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cephalopod Sentry
 * {2}{W}{U}
 * Artifact Creature — Phyrexian Squid
 * * / 5
 *
 * Flying
 * Cephalopod Sentry's power is equal to the number of artifacts you control.
 *
 * The printed `*` power is a characteristic-defining ability: [dynamicPower] over
 * [DynamicAmount.AggregateBattlefield] counting your artifacts, with no `power =` set. The
 * Sentry counts itself — it is an artifact — so it is never smaller than 1/5 on the battlefield.
 */
val CephalopodSentry = card("Cephalopod Sentry") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Artifact Creature — Phyrexian Squid"
    toughness = 5
    oracleText = "Flying\n" +
        "Cephalopod Sentry's power is equal to the number of artifacts you control."

    keywords(Keyword.FLYING)
    dynamicPower(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Artifact))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Mathias Kollros"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30c22eb9-5056-4c0b-a5d8-41e09161eb40.jpg?1783918004"
    }
}
