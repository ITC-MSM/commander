package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player

val TwinningGlass = card("Twinning Glass") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}: You may cast a spell from your hand without paying its mana cost if it has the same name as a spell that was cast this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.Composite(
            GatherCardsEffect(CardSource.FromZone(Zone.HAND, Player.You), "hand"),
            SelectFromCollectionEffect(
                from = "hand",
                selection = SelectionMode.ChooseSpell,
                filter = GameObjectFilter.Nonland.sharesNameWithSpellCastThisTurn(),
                storeSelected = "spellToCast",
                showAllCards = true,
                prompt = "You may cast a spell with the same name as a spell cast this turn",
                selectedLabel = "Cast for free"
            ),
            Effects.CastFromCollectionWithoutPayingCost("spellToCast")
        )
        description = "{1}, {T}: Cast a spell with a name already cast this turn for free"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "264"
        artist = "Franz Vohwinkel"
        flavorText = "It takes two to craft a mirror: a practiced metalsmith to silver one side and her own hazy reflection to polish the other."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0138c42-77ea-45f0-b2ca-cda03f3f50d1.jpg?1783942850"
        ruling("2007-10-01", "Activating Twinning Glass's ability allows you to cast a single card from your hand as it resolves. It doesn't create a continuous effect, and it doesn't let you cast multiple cards for free.")
        ruling("2007-10-01", "It doesn't matter who cast the first spell.")
        ruling("2007-10-01", "You can't pay any alternative costs (such as those from evoke or morph) when casting the card. On the other hand, if the card has additional costs (such as those from kicker or buyback), you may pay those.")
    }
}
