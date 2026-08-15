package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Pyrotechnic Performer — Murders at Karlov Manor #140
 * {1}{R} · Creature — Lizard Assassin · 3/2 · Rare
 *
 * Disguise {R}
 * Whenever this creature or another creature you control is turned face up, that creature deals
 * damage equal to its power to each opponent.
 *
 * The red disguise payoff: a two-mana 3/2 that turns every subsequent flip in the deck — including
 * its own — into a reach spell. Flipping the Performer itself for {R} deals 3 to each opponent
 * immediately, and every later disguise/cloak flip repeats the trick.
 *
 * Three details the modelling has to get right:
 *
 * - **"this creature or another creature you control"** has no "another" clause, so the ability
 *   must fire for the Performer's own flip too. That is [Triggers.CreatureTurnedFaceUp] with
 *   `Player.You` — an ANY-bound trigger over the controller's creatures — not [Triggers.TurnedFaceUp]
 *   (SELF-only) nor an OTHER binding. The same reasoning [PerimeterEnforcer] documents.
 * - **The damage source is the flipped creature, not the Performer** ("*that creature* deals
 *   damage"), so `damageSource` is [EffectTarget.TriggeringEntity]. This is what makes the flipped
 *   creature's own lifelink/deathtouch and any "whenever a creature you control deals damage"
 *   payoffs see the damage correctly, and it is why killing the Performer in response doesn't stop
 *   the damage being dealt by the other creature.
 * - **The amount is read at resolution, with last-known information.** Per the Scryfall ruling
 *   below, a flipped creature that has left the battlefield before the trigger resolves still deals
 *   damage equal to its power as it last existed there — which is exactly the semantics of
 *   [DynamicAmount.EntityProperty] over [EntityReference.Triggering], so no special casing is needed.
 *
 * Turning a permanent face up is a special action that doesn't use the stack (CR 701.34a); the
 * trigger goes on the stack afterwards and can be responded to, but the flip itself cannot.
 */
val PyrotechnicPerformer = card("Pyrotechnic Performer") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Assassin"
    power = 3
    toughness = 2
    oracleText = "Disguise {R} (You may cast this card face down for {3} as a 2/2 creature with " +
        "ward {2}. Turn it face up any time for its disguise cost.)\n" +
        "Whenever this creature or another creature you control is turned face up, that creature " +
        "deals damage equal to its power to each opponent."

    disguise = "{R}"

    triggeredAbility {
        trigger = Triggers.CreatureTurnedFaceUp(player = Player.You)
        effect = Effects.DealDamage(
            amount = DynamicAmount.EntityProperty(
                EntityReference.Triggering,
                EntityNumericProperty.Power
            ),
            target = EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.TriggeringEntity
        )
        description = "Whenever this creature or another creature you control is turned face up, " +
            "that creature deals damage equal to its power to each opponent."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Peter Polach"
        flavorText = "\"Let's get this party started.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fa5671b-2651-4944-a50a-c768ec70229e.jpg?1783912874"

        ruling(
            "2024-02-02",
            "If a creature that is turned face up leaves the battlefield before Pyrotechnic " +
                "Performer's triggered ability resolves, use that creature's power as it last " +
                "existed on the battlefield to determine how much damage it deals."
        )
        ruling(
            "2024-02-02",
            "Any time you have priority, you may turn the face-down creature face up by revealing " +
                "what its disguise cost is and paying that cost. This is a special action. It " +
                "doesn't use the stack and can't be responded to. Only a face-down permanent can " +
                "be turned face up this way; a face-down spell cannot."
        )
        ruling(
            "2024-02-02",
            "Because the permanent is on the battlefield both before and after it's turned face " +
                "up, turning a permanent face up doesn't cause any enters-the-battlefield " +
                "abilities to trigger."
        )
    }
}
