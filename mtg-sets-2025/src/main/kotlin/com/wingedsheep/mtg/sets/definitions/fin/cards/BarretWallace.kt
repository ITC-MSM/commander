package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Barret Wallace
 * {3}{R}
 * Legendary Creature — Human Rebel
 * 4/4
 *
 * Reach
 * Whenever Barret Wallace attacks, it deals damage equal to the number of equipped creatures
 * you control to defending player.
 *
 * The attack trigger deals damage from Barret (the default source for a triggered ability) to the
 * defending player; the amount is the count of equipped creatures you control
 * ([DynamicAmounts.equippedCreaturesYouControl] — creatures with at least one Equipment attached,
 * each counted once, CR 301.5). Evaluated at resolution, so it includes Barret itself if equipped.
 */
val BarretWallace = card("Barret Wallace") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Rebel"
    power = 4
    toughness = 4
    oracleText = "Reach\nWhenever Barret Wallace attacks, it deals damage equal to the number of " +
        "equipped creatures you control to defending player."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.DealDamage(
            DynamicAmounts.equippedCreaturesYouControl(),
            EffectTarget.PlayerRef(Player.DefendingPlayer)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Patrik Hell"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a504dff-5857-4a61-ab99-616d5df7cf5a.jpg?1748706246"
    }
}
