package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Archaic's Agony
 * {4}{R}
 * Sorcery
 *
 * Converge — Archaic's Agony deals X damage to target creature, where X is the number of colors of
 * mana spent to cast this spell. Exile cards from the top of your library equal to the excess
 * damage dealt to that creature this way. You may play those cards until the end of your next turn.
 *
 * Converge is an ability word (no keyword); X is [DynamicAmount.DistinctColorsManaSpent]
 * ([com.wingedsheep.sdk.dsl.DynamicAmounts.colorsOfManaSpent]). Composed from existing atoms:
 *  1. [Effects.DealDamage] deals that color-count to the target and marks the damage.
 *  2. The exile count reads the post-damage excess via
 *     `EntityProperty(Target(0), ExcessMarkedDamage)` — `max(0, marked − toughness)` (CR 120.4a),
 *     the same amount Hell to Pay reads. CompositeEffect resolves steps sequentially with no
 *     interleaved SBA pass, so the marked damage in scope is exactly what this spell just dealt.
 *  3. Gather that many cards off the top of your library → move them to exile → grant
 *     "you may play them until the end of your next turn" ([MayPlayExpiry.UntilEndOfNextTurn]) —
 *     the impulse-draw shape used by Alania's Pathmaker.
 *
 * If excess is 0 (the creature wasn't overkilled), step 3 gathers 0 cards and is a no-op.
 */
val ArchaicsAgony = card("Archaic's Agony") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Converge — Archaic's Agony deals X damage to target creature, where X is the " +
        "number of colors of mana spent to cast this spell. Exile cards from the top of your " +
        "library equal to the excess damage dealt to that creature this way. You may play those " +
        "cards until the end of your next turn."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(DynamicAmount.DistinctColorsManaSpent, creature),
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(
                    count = DynamicAmount.EntityProperty(
                        EntityReference.Target(0),
                        EntityNumericProperty.ExcessMarkedDamage,
                    ),
                    player = Player.You,
                ),
                storeAs = "exiledByAgony",
            ),
            MoveCollectionEffect(
                from = "exiledByAgony",
                destination = CardDestination.ToZone(com.wingedsheep.sdk.core.Zone.EXILE),
            ),
            GrantMayPlayFromExileEffect("exiledByAgony", MayPlayExpiry.UntilEndOfNextTurn),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Joshua Raphael"
        flavorText = "Its past became present became paradox."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d99f8b2-5c1c-4059-bf68-c6b2e9e5b275.jpg?1775937675"
    }
}
