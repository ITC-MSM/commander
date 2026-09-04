package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stitchwing Skaab (Shadows over Innistrad #90)
 * {3}{U}
 * Creature — Zombie Horror
 * 3 / 1
 *
 * Flying
 * {1}{U}, Discard two cards: Return this card from your graveyard to the battlefield tapped.
 */
val StitchwingSkaab = card("Stitchwing Skaab") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Horror"
    power = 3
    toughness = 1
    oracleText = "Flying\n" +
        "{1}{U}, Discard two cards: Return this card from your graveyard to the battlefield tapped."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Discard(count = 2))
        effect = Effects.PutOntoBattlefieldFromGraveyard(EffectTarget.Self, tapped = true)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Nils Hamm"
        flavorText = "\"Amazing, isn't it, how scraps can come together to create such a wonder?\"\n—Stitcher Geralf"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f84bb1ce-a8a0-4a29-9129-b1d7041fd01a.jpg?1783937783"
    }
}
