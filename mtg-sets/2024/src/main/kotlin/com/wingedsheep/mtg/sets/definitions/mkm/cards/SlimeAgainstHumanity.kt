package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Slime Against Humanity — Murders at Karlov Manor #177
 * {2}{G} · Sorcery · Common
 *
 * The Ooze-or-name test is one union filter reused for both relevant zones. The resolving spell is
 * still on the stack, so it does not count itself; face-down exiled cards have no matching name or
 * subtype. The token creator publishes the new Ooze through [CREATED_TOKENS], allowing the counter
 * effect to address exactly that token after it enters.
 *
 * The final sentence is a deck-construction allowance and requires no in-game script.
 */
val SlimeAgainstHumanity = card("Slime Against Humanity") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 0/0 green Ooze creature token with trample. Put X +1/+1 counters on it, " +
        "where X is two plus the total number of cards you own in exile and in your graveyard that " +
        "are Oozes or are named Slime Against Humanity.\n" +
        "A deck can have any number of cards named Slime Against Humanity."

    spell {
        val matchingSlimes = (
            GameObjectFilter.Any.withSubtype("Ooze") or
                GameObjectFilter.Any.named("Slime Against Humanity")
            ).faceUp()
        val counterCount = DynamicAmount.Add(
            DynamicAmount.Fixed(2),
            DynamicAmount.Add(
                DynamicAmounts.zone(Player.You, Zone.EXILE, matchingSlimes).count(),
                DynamicAmounts.zone(Player.You, Zone.GRAVEYARD, matchingSlimes).count(),
            ),
        )

        effect = Effects.CreateToken(
            power = 0,
            toughness = 0,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Ooze"),
            keywords = setOf(Keyword.TRAMPLE),
            imageUri = "https://cards.scryfall.io/normal/front/5/4/54b37032-9660-4dfb-9629-c4e8eddc43b0.jpg?1783912608",
        ).then(
            Effects.AddDynamicCounters(
                Counters.PLUS_ONE_PLUS_ONE,
                counterCount,
                EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Brent Hollowell"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb21318-d32e-4724-8908-c0d7613de2f4.jpg?1783912861"
        ruling(
            "2024-02-02",
            "Slime Against Humanity isn't put into your graveyard until after it finishes resolving, " +
                "so it doesn't count itself for its own effect.",
        )
        ruling(
            "2024-02-02",
            "If you own face-down cards in exile, they won't count toward the value of X, even if " +
                "you're allowed to look at them and you know they are Ooze cards or cards named " +
                "Slime Against Humanity.",
        )
    }
}
