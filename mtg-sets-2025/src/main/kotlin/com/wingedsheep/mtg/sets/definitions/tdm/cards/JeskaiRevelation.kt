package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetSpellOrPermanent

/**
 * Jeskai Revelation — Tarkir: Dragonstorm #196
 * {4}{U}{R}{W} · Instant
 *
 * Return target spell or permanent to its owner's hand. Jeskai Revelation deals 4 damage
 * to any target. Create two 1/1 white Monk creature tokens with prowess. Draw two cards.
 * You gain 4 life.
 *
 * Two targets are chosen as the spell is cast: the bounce target (spell or permanent) and
 * the damage target (any target). The remaining clauses are non-targeted and always happen.
 */
val JeskaiRevelation = card("Jeskai Revelation") {
    manaCost = "{4}{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Instant"
    oracleText = "Return target spell or permanent to its owner's hand. Jeskai Revelation deals 4 " +
        "damage to any target. Create two 1/1 white Monk creature tokens with prowess. Draw two " +
        "cards. You gain 4 life."

    spell {
        val bounceTarget = target("target spell or permanent", TargetSpellOrPermanent())
        val damageTarget = target("any target", Targets.Any)
        effect = Effects.Composite(
            Effects.ReturnSpellOrPermanentToOwnersHand(bounceTarget),
            Effects.DealDamage(4, damageTarget),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Monk"),
                keywords = setOf(Keyword.PROWESS),
                count = 2,
                imageUri = "https://cards.scryfall.io/normal/front/6/3/633d2d10-def7-426f-8496-ed6b45684299.jpg?1742421122"
            ),
            Effects.DrawCards(2),
            Effects.GainLife(4),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "196"
        artist = "Igor Grechanyi"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cac0ad3-5107-4ed6-a688-d44bbd65e407.jpg?1743204770"
    }
}
