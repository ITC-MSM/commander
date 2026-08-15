package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantHexproofToController
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Crystal Barricade
 * {1}{W}
 * Artifact Creature — Wall
 * 0/4
 * Defender
 * You have hexproof.
 * Prevent all noncombat damage that would be dealt to other creatures you control.
 *
 * Three printed abilities, each mapped to its own primitive:
 *  - Defender — plain keyword.
 *  - "You have hexproof" — the [GrantHexproofToController] static (Shalai, Voice of Plenty's
 *    player leg); it lives and dies with the Barricade being on the battlefield, so no cleanup.
 *  - The prevention shield — a continuous [PreventDamage] replacement (`amount = null` = prevent
 *    all) restricted to `DamageType.NonCombat` and to a recipient filter of creatures you control
 *    *other than the Barricade itself* (`notSourceItself()`; the Barricade's own damage is not
 *    prevented, and players aren't creatures so your own life total isn't shielded either). The
 *    filter is re-evaluated per damage instance against projected state, so creatures that come
 *    under your control later are covered.
 */
val CrystalBarricade = card("Crystal Barricade") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 4
    oracleText = "Defender (This creature can't attack.)\n" +
        "You have hexproof. (You can't be the target of spells or abilities your opponents control.)\n" +
        "Prevent all noncombat damage that would be dealt to other creatures you control."

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = GrantHexproofToController
    }

    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Matching(
                    GameObjectFilter.Creature.youControl().notSourceItself(),
                ),
                damageType = DamageType.NonCombat,
            ),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Rockey Chen"
        flavorText = "\"Good as new!\"\n—Krek, daysquad captain"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/905d3e02-ea06-45e7-9adb-c8e7583323a2.jpg?1783909129"
    }
}
