package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Deadly Plot
 * {3}{B}
 * Instant
 * Choose one —
 * • Destroy target creature or planeswalker.
 * • Return target Zombie creature card from your graveyard to the battlefield tapped.
 *
 * A plain choose-one modal spell: each mode carries its own target, so only the chosen mode's
 * target is announced at cast time (CR 601.2b). The reanimation mode is scoped to Zombie
 * *creature* cards in your own graveyard and puts the card onto the battlefield tapped.
 */
val DeadlyPlot = card("Deadly Plot") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target creature or planeswalker.\n" +
        "• Return target Zombie creature card from your graveyard to the battlefield tapped."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target creature or planeswalker") {
                val t = target("creature or planeswalker", Targets.CreatureOrPlaneswalker)
                effect = Effects.Destroy(t)
            }
            mode("Return target Zombie creature card from your graveyard to the battlefield tapped") {
                val t = target(
                    "Zombie creature card in your graveyard",
                    TargetObject(
                        filter = TargetFilter(
                            GameObjectFilter(
                                cardPredicates = listOf(
                                    CardPredicate.IsCreature,
                                    CardPredicate.HasSubtype(Subtype.ZOMBIE),
                                ),
                                controllerPredicate = ControllerPredicate.OwnedByYou,
                            ),
                            zone = Zone.GRAVEYARD,
                        ),
                    ),
                )
                effect = Effects.PutOntoBattlefield(t, tapped = true)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Peter Polach"
        flavorText = "\"Open grave available, only one previous tenant.\"\n—Sign on cemetery gate"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c78da23-e7ec-4a3d-9f79-09fb86993b26.jpg?1783919187"
    }
}
