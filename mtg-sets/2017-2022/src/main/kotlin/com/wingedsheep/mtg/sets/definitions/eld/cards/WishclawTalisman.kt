package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wishclaw Talisman
 * {1}{B}
 * Artifact
 * This artifact enters with three wish counters on it.
 * {1}, {T}, Remove a wish counter from this artifact: Search your library for a card, put it into
 *   your hand, then shuffle. An opponent gains control of this artifact. Activate only during your turn.
 *
 * Modelling notes:
 * - "Enters with three wish counters" is a replacement effect (CR 614.1c), not an ETB trigger, so it
 *   is an [EntersWithCounters] with `selfOnly = true`.
 * - The wish counters *are* the use limit: once they run out the Talisman just sits on the
 *   battlefield with an unactivatable ability (Scryfall ruling), which falls out of the cost being
 *   unpayable — no extra gate needed.
 * - "An opponent gains control" is not targeted; the controller picks while the ability resolves
 *   (Scryfall ruling), so [Effects.ChooseOpponent] records the pick on the source and
 *   [GiveControlToTargetPlayerEffect] reads it back through [Player.ChosenOpponent]. In a two-player
 *   game the pick is forced, so no extra decision is surfaced.
 */
val WishclawTalisman = card("Wishclaw Talisman") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "This artifact enters with three wish counters on it.\n" +
        "{1}, {T}, Remove a wish counter from this artifact: Search your library for a card, put it " +
        "into your hand, then shuffle. An opponent gains control of this artifact. Activate only " +
        "during your turn."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.WISH),
            count = 3,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.WISH, 1)
        )
        effect = Effects.Composite(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any,
                destination = SearchDestination.HAND
            ),
            Effects.ChooseOpponent("Choose an opponent to gain control of Wishclaw Talisman"),
            GiveControlToTargetPlayerEffect(
                permanent = EffectTarget.Self,
                newController = EffectTarget.PlayerRef(Player.ChosenOpponent)
            )
        )
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "110"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07c17b01-ee5d-491a-8403-b3f819b778c4.jpg?1783932629"
        ruling(
            "2019-10-04",
            "Once Wishclaw Talisman runs out of wish counters, it remains on the battlefield. " +
                "You can't activate its last ability at all."
        )
        ruling(
            "2019-10-04",
            "You choose which opponent gains control of Wishclaw Talisman while its ability is " +
                "resolving. If that player later leaves the game, you regain control of Wishclaw Talisman."
        )
    }
}
