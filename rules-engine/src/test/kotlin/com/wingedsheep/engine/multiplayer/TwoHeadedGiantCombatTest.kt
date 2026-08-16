package com.wingedsheep.engine.multiplayer

import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.BlockTaxManaSelectionContinuation
import com.wingedsheep.engine.core.ContinuationFrame
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.core.Concede
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.BlockersDeclaredEvent
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.ManaSourceOption
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilityRef
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilitiesSelectedResponse
import com.wingedsheep.engine.core.PermanentsSacrificedEvent
import com.wingedsheep.engine.core.SelectAtomicBlockTaxManaAbilitiesDecision
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.mechanics.combat.CombatDefenders
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.combat.AttackersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BlockedComponent
import com.wingedsheep.engine.state.components.combat.BlockersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.combat.BlockingComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.mtg.sets.definitions.ori.cards.ArchangelOfTithes
import com.wingedsheep.mtg.sets.definitions.mir.cards.CrystalVein
import com.wingedsheep.mtg.sets.definitions.scg.cards.ElvishAberration
import com.wingedsheep.mtg.sets.definitions.woe.cards.VirtueOfStrength
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Two-Headed Giant — Phase 5: combined combat (CR 805.10).
 *
 * The active team is the attacking team and the nonactive team the defending team (805.10a). The
 * active team makes one combined attack whose creatures may be controlled by either teammate, each
 * aimed at an opposing-team player (805.10b); a creature never attacks a teammate. The defending
 * team makes one combined block in which a creature controlled by either defender may block any
 * attacker aimed at any member of that team (805.10d). Damage applies to the shared team total.
 *
 * Teams are [[0,1],[2,3]] with turn order pinned to player order: p0,p1 = team 0 (active);
 * p2,p3 = team 1 (defending).
 */
