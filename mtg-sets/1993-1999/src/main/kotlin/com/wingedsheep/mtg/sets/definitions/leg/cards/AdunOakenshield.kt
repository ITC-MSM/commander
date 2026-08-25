package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Adun Oakenshield
 * {B}{R}{G}
 * Legendary Creature — Human Knight
 * 1/2
 *
 * {B}{R}{G}, {T}: Return target creature card from your graveyard to your hand.
 *
 * A **targeted** graveyard return takes plain [Effects.ReturnToHand], not the
 * `fromZone`-guarded self-return facade: the target requirement's own `zone = GRAVEYARD` is
 * re-checked on resolution under CR 608.2b, so a card exiled in response simply fizzles.
 */
val AdunOakenshield = card("Adun Oakenshield") {
    manaCost = "{B}{R}{G}"
    colorIdentity = "BGR"
    typeLine = "Legendary Creature — Human Knight"
    power = 1
    toughness = 2
    oracleText = "{B}{R}{G}, {T}: Return target creature card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{R}{G}"), Costs.Tap)
        val creatureCard = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard,
        )
        effect = Effects.ReturnToHand(creatureCard)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "216"
        artist = "Jeff A. Menges"
        flavorText = "\". . . And at his passing, the bodies of the world's great warriors shall rise from their " +
            "graves and follow him to battle.\" —*The Anvilonian Grimoire*"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60252226-a102-4d88-9b80-42d021b5184d.jpg?1783948042"
    }
}
