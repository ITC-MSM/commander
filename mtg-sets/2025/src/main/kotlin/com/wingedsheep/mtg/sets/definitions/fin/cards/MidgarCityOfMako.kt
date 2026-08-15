package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Midgar, City of Mako // Reactor Raid
 * Land — Town // Sorcery — Adventure
 *
 * Midgar, City of Mako:
 *   This land enters tapped.
 *   {T}: Add {B}.
 *
 * Reactor Raid — {2}{B}, Sorcery — Adventure:
 *   You may sacrifice an artifact or creature. If you do, draw two cards.
 *   (Then exile this card. You may play the land later from exile.)
 *
 * Town land // spell Adventure — see [IshgardTheHolySee]. The optional sacrifice is a
 * gather → choose-up-to-one → sacrifice pipeline wrapped in [Effects.IfYouDo]: choosing nothing
 * (the "you may" no) performs no sacrifice, so `SuccessCriterion.Auto` reports no success and the
 * draw doesn't happen (mirrors Highway Robbery's "sacrifice a land. If you do, draw two cards").
 */
val MidgarCityOfMako = card("Midgar, City of Mako") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Land — Town"
    oracleText = "This land enters tapped.\n{T}: Add {B}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    adventure("Reactor Raid") {
        manaCost = "{2}{B}"
        typeLine = "Sorcery — Adventure"
        oracleText = "You may sacrifice an artifact or creature. If you do, draw two cards. " +
            "(Then exile this card. You may play the land later from exile.)"
        spell {
            effect = Effects.IfYouDo(
                action = Effects.Pipeline {
                    val fodder = gather(GameObjectFilter.CreatureOrArtifact, player = Player.You)
                    val chosen = chooseUpTo(
                        1,
                        from = fodder,
                        useTargetingUI = true,
                        prompt = "Choose an artifact or creature to sacrifice"
                    )
                    sacrifice(chosen)
                },
                ifYouDo = Effects.DrawCards(2)
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "286"
        artist = "Anthony Devine"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a837256-6bb4-4a60-962d-d2793548d26c.jpg?1748706848"
    }
}
