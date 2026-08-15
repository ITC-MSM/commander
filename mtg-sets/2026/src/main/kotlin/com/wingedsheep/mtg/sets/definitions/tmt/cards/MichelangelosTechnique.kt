package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.sneak
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Michelangelo's Technique
 * {4}{G}
 * Sorcery
 *
 * Sneak {3}{G}
 * Look at the top eight cards of your library. Put up to two creature cards with
 * total mana value 6 or less from among them onto the battlefield and the rest on
 * the bottom of your library in a random order.
 */
val MichelangelosTechnique = card("Michelangelo's Technique") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Sneak {3}{G} (You may cast this spell for {3}{G} if you also return an unblocked attacker you control to hand during the declare blockers step.)\nLook at the top eight cards of your library. Put up to two creature cards with total mana value 6 or less from among them onto the battlefield and the rest on the bottom of your library in a random order."

    sneak("{3}{G}")

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = DynamicAmount.Fixed(8), player = Player.You),
                    storeAs = "looked"
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
                    filter = GameObjectFilter.Creature,
                    restrictions = listOf(SelectionRestriction.TotalManaValueAtMost(6)),
                    showAllCards = true,
                    storeSelected = "toBattlefield",
                    storeRemainder = "toBottom",
                    prompt = "Put up to two creature cards with total mana value 6 or less onto the battlefield",
                    selectedLabel = "Put onto the battlefield",
                    remainderLabel = "Put on the bottom of your library"
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, Player.You)
                ),
                MoveCollectionEffect(
                    from = "toBottom",
                    destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom),
                    order = CardOrder.Random
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a63c06a-7c59-4b72-b916-e5b6ad78c684.jpg?1769006772"
    }
}
