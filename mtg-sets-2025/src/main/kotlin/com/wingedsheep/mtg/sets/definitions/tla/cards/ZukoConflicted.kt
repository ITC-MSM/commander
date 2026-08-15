package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zuko, Conflicted
 * {B}{R}
 * Legendary Creature — Human Rogue
 * 2/3
 *
 * At the beginning of your first main phase, choose one that hasn't been chosen and you lose 2 life —
 * • Draw a card.
 * • Put a +1/+1 counter on Zuko.
 * • Add {R}.
 * • Exile Zuko, then return him to the battlefield under an opponent's control.
 *
 * "Choose one that hasn't been chosen" is modeled with [ModalEffect.chooseOneNotYetChosen]: the engine
 * remembers which modes this Zuko has already chosen (in a per-source memory component) and never offers
 * them again. Once all four have been chosen, the ability has no legal mode and does nothing — and the
 * "you lose 2 life" rider is bundled into each mode, so no life is lost when nothing can be chosen. The
 * memory is keyed to the object; the fourth mode (return Zuko under an opponent's control) makes him a
 * new object, resetting the memory for his new controller.
 */
val ZukoConflicted = card("Zuko, Conflicted") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Rogue"
    power = 2
    toughness = 3
    oracleText = "At the beginning of your first main phase, choose one that hasn't been chosen and you lose 2 life —\n" +
        "• Draw a card.\n" +
        "• Put a +1/+1 counter on Zuko.\n" +
        "• Add {R}.\n" +
        "• Exile Zuko, then return him to the battlefield under an opponent's control."

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = ModalEffect.chooseOneNotYetChosen(
            // • Draw a card.
            Mode.noTarget(
                Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You))
                    .then(Effects.DrawCards(1)),
                "Draw a card"
            ),
            // • Put a +1/+1 counter on Zuko.
            Mode.noTarget(
                Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You))
                    .then(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)),
                "Put a +1/+1 counter on Zuko"
            ),
            // • Add {R}.
            Mode.noTarget(
                Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You))
                    .then(Effects.AddMana(Color.RED)),
                "Add {R}"
            ),
            // • Exile Zuko, then return him to the battlefield under an opponent's control.
            Mode.noTarget(
                Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You))
                    .then(Effects.Move(EffectTarget.Self, Zone.EXILE))
                    .then(
                        Effects.Move(
                            target = EffectTarget.Self,
                            destination = Zone.BATTLEFIELD,
                            controllerOverride = EffectTarget.PlayerRef(Player.AnOpponent)
                        )
                    ),
                "Exile Zuko, then return him to the battlefield under an opponent's control"
            )
        )
        description = "At the beginning of your first main phase, choose one that hasn't been chosen and you lose 2 life —"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Halil Ural"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d555cab-ec86-4f27-bca2-7d01f79c3f46.jpg?1764121869"
    }
}
