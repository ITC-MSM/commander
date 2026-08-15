package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Price of Freedom — {1}{R} Sorcery — Lesson
 *
 * Destroy target artifact or land an opponent controls. Its controller may search
 * their library for a basic land card, put it onto the battlefield tapped, then
 * shuffle.
 * Draw a card.
 *
 * Same Path-to-Exile-style compensation shape as [com.wingedsheep.mtg.sets.definitions.fin.cards.Sandworm]:
 * the destroy resolves first, then the destroyed permanent's controller — not the caster —
 * gets the optional basic-land search, so the [MayEffect] gate and the search pipeline are
 * delegated to [EffectTarget.TargetController] / [Player.ControllerOf]. "Its controller"
 * resolves from the targeted permanent at resolution; since it has just left the battlefield,
 * it falls back to its owner (last-known controller for a permanent that left play). Finally
 * the caster draws a card.
 */
val PriceOfFreedom = card("Price of Freedom") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery — Lesson"
    oracleText = "Destroy target artifact or land an opponent controls. Its controller may " +
        "search their library for a basic land card, put it onto the battlefield tapped, then shuffle.\n" +
        "Draw a card."

    spell {
        target = TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactOrLand.opponentControls()))
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
            .then(
                MayEffect(
                    effect = Effects.Composite(
                        listOf(
                            GatherCardsEffect(
                                source = CardSource.FromZone(
                                    zone = Zone.LIBRARY,
                                    player = Player.ControllerOf("target"),
                                    filter = GameObjectFilter.BasicLand,
                                ),
                                storeAs = "searchable",
                            ),
                            SelectFromCollectionEffect(
                                from = "searchable",
                                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                                chooser = Chooser.ControllerOfTarget,
                                storeSelected = "found",
                            ),
                            MoveCollectionEffect(
                                from = "found",
                                destination = CardDestination.ToZone(
                                    zone = Zone.BATTLEFIELD,
                                    player = Player.ControllerOf("target"),
                                    placement = ZonePlacement.Tapped,
                                ),
                            ),
                            ShuffleLibraryEffect(target = EffectTarget.TargetController),
                        ),
                    ),
                    decisionMaker = EffectTarget.TargetController,
                ),
            )
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "149"
        artist = "Kotakan"
        flavorText = "\"We're going to win a great victory against the Fire Nation today.\"\n—Jet"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9fbe94e9-a71d-4a31-9210-c599abe08e3f.jpg?1764121025"
    }
}
