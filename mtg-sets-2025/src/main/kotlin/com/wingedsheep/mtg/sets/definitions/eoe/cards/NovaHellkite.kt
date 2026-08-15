package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nova Hellkite
 * {3}{R}{R}
 * Creature — Dragon
 * Flying, haste
 * When this creature enters, it deals 1 damage to target creature an opponent controls.
 * Warp {2}{R} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 4/5
 */
val NovaHellkite = card("Nova Hellkite") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying, haste\nWhen this creature enters, it deals 1 damage to target creature an opponent controls.\n" +
        "Warp {2}{R} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 4
    toughness = 5

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val target = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(1, target)
    }

    warp = "{2}{R}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/424af0d0-398c-4d78-9ad5-2171bf1bcbd1.jpg?1752947152"
    }
}
