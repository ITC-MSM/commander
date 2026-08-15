package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Lobelia Sackville-Baggins
 * {2}{B}
 * Legendary Creature — Halfling Citizen
 * 2/3
 * Flash · Menace
 *
 * When Lobelia enters, exile target creature card from an opponent's graveyard that was
 * put there from the battlefield this turn, then create X Treasure tokens, where X is
 * the exiled card's power.
 *
 * Uses `StatePredicate.PutIntoGraveyardFromBattlefieldThisTurn` (LTR Gap 20) restricted
 * to an opponent's graveyard. The Treasure count reads `Target(0).Power`, which evaluates
 * against the exiled card's base power after it lands in the exile zone. Although a card
 * that changes zones becomes a new object (CR 400.7), CR 400.7j lets the later part of the
 * same effect find the object it became in the public zone it moved to — so the "then
 * create X Treasures" half can still read the exiled card's power. Its base power is
 * unchanged from its graveyard reading.
 */
val LobeliaSackvilleBaggins = card("Lobelia Sackville-Baggins") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Halfling Citizen"
    power = 2
    toughness = 3
    oracleText = "Flash\n" +
        "Menace\n" +
        "When Lobelia enters, exile target creature card from an opponent's graveyard " +
        "that was put there from the battlefield this turn, then create X Treasure " +
        "tokens, where X is the exiled card's power."

    keywords(Keyword.FLASH, Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val exileTarget = target(
            "creature card in an opponent's graveyard that was put there from the battlefield this turn",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .ownedByOpponent()
                        .putIntoGraveyardFromBattlefieldThisTurn(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.Move(
            target = exileTarget,
            destination = Zone.EXILE,
            fromZone = Zone.GRAVEYARD
        ).then(
            CreatePredefinedTokenEffect(
                tokenType = "Treasure",
                dynamicCount = DynamicAmount.EntityProperty(
                    entity = EntityReference.Target(0),
                    numericProperty = EntityNumericProperty.Power
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "93"
        artist = "Hristo D. Chukov"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87500b92-3d68-42f3-afa9-f8206b2ebcbb.jpg?1686968553"
    }
}
