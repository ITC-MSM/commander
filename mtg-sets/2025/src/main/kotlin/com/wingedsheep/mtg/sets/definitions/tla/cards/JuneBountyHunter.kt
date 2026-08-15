package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * June, Bounty Hunter
 * {1}{B}
 * Legendary Creature — Human Mercenary
 * 2/2
 *
 * June can't be blocked as long as you've drawn two or more cards this turn.
 * {1}, Sacrifice another creature: Create a Clue token. Activate only during your turn.
 * (It's an artifact with "{2}, Sacrifice this token: Draw a card.")
 *
 * The conditional evasion is a [ConditionalStaticAbility] wrapping a source-scoped
 * [CantBeBlocked] gated on [Conditions.YouDrewCardsThisTurn] (threshold 2) — the
 * cards-drawn-this-turn tracker is read each time the projected state is computed, so June
 * becomes unblockable the moment you've drawn your second card and reverts at end of turn.
 *
 * The activated ability pays {1} + [Costs.SacrificeAnother] (another creature) to
 * [Effects.CreateClue], restricted to your turn via [ActivationRestriction.OnlyDuringYourTurn].
 */
val JuneBountyHunter = card("June, Bounty Hunter") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Mercenary"
    power = 2
    toughness = 2
    oracleText = "June can't be blocked as long as you've drawn two or more cards this turn.\n" +
        "{1}, Sacrifice another creature: Create a Clue token. Activate only during your turn. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBeBlocked(),
            condition = Conditions.YouDrewCardsThisTurn(2),
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature),
        )
        effect = Effects.CreateClue()
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        description = "{1}, Sacrifice another creature: Create a Clue token. Activate only during your turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Shiren"
        flavorText = "\"My shirshu can smell a rat a continent away.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/4/148bbaab-bc7b-46ab-8e18-ade69d71d847.jpg?1764120735"
    }
}
