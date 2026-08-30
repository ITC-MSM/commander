package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Voltaic Visionary // Volt-Charged Berserker (Innistrad: Crimson Vow #183)
 * {1}{R}
 * Creature — Human Wizard // Creature — Human Berserker
 *
 * Front — Voltaic Visionary (3/1)
 *   {T}: This creature deals 2 damage to you. Exile the top card of your library. You may play that
 *   card this turn. Activate only as a sorcery.
 *   When you play a card exiled with this creature, transform this creature.
 *
 * Back — Volt-Charged Berserker (4/3)
 *   This creature can't block.
 *
 * Modeling notes:
 *
 *  - **The two printed lines are one effect here.** "You may play that card this turn" and "When you
 *    play a card exiled with this creature, transform this creature" are the impulse-exile grant and
 *    its rider, so they collapse into [Effects.GrantMayPlayFromExile]'s `onPlayRider` (the Fires of
 *    Mount Doom shape): the engine registers a link-id-scoped delayed trigger alongside the play
 *    permission, and playing a granted card fires it *as a triggered ability of this creature*.
 *    That is exactly the printed ruling — "Voltaic Visionary will transform **before** that spell
 *    resolves", because the reflexive trigger goes on the stack above it. The rider expires with the
 *    grant at end of turn, which loses nothing: the permission itself is only good for that turn, so
 *    there is no window in which a card "exiled with this creature" is playable but unwatched.
 *  - **No `nonLandOnly`.** The card says "**play**", not "cast", so a land among the exiled cards is
 *    playable (subject to the normal one-land-per-turn rule, per the printed ruling) and playing it
 *    transforms the Visionary just the same.
 *  - **The damage is to you**, from this creature — `damageSource = Self` so lifelink/damage triggers
 *    and any "damage dealt by a source you control" reads see the right source.
 *  - "Activate only as a sorcery" is `timing = TimingRule.SorcerySpeed`.
 *  - The back face is a transformed face with no mana cost, so its colour comes from a colour
 *    indicator (CR 204): `colorIndicator = "R"`.
 */
private val VoltaicVisionaryFront = card("Voltaic Visionary") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 1
    oracleText = "{T}: This creature deals 2 damage to you. Exile the top card of your library. " +
        "You may play that card this turn. Activate only as a sorcery.\n" +
        "When you play a card exiled with this creature, transform this creature."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            listOf(
                Effects.DealDamage(
                    2,
                    EffectTarget.PlayerRef(Player.You),
                    damageSource = EffectTarget.Self
                ),
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                    storeAs = "voltaicExiled"
                ),
                MoveCollectionEffect(
                    from = "voltaicExiled",
                    destination = CardDestination.ToZone(Zone.EXILE)
                ),
                Effects.GrantMayPlayFromExile(
                    from = "voltaicExiled",
                    onPlayRider = TransformEffect(EffectTarget.Self)
                ),
            )
        )
        timing = TimingRule.SorcerySpeed
        description = "This creature deals 2 damage to you. Exile the top card of your library. " +
            "You may play that card this turn. When you play a card exiled this way, transform " +
            "this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Francisco Miyara"
        flavorText = "She spent many years trying to harness chaos."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8b85386-462b-46f8-9412-fd47ed1dc1da.jpg?1783924827"

        ruling("2021-11-19", "Playing a card this way follows all the normal timing rules for that card. For example, if you play a land this way, you may do so only during your main phase while the stack is empty and only if you haven't yet played a land (unless another effect allows you to play additional lands).")
        ruling("2021-11-19", "When you play a spell exiled this way, Voltaic Visionary will transform before that spell resolves.")
        ruling("2021-11-19", "If Voltaic Visionary transforms into Volt-Charged Berserker after it has been declared as a blocker, it will not be removed from combat and is still blocking.")
    }
}

private val VoltChargedBerserker = card("Volt-Charged Berserker") {
    manaCost = ""
    colorIdentity = "R"
    colorIndicator = "R" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Human Berserker"
    power = 4
    toughness = 3
    oracleText = "This creature can't block."

    staticAbility {
        ability = CantBlock(filter = GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Francisco Miyara"
        flavorText = "She finally realized that chaos was meant to run rampant, as was she."
        imageUri = "https://cards.scryfall.io/normal/back/a/8/a8b85386-462b-46f8-9412-fd47ed1dc1da.jpg?1783924827"
    }
}

val VoltaicVisionary: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = VoltaicVisionaryFront,
    backFace = VoltChargedBerserker,
)
