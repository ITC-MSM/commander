package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Unstable Glyphbridge // Sandswirl Wanderglyph (CR 702.167, The Lost Caverns of Ixalan)
 * {3}{W}{W}
 * Artifact // Artifact Creature — Golem
 *
 * Front face — Unstable Glyphbridge ({3}{W}{W}, Artifact)
 *   When this artifact enters, if you cast it, for each player, choose a creature with power
 *   2 or less that player controls. Then destroy all creatures except creatures chosen this way.
 *   Craft with artifact {3}{W}{W}
 *
 * Back face — Sandswirl Wanderglyph (Artifact Creature — Golem, 5/3)
 *   Flying
 *   Whenever an opponent casts a spell during their turn, they can't attack you or
 *   planeswalkers you control this turn.
 *   Each opponent who attacked you or a planeswalker you control this turn can't cast spells.
 *
 * Implementation notes:
 *  - **ETB wipe** — the Winnowing shape: [ForEachPlayerEffect] (active player first) gathers each
 *    player's creatures, [Chooser.SourceController] lets the ability's controller pick the survivor
 *    (per the card's ruling, *you* choose for each player), with the selection filtered to power 2
 *    or less (evaluated against projected state). The unchosen remainder is destroyed via
 *    [MoveType.Destroy] inside the same iteration. A player with no power-≤2 creature gets no
 *    survivor (auto-selects nothing, everything is destroyed). Like Winnowing, the choose+destroy
 *    is interleaved per player rather than "all choices, then one simultaneous destruction" —
 *    identical outcomes except for exotic cross-player P/T dependencies mid-resolution and
 *    dies-trigger batching.
 *  - **"if you cast it"** — [Conditions.WasCast] as the trigger's intervening-if: false whenever
 *    the permanent was put onto the battlefield without being cast, which is exactly the craft
 *    return (and any reanimation/blink), so the wipe only fires on a cast.
 *  - **Craft with artifact {3}{W}{W}** — exact-count craft (`minCount = 1, maxCount = 1`) via the
 *    `craft(...)` helper; returns transformed as Sandswirl Wanderglyph.
 *  - **Back trigger** — [Triggers.OpponentCastsSpell] gated on [Conditions.IsNotYourTurn]
 *    ("during their turn"; exact in two-player games). The one-shot effect is a floating
 *    until-end-of-turn [Effects.CantAttackGroup] over creatures the *active player* controls:
 *    for the remainder of that turn the triggering opponent is the active player, and only the
 *    active player's creatures can be declared as attackers, so this is "that player can't attack"
 *    — which in a two-player game is precisely "can't attack you or planeswalkers you control".
 *    (In multiplayer, both the gate and the restriction would over-approximate; the engine's
 *    scenario coverage is two-player.) Rule 611.2c dynamics come free: a creature they cast later
 *    that turn (e.g. with haste) is also restricted.
 *  - **Back static** — [PlayersCantCastSpells] scoped to opponents, conditioned on
 *    [Conditions.PlayerAttackedPlayerThisTurn] (attacker = [Player.AnOpponent], defender = you).
 *    The per-turn attacked-players record ("attacked [a player]", CR 508.6) already counts attacks
 *    on you *or a planeswalker you control* — the defending player of an attack on a planeswalker
 *    is that planeswalker's controller (CR 508.5).
 *    Exact in two-player games ("an opponent" = the only opponent, who is also the only candidate
 *    caster); a multiplayer-exact per-caster scoping would need the cast-legality check to bind
 *    the casting player into the condition context.
 */

private val UnstableGlyphbridgeFront = card("Unstable Glyphbridge") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, if you cast it, for each player, choose a creature " +
        "with power 2 or less that player controls. Then destroy all creatures except creatures " +
        "chosen this way.\n" +
        "Craft with artifact {3}{W}{W} ({3}{W}{W}, Exile this artifact, Exile another artifact " +
        "you control or an artifact card from your graveyard: Return this card transformed under " +
        "its owner's control. Craft only as a sorcery.)"

    // When this artifact enters, if you cast it, for each player, choose a creature with power
    // 2 or less that player controls. Then destroy all creatures except creatures chosen this way.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        effect = ForEachPlayerEffect(
            players = Player.ActivePlayerFirst,
            effects = listOf(
                GatherCardsEffect(
                    source = CardSource.ControlledPermanents(
                        player = Player.You,
                        filter = GameObjectFilter.Creature
                    ),
                    storeAs = "playerCreatures"
                ),
                // The ability's controller (not the iterated player) chooses; only creatures
                // with power 2 or less are selectable. No eligible creature -> nothing spared.
                SelectFromCollectionEffect(
                    from = "playerCreatures",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    chooser = Chooser.SourceController,
                    filter = GameObjectFilter.Creature.powerAtMost(2),
                    storeSelected = "spared",
                    storeRemainder = "doomed",
                    prompt = "Choose a creature with power 2 or less this player controls",
                    useTargetingUI = true
                ),
                MoveCollectionEffect(
                    from = "doomed",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    moveType = MoveType.Destroy
                )
            )
        )
    }

    craft(
        filter = GameObjectFilter.Artifact,
        cost = "{3}{W}{W}",
        materialDescription = "artifact",
        minCount = 1,
        maxCount = 1
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Bastien Grivet"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d70f48e7-582c-4dd2-a64e-6fd03fa6b77e.jpg?1782694578"
    }
}

private val SandswirlWanderglyph = card("Sandswirl Wanderglyph") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Artifact Creature — Golem"
    power = 5
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever an opponent casts a spell during their turn, they can't attack you or " +
        "planeswalkers you control this turn.\n" +
        "Each opponent who attacked you or a planeswalker you control this turn can't cast spells."

    keywords(Keyword.FLYING)

    // Whenever an opponent casts a spell during their turn, they can't attack you or
    // planeswalkers you control this turn. (See KDoc: active-player-scoped floating
    // restriction, exact for two-player games.)
    triggeredAbility {
        trigger = Triggers.OpponentCastsSpell
        triggerCondition = Conditions.IsNotYourTurn
        effect = Effects.CantAttackGroup(
            GroupFilter(GameObjectFilter.Creature.controlledByActivePlayer())
        )
    }

    // Each opponent who attacked you or a planeswalker you control this turn can't cast spells.
    staticAbility {
        ability = PlayersCantCastSpells(
            affected = Player.EachOpponent,
            condition = Conditions.PlayerAttackedPlayerThisTurn(
                attacker = Player.AnOpponent,
                defender = Player.You
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Bastien Grivet"
        imageUri = "https://cards.scryfall.io/normal/back/d/7/d70f48e7-582c-4dd2-a64e-6fd03fa6b77e.jpg?1782694578"
    }
}

val UnstableGlyphbridge: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = UnstableGlyphbridgeFront,
    backFace = SandswirlWanderglyph
)
