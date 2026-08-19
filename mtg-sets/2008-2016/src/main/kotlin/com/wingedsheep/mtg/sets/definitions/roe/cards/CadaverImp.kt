package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cadaver Imp
 * {1}{B}{B}
 * Creature — Imp
 * 1/1
 *
 * Flying
 * When this creature enters, you may return target creature card from your graveyard to your hand.
 */
val CadaverImp = card("Cadaver Imp") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    oracleText = "Flying\nWhen this creature enters, you may return target creature card from your graveyard to your hand."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val card = target("target creature card in your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.ReturnToHand(card)
        description = "When this creature enters, you may return target creature card from your " +
            "graveyard to your hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Dave Kendall"
        flavorText = "The mouth must be pried open before rigor mortis sets in. Otherwise the returning soul can find no ingress."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b2f83d5-9269-4297-b507-558c2bdec32b.jpg?1783941988"
    }
}
