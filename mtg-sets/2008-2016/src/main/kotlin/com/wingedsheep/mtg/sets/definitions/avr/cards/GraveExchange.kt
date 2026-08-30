package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Grave Exchange
 * {4}{B}{B}
 * Sorcery
 *
 * Return target creature card from your graveyard to your hand. Target player sacrifices a creature of their choice.
 *
 * Two targets chosen as the spell goes on the stack, in printed order: the graveyard card first,
 * the sacrificing player second. The return needs no `fromZone` guard — the target requirement's
 * own `zone = GRAVEYARD` is re-checked at resolution under CR 608.2b.
 */
val GraveExchange = card("Grave Exchange") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to your hand. Target player sacrifices a " +
        "creature of their choice."

    spell {
        val creatureCard = target("target", Targets.CreatureCardInYourGraveyard)
        val victim = target("target 1", Targets.Player)
        effect = Effects.Composite(
            Effects.ReturnToHand(creatureCard),
            Effects.Sacrifice(GameObjectFilter.Creature, target = victim),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Sam Wolfe Connelly"
        flavorText = "\"It's a cold, dark journey either way.\"\n—Eruth of Lambholt"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14f420c4-801b-48e7-a10b-de44a2417265.jpg?1783940698"
    }
}
