package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Azula, Cunning Usurper
 * {2}{U}{B}{B}
 * Legendary Creature — Human Noble Rogue
 * 4/4
 *
 * Firebending 2 (Whenever this creature attacks, add {R}{R}. This mana lasts until end of combat.)
 * When Azula enters, target opponent exiles a nontoken creature they control, then they exile a
 * nonland card from their graveyard.
 * During your turn, you may cast cards exiled with Azula and you may cast them as though they had
 * flash. Mana of any type can be spent to cast those spells.
 *
 * Modeling notes:
 * - The ETB targets an opponent (`Targets.Opponent`); the opponent (`Chooser.TargetPlayer`) makes
 *   both exile choices for their own objects, mirroring Strategic Betrayal's linked-choice flow.
 *   Each chosen card is moved to exile with `linkToSource = true`, stamping Azula's
 *   `LinkedExileComponent` so the pile is "exiled with Azula" (CR 607.2 linked abilities). The two selections are
 *   guarded by [ConditionalOnCollectionEffect] so an opponent with no eligible nontoken creature /
 *   no nonland graveyard card simply skips that half.
 * - The cast grant reuses the from-exile may-play machinery: gather Azula's whole linked-exile pile
 *   and grant a [MayPlayExpiry.Permanent] play permission gated to [IsYourTurn], with
 *   `withAnyManaType = true` ("mana of any type") and the new `asThoughFlash = true` timing rider
 *   ("as though they had flash", CR 702.8) so a sorcery/creature card can be cast at instant speed
 *   on your turn. On opponents' turns the [IsYourTurn] gate closes the permission entirely.
 */
val AzulaCunningUsurper = card("Azula, Cunning Usurper") {
    manaCost = "{2}{U}{B}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Noble Rogue"
    power = 4
    toughness = 4
    oracleText = "Firebending 2 (Whenever this creature attacks, add {R}{R}. This mana lasts until end of combat.)\n" +
        "When Azula enters, target opponent exiles a nontoken creature they control, then they exile " +
        "a nonland card from their graveyard.\n" +
        "During your turn, you may cast cards exiled with Azula and you may cast them as though they " +
        "had flash. Mana of any type can be spent to cast those spells."

    firebending(2)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            listOf(
                // Opponent exiles a nontoken creature they control (their choice), linked to Azula.
                GatherCardsEffect(
                    source = CardSource.BattlefieldMatching(
                        filter = GameObjectFilter.Creature.nontoken(),
                        player = Player.ContextPlayer(0)
                    ),
                    storeAs = "azulaCreatures"
                ),
                ConditionalOnCollectionEffect(
                    collection = "azulaCreatures",
                    ifNotEmpty = Effects.Composite(
                        listOf(
                            SelectFromCollectionEffect(
                                from = "azulaCreatures",
                                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                                chooser = Chooser.TargetPlayer,
                                storeSelected = "azulaChosenCreature",
                                prompt = "Choose a nontoken creature to exile",
                                useTargetingUI = true
                            ),
                            MoveCollectionEffect(
                                from = "azulaChosenCreature",
                                destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0)),
                                linkToSource = true
                            )
                        )
                    )
                ),
                // Then they exile a nonland card from their graveyard (their choice), linked to Azula.
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.ContextPlayer(0),
                        filter = GameObjectFilter.Nonland
                    ),
                    storeAs = "azulaGraveyard"
                ),
                ConditionalOnCollectionEffect(
                    collection = "azulaGraveyard",
                    ifNotEmpty = Effects.Composite(
                        listOf(
                            SelectFromCollectionEffect(
                                from = "azulaGraveyard",
                                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                                chooser = Chooser.TargetPlayer,
                                storeSelected = "azulaChosenGraveyardCard",
                                prompt = "Choose a nonland card in your graveyard to exile"
                            ),
                            MoveCollectionEffect(
                                from = "azulaChosenGraveyardCard",
                                destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0)),
                                linkToSource = true
                            )
                        )
                    )
                ),
                // During your turn you may cast cards exiled with Azula, as though they had flash,
                // spending mana of any type. Gather the whole linked-exile pile and grant the play.
                GatherCardsEffect(
                    source = CardSource.FromLinkedExile(),
                    storeAs = "azulaExiled"
                ),
                GrantMayPlayFromExileEffect(
                    from = "azulaExiled",
                    expiry = MayPlayExpiry.Permanent,
                    condition = IsYourTurn,
                    withAnyManaType = true,
                    asThoughFlash = true
                )
            )
        )
        description = "When Azula enters, target opponent exiles a nontoken creature they control, " +
            "then they exile a nonland card from their graveyard."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "208"
        artist = "Evyn Fong"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/daf30e1c-436d-4f23-b1d2-570619a4b7f5.jpg?1764121470"
    }
}
