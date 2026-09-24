package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fblthp, Knows the Way — Reality Fracture #258
 * {X}{G}{G} · Legendary Creature — Homunculus Scout · * /2
 *
 * Domain — Fblthp's power is equal to the number of basic land types among lands you control.
 * When Fblthp enters, search your library for up to X basic land cards with different names,
 * reveal them, put them into your hand, then shuffle.
 *
 * Power is the Domain characteristic-defining value ([DynamicAmounts.domain]). The trigger's X is
 * the X paid for the spell ([DynamicAmount.XValue]) — zero when Fblthp entered without being cast.
 * The search is the Three Dreams pipeline: `ChooseUpTo(X)` under
 * [SelectionRestriction.OnePerCardName] ("with different names") → hand, revealed → shuffle.
 */
val FblthpKnowsTheWay = card("Fblthp, Knows the Way") {
    manaCost = "{X}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Homunculus Scout"
    oracleText = "Domain — Fblthp's power is equal to the number of basic land types among lands you control.\n" +
        "When Fblthp enters, search your library for up to X basic land cards with different names, " +
        "reveal them, put them into your hand, then shuffle."

    dynamicPower(DynamicAmounts.domain())
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.BasicLand),
                storeAs = "searchable",
                search = true
            ),
            SelectFromCollectionEffect(
                from = "searchable",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.XValue),
                storeSelected = "found",
                restrictions = listOf(SelectionRestriction.OnePerCardName),
                prompt = "Search for up to X basic land cards with different names"
            ),
            MoveCollectionEffect(
                from = "found",
                destination = CardDestination.ToZone(Zone.HAND),
                revealed = true
            ),
            ShuffleLibraryEffect(),
            EmitLibrarySearchedEventEffect
        )
        description = "When Fblthp enters, search your library for up to X basic land cards with " +
            "different names, reveal them, put them into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "Simon Dominic"
        flavorText = "Fblthp had always loved to travel."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28fbb55a-5c9d-45ee-bf42-a84b1048f5d2.jpg?1789127976"
        inBooster = false
    }
}
