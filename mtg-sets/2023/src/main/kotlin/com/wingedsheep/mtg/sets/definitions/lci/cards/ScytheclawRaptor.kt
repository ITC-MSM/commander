package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scytheclaw Raptor
 * {2}{R}
 * Creature — Dinosaur
 * 4/3
 * Whenever a player casts a spell, if it's not their turn, this creature deals 4 damage to them.
 */
val ScytheclawRaptor = card("Scytheclaw Raptor") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Whenever a player casts a spell, if it's not their turn, this creature deals 4 damage to them."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.AnyPlayerCastsSpell
        // "if it's not their turn" — "their" is the casting (triggering) player, so this is a
        // turn check relative to the triggering player, not Scytheclaw's controller.
        triggerCondition = Conditions.Not(Conditions.IsPlayersTurn(Player.TriggeringPlayer))
        effect = Effects.DealDamage(4, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        // Override the auto-generated text: NotCondition renders "if not (if it's their turn)",
        // an awkward double-"if". Pin it to the printed oracle wording instead.
        description = "Whenever a player casts a spell, if it's not their turn, this creature deals 4 damage to them."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "John Tedrick"
        flavorText = "\"She's a smart one. Noticed all on her own that the bloodsuckers wave their hands around " +
            "to do their filthy magic, so she rips off their hands first.\"\n—Citamitzin, Sun Empire warrior"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/9436cf62-56d6-4662-9982-72e9be80d25c.jpg?1782694476"
    }
}