class TwoHeadedGiantCombatTest : FunSpec({

    val bear = CardDefinition.creature(
        name = "Combat Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    val menaceBear = CardDefinition.creature(
        name = "Menace Combat Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        keywords = setOf(Keyword.MENACE),
    )

    val flyingBear = CardDefinition.creature(
        name = "Flying Combat Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        keywords = setOf(Keyword.FLYING),
    )

    val sacrificeManaBear = card("Sacrifice Mana Bear") {
        manaCost = "{1}{G}"
        colorIdentity = "G"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        activatedAbility {
            cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
            effect = Effects.AddColorlessMana(2)
            manaAbility = true
            timing = TimingRule.ManaAbility
        }
    }

    fun registry() = CardRegistry().also {
        it.register(bear)
        it.register(menaceBear)
        it.register(flyingBear)
        it.register(sacrificeManaBear)
        it.register(ArchangelOfTithes)
        it.register(ElvishAberration)
        it.register(CrystalVein)
        it.register(VirtueOfStrength)
        it.register(CardDefinition.basicLand("Plains", Subtype.PLAINS))
    }

    fun init2hg(): Pair<GameState, List<EntityId>> {
        val deck = Deck(cards = List(40) { bear.name })
        val result = GameInitializer(registry()).initializeGame(
            GameConfig(
                format = Format.TwoHeadedGiant(),
                players = (1..4).map { PlayerConfig("Player $it", deck) },
                teams = listOf(listOf(0, 1), listOf(2, 3)),
                startingPlayerIndex = 0,
                skipMulligans = true,
            )
        )
        return result.state to result.playerIds
    }

    /** Put a 2/2 bear on [owner]'s battlefield (untapped, not summoning sick). If [attacking] is
     *  set, it enters already declared as an attacker against that defender. */
    fun GameState.withBear(
        owner: EntityId,
        attacking: EntityId? = null,
        definition: CardDefinition = bear,
        extraKeywords: Set<Keyword> = emptySet(),
    ): Pair<GameState, EntityId> {
        val id = EntityId.generate()
        var container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = definition.name,
                name = definition.name,
                manaCost = definition.manaCost,
                typeLine = definition.typeLine,
                baseStats = definition.creatureStats,
                baseKeywords = definition.keywords + extraKeywords,
                ownerId = owner
            ),
            OwnerComponent(owner),
            ControllerComponent(owner)
        )
        if (attacking != null) container = container.with(AttackingComponent(defenderId = attacking))
        val next = withEntity(id, container).addToZone(ZoneKey(owner, Zone.BATTLEFIELD), id)
        return next to id
    }

    fun GameState.withPlains(owner: EntityId): Pair<GameState, EntityId> {
        val definition = CardDefinition.basicLand("Plains", Subtype.PLAINS)
        val id = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = definition.name,
                name = definition.name,
                manaCost = definition.manaCost,
                typeLine = definition.typeLine,
                ownerId = owner,
            ),
            OwnerComponent(owner),
            ControllerComponent(owner),
        )
        return withEntity(id, container).addToZone(ZoneKey(owner, Zone.BATTLEFIELD), id) to id
    }

    fun GameState.withCrystalVein(owner: EntityId): Pair<GameState, EntityId> {
        val id = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = CrystalVein.name,
                name = CrystalVein.name,
                manaCost = CrystalVein.manaCost,
                typeLine = CrystalVein.typeLine,
                ownerId = owner,
            ),
            OwnerComponent(owner),
            ControllerComponent(owner),
        )
        return withEntity(id, container).addToZone(ZoneKey(owner, Zone.BATTLEFIELD), id) to id
    }

    fun taxedTeamBlockState(): Triple<GameState, List<EntityId>, List<EntityId>> {
        val (base, p) = init2hg()
        val (s1, archangel) = base.withBear(p[0], attacking = p[2], definition = ArchangelOfTithes)
        val (s2, blkP2) = s1.withBear(p[2], definition = flyingBear)
        val (s3, blkP3) = s2.withBear(p[3], definition = flyingBear)
        val (s4, landP2) = s3.withPlains(p[2])
        val (s5, landP3) = s4.withPlains(p[3])
        var state = s5.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        return Triple(state, p, listOf(archangel, blkP2, blkP3, landP2, landP3))
    }

    fun taxedTeamBlockStateWithAberration(): Triple<GameState, List<EntityId>, List<EntityId>> {
        val (base, p) = init2hg()
        val (s1, archangel) = base.withBear(p[0], attacking = p[2], definition = ArchangelOfTithes)
        // The real Aberration supplies {G}{G}{G}; Flying is only fixture support so it can
        // legally block the attacking Archangel.
        val (s2, aberration) = s1.withBear(p[2], definition = ElvishAberration, extraKeywords = setOf(Keyword.FLYING))
        val (s3, blkP3) = s2.withBear(p[3], definition = flyingBear)
        val (s4, landP3) = s3.withPlains(p[3])
        var state = s4.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        return Triple(state, p, listOf(archangel, aberration, blkP3, landP3))
    }

    fun taxedTeamBlockStateWithCrystalVein(): Triple<GameState, List<EntityId>, List<EntityId>> {
        val (base, p) = init2hg()
        val (s1, archangel) = base.withBear(p[0], attacking = p[2], definition = ArchangelOfTithes)
        val (s2, blkP2) = s1.withBear(p[2], definition = flyingBear)
        val (s3, blkP3) = s2.withBear(p[3], definition = flyingBear)
        val (s4, crystalVein) = s3.withCrystalVein(p[2])
        val (s5, landP3) = s4.withPlains(p[3])
        var state = s5.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        return Triple(state, p, listOf(archangel, blkP2, blkP3, crystalVein, landP3))
    }

    fun taxedTeamBlockStateWithVirtueOfStrength(): Triple<GameState, List<EntityId>, List<EntityId>> {
        val (state, players, objects) = taxedTeamBlockState()
        val (withVirtue, virtue) = state.withBear(players[2], definition = VirtueOfStrength)
        return Triple(withVirtue, players, objects + virtue)
    }

    test("a creature attacks the opposing team, never a teammate (CR 805.10b)") {
        val (base, p) = init2hg()
        val (s1, atk) = base.withBear(p[0])
        val state = s1.copy(step = Step.DECLARE_ATTACKERS, phase = Phase.COMBAT).withPriority(p[0])
        val proc = ActionProcessor(registry())

        // Attacking the teammate (p1) is illegal.
        proc.process(state, DeclareAttackers(p[0], mapOf(atk to p[1]))).result.isSuccess.shouldBeFalse()
        // Attacking an opposing-team player (p2) is legal.
        proc.process(state, DeclareAttackers(p[0], mapOf(atk to p[2]))).result.isSuccess.shouldBeTrue()
    }

    test("the combined attack may include creatures controlled by BOTH active-team members (CR 805.10b)") {
        val (base, p) = init2hg()
        val (s1, atk0) = base.withBear(p[0])
        val (s2, atk1) = s1.withBear(p[1]) // the teammate's creature
        val state = s2.copy(step = Step.DECLARE_ATTACKERS, phase = Phase.COMBAT).withPriority(p[0])
        val proc = ActionProcessor(registry())

        // The active player declares one combined attack including the teammate's creature.
        val result = proc.process(
            state, DeclareAttackers(p[0], mapOf(atk0 to p[2], atk1 to p[3]))
        ).result
        result.isSuccess.shouldBeTrue()
        result.newState.getEntity(atk1)!!.get<AttackingComponent>()!!.defenderId shouldBe p[3]
    }

    test("the whole nonactive team defends — even an un-attacked teammate is a defending player (CR 805.10a)") {
        val (base, p) = init2hg()
        val (state, _) = base.withBear(p[0], attacking = p[2]) // p0 attacks p2 only
        // Both members of team 1 are defending players, including the un-attacked p3.
        CombatDefenders.defendingPlayers(state) shouldBe setOf(p[2], p[3])
        CombatDefenders.isDefendingPlayer(state, p[3]).shouldBeTrue()
        // Nobody on the attacking team is a defender.
        CombatDefenders.isDefendingPlayer(state, p[1]).shouldBeFalse()
    }

    test("a defending creature may block an attacker aimed at its TEAMMATE (CR 805.10d)") {
        val (base, p) = init2hg()
        val (s1, atk) = base.withBear(p[0], attacking = p[2]) // p0's attacker aimed at p2
        val (s2, blkP3) = s1.withBear(p[3])                   // blocker controlled by p3 (p2's teammate)
        var state = s2.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[3])
        val proc = ActionProcessor(registry())

        // p3 blocks an attacker that is attacking p2 — legal in 2HG (would be illegal in FFA, 509.1b).
        val result = proc.process(state, DeclareBlockers(p[3], mapOf(blkP3 to listOf(atk)))).result
        result.isSuccess.shouldBeTrue()
    }

    test("a member of the attacking team cannot declare blockers (CR 805.10a)") {
        val (base, p) = init2hg()
        val (s1, atk) = base.withBear(p[0], attacking = p[2]) // team 0 (active) attacks p2
        val (s2, blkP1) = s1.withBear(p[1])                   // p1 is on the active (attacking) team
        var state = s2.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[1])
        val proc = ActionProcessor(registry())

        // p1 is on the attacking team, so it may not block (the active team never blocks).
        proc.process(state, DeclareBlockers(p[1], mapOf(blkP1 to listOf(atk)))).result.isSuccess.shouldBeFalse()
    }

    test("one atomic team map combines both defenders' blockers for menace, then reaches the normal boundary (CR 805.10d)") {
        val (base, p) = init2hg()
        val (s1, menace) = base.withBear(p[0], attacking = p[2], definition = menaceBear)
        val (s2, blkP2) = s1.withBear(p[2])
        val (s3, blkP3) = s2.withBear(p[3])
        var state = s3.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        val proc = ActionProcessor(registry())

        val result = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(menace), blkP3 to listOf(menace)))
        ).result

        result.isSuccess.shouldBeTrue()
        result.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
        result.newState.getEntity(menace)!!.get<BlockedComponent>()!!.blockerIds.toSet() shouldBe setOf(blkP2, blkP3)
        result.newState.getEntity(blkP2)!!.get<BlockingComponent>()!!.blockedAttackerIds shouldBe listOf(menace)
        result.newState.getEntity(blkP3)!!.get<BlockingComponent>()!!.blockedAttackerIds shouldBe listOf(menace)
        result.newState.getEntity(p[2])!!.has<BlockersDeclaredThisCombatComponent>().shouldBeTrue()
        result.newState.getEntity(p[3])!!.has<BlockersDeclaredThisCombatComponent>().shouldBeTrue()
        // The completed team declaration goes through the ordinary final SBA/trigger boundary.
        result.newState.priorityPlayerId shouldBe p[0]
    }

    test("a one-creature combined team map cannot block a menace attacker (CR 805.10d)") {
        val (base, p) = init2hg()
        val (s1, menace) = base.withBear(p[0], attacking = p[2], definition = menaceBear)
        val (s2, blkP3) = s1.withBear(p[3])
        var state = s2.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])

        ActionProcessor(registry()).process(
            state,
            DeclareBlockers(p[2], mapOf(blkP3 to listOf(menace)))
        ).result.isSuccess.shouldBeFalse()
    }

    test("a defending team cannot submit a second blockers declaration after its atomic map") {
        val (base, p) = init2hg()
        val (s1, atk) = base.withBear(p[0], attacking = p[2])
        val (s2, firstBlocker) = s1.withBear(p[2])
        val (s3, laterBlocker) = s2.withBear(p[3])
        var state = s3.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        val proc = ActionProcessor(registry())

        val first = proc.process(state, DeclareBlockers(p[2], mapOf(firstBlocker to listOf(atk)))).result
        first.isSuccess.shouldBeTrue()
        val second = proc.process(first.newState, DeclareBlockers(p[3], mapOf(laterBlocker to listOf(atk)))).result

        second.isSuccess.shouldBeFalse()
        second.newState shouldBe first.newState
        second.events.size shouldBe 0
    }

    test("a combined defending-team map rejects a blocker controlled by the attacking team") {
        val (base, p) = init2hg()
        val (s1, atk) = base.withBear(p[0], attacking = p[2])
        val (s2, offTeamBlocker) = s1.withBear(p[1])
        var state = s2.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
        state = state.copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])

        ActionProcessor(registry()).process(
            state,
            DeclareBlockers(p[2], mapOf(offTeamBlocker to listOf(atk)))
        ).result.isSuccess.shouldBeFalse()
    }

    test("a taxed combined team map collects pay-pay intents then pays and commits once") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val proc = ActionProcessor(registry())

        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p2Prompt.playerId shouldBe p[2]
        declared.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeFalse()
        declared.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeFalse()
        declared.events.filterIsInstance<BlockersDeclaredEvent>() shouldBe emptyList()

        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP2, -1)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p3Prompt.playerId shouldBe p[3]
        // The second prompt is still inside the same turn-based action; priority did not pass.
        p2Accepted.newState.priorityPlayerId shouldBe p[2]
        p2Accepted.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeFalse()
        p2Accepted.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeFalse()
        p2Accepted.events.filterIsInstance<TappedEvent>() shouldBe emptyList()

        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p3Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
            )),
        ).result
        paid.isSuccess.shouldBeTrue()
        paid.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
        paid.events.filterIsInstance<TappedEvent>().map { it.entityId }.toSet() shouldBe setOf(landP2, landP3)
        paid.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getEntity(blkP2)!!.has<BlockingComponent>().shouldBeTrue()
        paid.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeTrue()
    }

    test("a teammate declining after the first intent rolls the combined block back fully") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP2, -1)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()

        val declined = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(p3Prompt.id, declined = true)),
        ).result
        declined.isSuccess.shouldBeTrue()
        declined.pendingDecision shouldBe null
        declined.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP2)!!.has<BlockingComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeFalse()
        declined.newState.getEntity(p[2])!!.has<BlockersDeclaredThisCombatComponent>().shouldBeFalse()
        declined.events.filterIsInstance<TappedEvent>() shouldBe emptyList()
        declined.events.filterIsInstance<BlockersDeclaredEvent>() shouldBe emptyList()
    }

    test("a fixed-output multi-mana source pays only its controller's atomic team block tax") {
        val (state, p, objects) = taxedTeamBlockStateWithAberration()
        val (archangel, aberration, blkP3, landP3) = objects
        val proc = ActionProcessor(registry())

        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(aberration to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p2Prompt.playerId shouldBe p[2]

        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(aberration, 0)),
            )),
        ).result
        p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().playerId shouldBe p[3]
        p2Accepted.newState.getEntity(aberration)!!.has<TappedComponent>().shouldBeFalse()
        p2Accepted.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.green shouldBe 0

        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(
                p[3],
                AtomicBlockTaxManaAbilitiesSelectedResponse(
                    p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().id,
                    selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
                ),
            ),
        ).result

        paid.isSuccess.shouldBeTrue()
        paid.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
        paid.newState.getEntity(aberration)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.green shouldBe 2
        paid.newState.getEntity(p[3])!!.get<ManaPoolComponent>()!!.green shouldBe 0
        paid.newState.getEntity(aberration)!!.has<BlockingComponent>().shouldBeTrue()
        paid.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeTrue()
    }

    test("Crystal Vein's atomic tax selection exposes and pays the exact normal branch") {
        val (state, p, objects) = taxedTeamBlockStateWithCrystalVein()
        val (archangel, blkP2, blkP3, crystalVein, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p2Prompt.availableOptions.map { it.ref } shouldBe listOf(
            AtomicBlockTaxManaAbilityRef(crystalVein, 0),
            AtomicBlockTaxManaAbilityRef(crystalVein, 1),
        )
        p2Prompt.availableOptions.map { it.manaAmount } shouldBe listOf(1, 2)
        p2Prompt.availableOptions.map { it.requiresSacrificeSelf } shouldBe listOf(false, true)
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(crystalVein, 0)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()

        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p3Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
            )),
        ).result

        paid.isSuccess.shouldBeTrue()
        paid.newState.getEntity(crystalVein)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getBattlefield().contains(crystalVein).shouldBeTrue()
        paid.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.colorless shouldBe 0
        paid.events.filterIsInstance<TappedEvent>().map { it.entityId }.toSet() shouldBe setOf(crystalVein, landP3)
        paid.events.filterIsInstance<PermanentsSacrificedEvent>() shouldBe emptyList()
        paid.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
    }

    test("Crystal Vein's atomic sacrifice branch pays two, sacrifices the source, and keeps the declaration valid") {
        val (state, p, objects) = taxedTeamBlockStateWithCrystalVein()
        val (archangel, blkP2, blkP3, crystalVein, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(state, DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel)))).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(crystalVein, 1)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p3Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
            )),
        ).result

        paid.isSuccess.shouldBeTrue()
        paid.newState.getBattlefield().contains(crystalVein).shouldBeFalse()
        paid.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.colorless shouldBe 1
        paid.events.filterIsInstance<PermanentsSacrificedEvent>().single().permanentIds shouldBe listOf(crystalVein)
        paid.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
        paid.newState.getEntity(blkP2)!!.has<BlockingComponent>().shouldBeTrue()
    }

    test("atomic Crystal Vein branch decline and forged branch preserve the proposed declaration") {
        val (state, p, objects) = taxedTeamBlockStateWithCrystalVein()
        val (archangel, blkP2, _, crystalVein, _) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(state, DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel)))).result
        val prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val forged = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(crystalVein, 99)),
            )),
        ).result
        forged.isSuccess.shouldBeFalse()
        forged.newState shouldBe declared.newState
        forged.events shouldBe emptyList()

        val declined = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(prompt.id, declined = true)),
        ).result
        declined.isSuccess.shouldBeTrue()
        // A valid decline records its submission, but must not produce payment or combat events.
        declined.events.size shouldBe 1
        declined.newState.getEntity(crystalVein)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP2)!!.has<BlockingComponent>().shouldBeFalse()
    }

    test("a declared blocker sacrificed for atomic tax leaves its attacker blocked but is not blocking") {
        val (base, p) = init2hg()
        val (s1, archangel) = base.withBear(p[0], attacking = p[2], definition = ArchangelOfTithes)
        val (s2, sacrificeBlocker) = s1.withBear(p[2], definition = sacrificeManaBear, extraKeywords = setOf(Keyword.FLYING))
        val (s3, teammateBlocker) = s2.withBear(p[3], definition = flyingBear)
        val (s4, teammateLand) = s3.withPlains(p[3])
        val state = s4.updateEntity(p[0]) { it.with(AttackersDeclaredThisCombatComponent) }
            .copy(step = Step.DECLARE_BLOCKERS, phase = Phase.COMBAT).withPriority(p[2])
        val proc = ActionProcessor(registry())
        val declared = proc.process(state, DeclareBlockers(p[2], mapOf(
            sacrificeBlocker to listOf(archangel), teammateBlocker to listOf(archangel),
        ))).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(sacrificeBlocker, 0)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p3Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(teammateLand, -1)),
            )),
        ).result

        paid.isSuccess.shouldBeTrue()
        paid.newState.getBattlefield().contains(sacrificeBlocker).shouldBeFalse()
        paid.newState.getEntity(archangel)!!.has<BlockedComponent>().shouldBeTrue()
        paid.newState.getEntity(sacrificeBlocker)!!.has<BlockingComponent>().shouldBeFalse()
    }

    test("an atomic payer preserves a tapped mana source's multiplied normal output") {
        val (state, p, objects) = taxedTeamBlockStateWithVirtueOfStrength()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val proc = ActionProcessor(registry())

        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p2Prompt.availableOptions.single {
            it.ref == AtomicBlockTaxManaAbilityRef(landP2, -1)
        }.manaAmount shouldBe 3

        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP2, -1)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val paid = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p3Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
            )),
        ).result

        paid.isSuccess.shouldBeTrue()
        paid.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.white shouldBe 2
        paid.newState.getEntity(p[3])!!.get<ManaPoolComponent>()!!.white shouldBe 0
        paid.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeTrue()
        paid.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeTrue()
        paid.events.filterIsInstance<BlockersDeclaredEvent>().size shouldBe 1
    }

    test("a declined teammate leaves a selected fixed-output multi-mana source and pool untouched") {
        val (state, p, objects) = taxedTeamBlockStateWithAberration()
        val (archangel, aberration, blkP3, _) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(aberration to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(aberration, 0)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()

        val declined = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(p3Prompt.id, declined = true)),
        ).result

        declined.isSuccess.shouldBeTrue()
        declined.pendingDecision shouldBe null
        declined.events.filterIsInstance<TappedEvent>() shouldBe emptyList()
        declined.events.filterIsInstance<BlockersDeclaredEvent>() shouldBe emptyList()
        declined.newState.getEntity(aberration)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.green shouldBe 0
        declined.newState.getEntity(aberration)!!.has<BlockingComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeFalse()
    }

    test("a payer conceding during atomic team block-tax collection cancels the whole proposal") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP2, -1)),
            )),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p3Prompt.playerId shouldBe p[3]

        val conceded = proc.process(
            p2Accepted.newState,
            Concede(p[2]),
        ).result

        conceded.pendingDecision shouldBe null
        conceded.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeFalse()
        conceded.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeFalse()
        conceded.events.filterIsInstance<BlockersDeclaredEvent>() shouldBe emptyList()
    }

    test("p2 submitting a p3-only taxed block gives the payment prompt to p3") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, _, blkP3) = objects

        val declared = ActionProcessor(registry()).process(
            state,
            DeclareBlockers(p[2], mapOf(blkP3 to listOf(archangel))),
        ).result

        declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().playerId shouldBe p[3]
    }

    test("a direct mana ability is rejected without mutation while a combined team block-tax prompt is pending") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().playerId shouldBe p[2]

        val p2DirectMana = proc.process(
            declared.newState,
            ActivateAbility(p[2], landP2, AbilityId.intrinsicMana('W')),
        ).result

        p2DirectMana.isSuccess.shouldBeFalse()
        p2DirectMana.newState shouldBe declared.newState
        p2DirectMana.events shouldBe emptyList()

        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                p2Prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP2, -1)),
            )),
        ).result
        p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().playerId shouldBe p[3]

        val p3DirectMana = proc.process(
            p2Accepted.newState,
            ActivateAbility(p[3], landP3, AbilityId.intrinsicMana('W')),
        ).result

        p3DirectMana.isSuccess.shouldBeFalse()
        p3DirectMana.newState shouldBe p2Accepted.newState
        p3DirectMana.events shouldBe emptyList()
    }

    test("floating mana pays an atomic team tax without exposing or permitting extra mana activations") {
        val (baseState, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, blkP3, landP2, landP3) = objects
        val state = baseState
            .updateEntity(p[2]) { it.with(ManaPoolComponent(white = 1)) }
            .updateEntity(p[3]) { it.with(ManaPoolComponent(white = 1)) }
        val proc = ActionProcessor(registry())

        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel), blkP3 to listOf(archangel))),
        ).result
        val p2Prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p2Prompt.playerId shouldBe p[2]
        p2Prompt.availableOptions shouldBe emptyList()
        p2Prompt.autoPaySuggestion shouldBe emptyList()

        val directMana = proc.process(
            declared.newState,
            ActivateAbility(p[2], landP2, AbilityId.intrinsicMana('W')),
        ).result
        directMana.isSuccess.shouldBeFalse()
        directMana.newState shouldBe declared.newState
        directMana.events shouldBe emptyList()

        val p2Accepted = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(p2Prompt.id, autoPay = true)),
        ).result
        val p3Prompt = p2Accepted.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()
        p3Prompt.playerId shouldBe p[3]
        p3Prompt.availableOptions shouldBe emptyList()
        p3Prompt.autoPaySuggestion shouldBe emptyList()

        val declined = proc.process(
            p2Accepted.newState,
            SubmitDecision(p[3], AtomicBlockTaxManaAbilitiesSelectedResponse(p3Prompt.id, declined = true)),
        ).result
        declined.isSuccess.shouldBeTrue()
        declined.pendingDecision shouldBe null
        declined.events.filterIsInstance<TappedEvent>() shouldBe emptyList()
        declined.events.filterIsInstance<BlockersDeclaredEvent>() shouldBe emptyList()
        declined.newState.getEntity(landP2)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(landP3)!!.has<TappedComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP2)!!.has<BlockingComponent>().shouldBeFalse()
        declined.newState.getEntity(blkP3)!!.has<BlockingComponent>().shouldBeFalse()
        declined.newState.getEntity(p[2])!!.get<ManaPoolComponent>()!!.white shouldBe 1
        declined.newState.getEntity(p[3])!!.get<ManaPoolComponent>()!!.white shouldBe 1
    }

    test("a direct mana ability is rejected without mutation for a p3-only team block-tax payer") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, _, blkP3, _, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP3 to listOf(archangel))),
        ).result
        declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>().playerId shouldBe p[3]

        val directMana = proc.process(
            declared.newState,
            ActivateAbility(p[3], landP3, AbilityId.intrinsicMana('W')),
        ).result

        directMana.isSuccess.shouldBeFalse()
        directMana.newState shouldBe declared.newState
        directMana.events shouldBe emptyList()
    }

    test("block-tax payment rejects forged and duplicate sources without mutating state") {
        val (state, p, objects) = taxedTeamBlockState()
        val (archangel, blkP2, _, landP2, landP3) = objects
        val proc = ActionProcessor(registry())
        val declared = proc.process(
            state,
            DeclareBlockers(p[2], mapOf(blkP2 to listOf(archangel))),
        ).result
        val prompt = declared.pendingDecision.shouldBeInstanceOf<SelectAtomicBlockTaxManaAbilitiesDecision>()

        val forged = proc.process(
            declared.newState,
            SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                prompt.id, selectedManaAbilityRefs = listOf(AtomicBlockTaxManaAbilityRef(landP3, -1)),
            )),
        ).result
        forged.isSuccess.shouldBeFalse()
        forged.newState shouldBe declared.newState
        forged.events shouldBe emptyList()

        val duplicate = shouldNotThrowAny {
            proc.process(
                declared.newState,
                SubmitDecision(p[2], AtomicBlockTaxManaAbilitiesSelectedResponse(
                    prompt.id,
                    selectedManaAbilityRefs = listOf(
                        AtomicBlockTaxManaAbilityRef(landP2, -1),
                        AtomicBlockTaxManaAbilityRef(landP2, -1),
                    ),
                )),
            ).result
        }
        duplicate.isSuccess.shouldBeFalse()
        duplicate.newState shouldBe declared.newState
        duplicate.events shouldBe emptyList()
    }

    test("legacy paused block-tax continuation still decodes as a one-payer payment") {
        val json = Json {
            serializersModule = com.wingedsheep.engine.core.engineSerializersModule
            encodeDefaults = true
        }
        val original: ContinuationFrame = BlockTaxManaSelectionContinuation(
            decisionId = "legacy-block-tax",
            blockingPlayer = EntityId.of("defender"),
            blockers = emptyMap(),
            manaCost = com.wingedsheep.sdk.core.ManaCost.parse("{1}"),
            availableSources = emptyList(),
            autoPaySuggestion = emptyList(),
        )
        val objectPayload = json.parseToJsonElement(
            json.encodeToString(ContinuationFrame.serializer(), original),
        ).jsonObject.toMutableMap()
        // This is exactly the old persisted shape: no atomic-plan fields existed yet.
        objectPayload.remove("payerPlans")
        objectPayload.remove("payerIndex")
        objectPayload.remove("acceptedIntents")

        val decoded = json.decodeFromString(
            ContinuationFrame.serializer(),
            JsonObject(objectPayload).toString(),
        ).shouldBeInstanceOf<BlockTaxManaSelectionContinuation>()

        decoded.payerPlans shouldBe emptyList()
        decoded.manaCost shouldBe com.wingedsheep.sdk.core.ManaCost.parse("{1}")
        decoded.blockingPlayer shouldBe EntityId.of("defender")
    }

    test("legacy mana-source options default to one mana when decoding") {
        val json = Json {
            serializersModule = com.wingedsheep.engine.core.engineSerializersModule
            encodeDefaults = true
        }
        val original = ManaSourceOption(
            entityId = EntityId.of("legacy-source"),
            name = "Legacy Forest",
            producesColors = setOf(com.wingedsheep.sdk.core.Color.GREEN),
            producesColorless = false,
            manaAmount = 3,
        )
        val payload = json.parseToJsonElement(json.encodeToString(original)).jsonObject.toMutableMap()
        payload.remove("manaAmount")

        val decoded = json.decodeFromString<ManaSourceOption>(JsonObject(payload).toString())

        decoded.manaAmount shouldBe 1
    }
})
