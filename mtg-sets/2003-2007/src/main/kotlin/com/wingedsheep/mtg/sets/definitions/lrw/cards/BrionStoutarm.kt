package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Brion Stoutarm
 * {2}{R}{W}
 * Legendary Creature — Giant Warrior
 * 4/4
 * Lifelink
 * {R}, {T}, Sacrifice another creature: Brion Stoutarm deals damage equal to the sacrificed
 * creature's power to target player or planeswalker.
 *
 * Bloodshot Cyclops' shape with two differences read straight off the text: the sacrifice is
 * "another creature" ([Costs.SacrificeAnother], which excludes Brion itself), and the target is a
 * player or planeswalker rather than any target. [DynamicAmounts.sacrificedPower] reads the
 * sacrificed creature's last-known power off the paid cost.
 */
val BrionStoutarm = card("Brion Stoutarm") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Giant Warrior"
    power = 4
    toughness = 4
    oracleText = "Lifelink\n" +
        "{R}, {T}, Sacrifice another creature: Brion Stoutarm deals damage equal to the sacrificed " +
        "creature's power to target player or planeswalker."

    keywords(Keyword.LIFELINK)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}"),
            Costs.Tap,
            Costs.SacrificeAnother(GameObjectFilter.Creature),
        )
        val victim = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(DynamicAmounts.sacrificedPower(), victim)
        description = "Brion Stoutarm deals damage equal to the sacrificed creature's power to target player or planeswalker."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa21cfc2-9671-4bf3-b922-2f436f284cb1.jpg?1783942854"
        ruling("2018-03-16", "If Brion Stoutarm leaves the battlefield after its ability has been activated but before it resolves, the game uses its last known information to determine that it had lifelink and you'll gain life for the damage it deals.")
    }
}
