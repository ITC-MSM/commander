package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Mistbind Clique
 * {3}{U}
 * Creature — Faerie Wizard
 * 4/4
 *
 * Flash
 * Flying
 * Champion a Faerie
 * When a Faerie is championed with this creature, tap all lands target player controls.
 *
 * The follow-up is a real triggered ability keyed to the CR 702.72c "championed" event
 * ([Triggers.championedWith]), not a rider on the champion trigger itself. That is what makes the
 * printed line behave: it fires only when a Faerie was *actually* exiled (declining the champion
 * choice sacrifices the Clique and taps nothing), it is a separate object on the stack that can be
 * responded to, and its player target is chosen when it goes on the stack rather than when the
 * Clique enters. The "a Faerie" quality needs no restating — the champion clause above can only
 * ever exile a Faerie permanent.
 *
 * "Tap all lands target player controls" is a group tap: gather the target player's lands, then
 * [TapUntapCollectionEffect]. The lands are not targeted — only the player is.
 */
val MistbindClique = card("Mistbind Clique") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 4
    toughness = 4
    oracleText = "Flash\n" +
        "Flying\n" +
        "Champion a Faerie (When this enters, sacrifice it unless you exile another Faerie you " +
        "control. When this leaves the battlefield, that card returns to the battlefield.)\n" +
        "When a Faerie is championed with this creature, tap all lands target player controls."

    keywords(Keyword.FLASH, Keyword.FLYING)
    champion(Subtype.FAERIE)

    triggeredAbility {
        trigger = Triggers.championedWith()
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.BattlefieldMatching(
                        filter = GameObjectFilter.Land.targetPlayerControls(player),
                        player = Player.Each
                    ),
                    storeAs = "mistbindClique_lands"
                ),
                TapUntapCollectionEffect("mistbindClique_lands", tap = true)
            )
        )
        description = "When a Faerie is championed with this creature, tap all lands target " +
            "player controls."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Ben Thompson"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfc421e2-0dd4-4bdf-b5f5-a60c4b0df63b.jpg?1783942900"
    }
}
