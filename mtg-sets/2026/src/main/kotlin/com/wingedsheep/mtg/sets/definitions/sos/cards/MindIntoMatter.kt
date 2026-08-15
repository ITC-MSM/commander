package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mind into Matter — Secrets of Strixhaven #202
 * {X}{G}{U} · Sorcery
 *
 * Draw X cards. Then you may put a permanent card with mana value X or less from your hand onto the
 * battlefield tapped.
 *
 * X flows through the resolution context (`DynamicAmount.XValue` for the draw; the chosen X is
 * stamped on the context, exactly as for Mind Spring / Nature's Rhythm). The optional put is the
 * stock `Patterns.Hand.putFromHand` pipeline (Gather hand → optional `ChooseUpTo(1)` Select → Move
 * to battlefield tapped); the candidate filter is `Permanent.manaValueAtMostX()`, which reads the
 * same chosen X at selection time. Drawing first matters: a card just drawn this turn is a legal
 * choice to put onto the battlefield.
 */
val MindIntoMatter = card("Mind into Matter") {
    manaCost = "{X}{G}{U}"
    colorIdentity = "UG"
    typeLine = "Sorcery"
    oracleText = "Draw X cards. Then you may put a permanent card with mana value X or less from " +
        "your hand onto the battlefield tapped."

    spell {
        effect = Effects.DrawCards(DynamicAmount.XValue) then Patterns.Hand.putFromHand(
            filter = GameObjectFilter.Permanent.manaValueAtMostX(),
            count = 1,
            entersTapped = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "202"
        artist = "Joe Slucher"
        flavorText = "\"What the mind conceives,\" began Dean Adrix. \"Magic can achieve,\" assured Dean Nev."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a7f0fdf-1d4b-4458-a19c-274611e8a59a.jpg?1775938403"
    }
}
