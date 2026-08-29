package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Benevolent Ancestor
 * {2}{W}
 * Creature — Spirit
 * 0/4
 * Defender (This creature can't attack.)
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 *
 * "Prevent the next N damage ... this turn" is the damage *shield*
 * ([Effects.PreventNextDamage]), not the static replacement effect Fog Bank uses: a shield is
 * created on resolution, absorbs one damage event up to its amount, and expires at end of turn.
 */
val BenevolentAncestor = card("Benevolent Ancestor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Defender (This creature can't attack.)\n" +
        "{T}: Prevent the next 1 damage that would be dealt to any target this turn."
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Tap
        val t = target("any target", Targets.Any)
        effect = Effects.PreventNextDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Nick Percival"
        flavorText = "Although the door is flimsy and the lock pathetically small, Josuri's family never fears the night outside."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f23110b5-0dd4-49a2-8991-bc673aed53c9.jpg"
    }
}
