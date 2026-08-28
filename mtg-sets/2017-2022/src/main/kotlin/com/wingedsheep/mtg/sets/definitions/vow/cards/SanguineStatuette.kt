package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sanguine Statuette
 * {1}{R}
 * Artifact
 *
 * When this artifact enters, create a Blood token.
 * Whenever you sacrifice a Blood token, you may have this artifact become a 3/3 Vampire artifact
 * creature with haste until end of turn.
 *
 * The animation trigger is [Triggers.YouSacrificeA] rather than the batch
 * `YouSacrificeOneOrMore`: "whenever you sacrifice a Blood token" is per-permanent (CR 603.2c),
 * so sacrificing two Bloods to one cost gives two triggers and two chances to say yes.
 *
 * [Effects.BecomeCreature] sets *base* P/T in layer 7b, which is what makes the printed rulings
 * true without extra wiring — a later -2/-2 still applies on top (leaving a 1/1), and a second
 * sacrifice re-sets the base to 3/3 without undoing that -2/-2. `creatureTypes` supplies the
 * Vampire and `addTypes` the printed "artifact", which is a no-op union on a permanent that is
 * already one — the literal reading of the sentence rather than a type the text leaves implied.
 */
val SanguineStatuette = card("Sanguine Statuette") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, create a Blood token. (It's an artifact with \"{1}, " +
        "{T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "Whenever you sacrifice a Blood token, you may have this artifact become a 3/3 Vampire " +
        "artifact creature with haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateBlood(1)
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Artifact.withSubtype("Blood"))
        effect = MayEffect(
            Effects.BecomeCreature(
                target = EffectTarget.Self,
                power = 3,
                toughness = 3,
                keywords = setOf(Keyword.HASTE),
                creatureTypes = setOf("Vampire"),
                addTypes = setOf("ARTIFACT"),
                duration = Duration.EndOfTurn
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "177"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/706fafd4-443f-4e16-972e-86c16f22670a.jpg?1783924823"
        ruling(
            "2021-11-19",
            "If after becoming a creature, Sanguine Statuette becomes subject to an effect that " +
                "reduces or increases its toughness, sacrificing another Blood token won't allow " +
                "you to counteract that effect. For example, if a resolving spell gives it -2/-2 " +
                "until end of turn, it will be a 1/1 creature, even if you set its power to 3/3 again."
        )
        ruling(
            "2021-11-19",
            "If another effect sets Sanguine Statuette's power and/or toughness to another number, " +
                "sacrificing a Blood token will set it back to 3/3. However, it would still be " +
                "subject to any increases or reductions that are applying, as described above."
        )
    }
}
