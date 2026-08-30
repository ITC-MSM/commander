package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kolaghan's Command
 * {1}{B}{R}
 * Instant
 *
 * Choose two —
 * • Return target creature card from your graveyard to your hand.
 * • Target player discards a card.
 * • Destroy target artifact.
 * • Kolaghan's Command deals 2 damage to any target.
 *
 * "Choose two —" over four modes is `modal(chooseCount = 2)` — two *different* modes, since the
 * card carries no "you may choose the same mode more than once" rider.
 *
 * Three of the four modes target, and each targets something different, so the targets are declared
 * *inside* their modes (the block form of `mode`): only the chosen modes' requirements are asked
 * for, which is what CR 601.2c means by choosing targets after choosing modes.
 *
 * The graveyard return writes the plain [Effects.ReturnToHand] with no `fromZone` guard: the
 * requirement's own `zone = GRAVEYARD` is re-checked at resolution under CR 608.2b, so a card
 * exiled in response is already an illegal target.
 */
val KolaghansCommand = card("Kolaghan's Command") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Choose two —\n" +
        "• Return target creature card from your graveyard to your hand.\n" +
        "• Target player discards a card.\n" +
        "• Destroy target artifact.\n" +
        "• Kolaghan's Command deals 2 damage to any target."

    spell {
        modal(chooseCount = 2) {
            mode("Return target creature card from your graveyard to your hand") {
                val creatureCard = target("creature card in your graveyard", Targets.CreatureCardInYourGraveyard)
                effect = Effects.ReturnToHand(creatureCard)
            }
            mode("Target player discards a card") {
                val player = target("target player", Targets.Player)
                effect = Effects.Discard(1, player)
            }
            mode("Destroy target artifact") {
                val artifact = target("target artifact", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
            mode("Kolaghan's Command deals 2 damage to any target") {
                val victim = target("any target", Targets.Any)
                effect = Effects.DealDamage(2, victim)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c884e1e-fecb-4330-b3de-5fc2a60f7173.jpg?1783938571"
    }
}
