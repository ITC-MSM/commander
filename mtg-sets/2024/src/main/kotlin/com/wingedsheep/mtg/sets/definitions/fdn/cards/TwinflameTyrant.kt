package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DoubleDamage
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Twinflame Tyrant
 * {3}{R}{R}
 * Creature — Dragon
 * 3/5
 *
 * Flying
 * If a source you control would deal damage to an opponent or a permanent an opponent
 * controls, it deals double that damage instead.
 *
 * The doubling is a [DoubleDamage] damage-amount replacement (CR 616), scoped by an
 * [EventPattern.DamageEvent] whose source is "any source you control" and whose recipient is
 * [RecipientFilter.OpponentOrPermanentTheyControl] ("an opponent or a permanent an opponent
 * controls" — the same recipient shape used by Fated Firepower). Combat and noncombat damage
 * alike double, matching the unrestricted "deal damage" wording.
 */
val TwinflameTyrant = card("Twinflame Tyrant") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 3
    toughness = 5
    oracleText = "Flying\n" +
        "If a source you control would deal damage to an opponent or a permanent an opponent " +
        "controls, it deals double that damage instead."

    keywords(Keyword.FLYING)

    replacementEffect(
        DoubleDamage(
            appliesTo = EventPattern.DamageEvent(
                source = SourceFilter.Matching(GameObjectFilter.Any.youControl()),
                recipient = RecipientFilter.OpponentOrPermanentTheyControl,
            )
        )
    )

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "97"
        artist = "Xabi Gaztelua"
        flavorText = "\"Job only paid enough for one head. I'm out.\"\n—Peren, veteran mercenary"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb34f51-0bd2-43c3-af95-2ce8dabcc7bb.jpg?1782689182"
    }
}
