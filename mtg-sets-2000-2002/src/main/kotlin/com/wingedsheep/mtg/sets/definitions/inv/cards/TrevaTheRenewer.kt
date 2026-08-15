package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Treva, the Renewer
 * {3}{G}{W}{U}
 * Legendary Creature — Dragon
 * 6/6
 * Flying
 * Whenever Treva deals combat damage to a player, you may pay {2}{W}. If you do, choose a color,
 * then you gain 1 life for each permanent of that color.
 *
 * One of the five Coalition Dragons (cf. [RithTheAwakener]). "Each permanent of that color"
 * counts every permanent on the battlefield regardless of controller, so it's an
 * `AggregateBattlefield(Player.Each)` over the chosen-color filter — `withChosenColor()` reads the
 * color stored by [Effects.ChooseColorThen]. The color is chosen during resolution (2004-10-04
 * ruling), which the resolution-time `ChooseColorThen` already honors.
 */
val TrevaTheRenewer = card("Treva, the Renewer") {
    manaCost = "{3}{G}{W}{U}"
    colorIdentity = "GWU"
    typeLine = "Legendary Creature — Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying\nWhenever Treva deals combat damage to a player, you may pay {2}{W}. If you do, " +
        "choose a color, then you gain 1 life for each permanent of that color."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}{W}"),
            effect = Effects.ChooseColorThen(
                then = Effects.GainLife(
                    amount = DynamicAmount.AggregateBattlefield(
                        player = Player.Each,
                        filter = GameObjectFilter.Permanent.withChosenColor()
                    )
                ),
                prompt = "Choose a color"
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "280"
        artist = "Ciruelo"
        flavorText = ""
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ee67039-6cee-4a2d-b973-570f5060f550.jpg?1562910976"
    }
}
