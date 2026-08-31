package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Ribbons of Night — Ravnica: City of Guilds #101
 * {4}{B} · Sorcery
 *
 * Ribbons of Night deals 4 damage to target creature and you gain 4 life. If {U} was spent to cast
 * this spell, draw a card.
 *
 * One of Ravnica's hybrid-flavoured "if {X} was spent" riders: the spell is mono-black and always
 * does the damage and the lifegain, and the blue clause is a *payment* question, not a colour
 * requirement — casting it with a Dimir land or an any-colour source turns the extra card on.
 * `Conditions.ManaSpentToCastIncludes` reads the payment recorded on the spell itself, so the rider
 * is checked on resolution (CR 608.2) against what was actually paid, and a copy of the spell —
 * which was never cast and so had no mana spent for it — correctly misses the rider.
 */
val RibbonsOfNight = card("Ribbons of Night") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Ribbons of Night deals 4 damage to target creature and you gain 4 life. " +
        "If {U} was spent to cast this spell, draw a card."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.DealDamage(4, creature)
            .then(Effects.GainLife(4))
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredBlue = 1),
                    effect = Effects.DrawCards(1),
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Ron Spears"
        flavorText = "\"My favorite meal is angel's flesh, slain in agony and iced with black vinegar.\"\n" +
            "—Aszala of House Dimir"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7a2e910-5a61-4ca0-8d69-0a6d4ea12ed7.jpg?1783943663"
    }
}
