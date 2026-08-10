package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.ScientistSupremeOfAim
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Scientist Supreme of A.I.M. (MSH #225) — {U}{B} Legendary Creature — Human Scientist Villain, 2/2.
 *
 * Pay 2 life: Copy target activated or triggered ability you control from an artifact source. You
 * may choose new targets for the copy. Activate only during your turn and only once each turn.
 *
 * The artifact-source twin of Echo, Perceptive Prodigy. Covered here:
 *  - an artifact-source ability is enumerated and copied, and the copy is retargeted (CR 707.10c);
 *  - a creature-source ability is neither offered nor accepted;
 *  - the cost is 2 life and the ability is once-per-turn and your-turn-only.
 */
class ScientistSupremeOfAimScenarioTest : FunSpec({

    // Artifact source: {T}: Target creature you control gets +1/+0 until end of turn.
    val testLens = card("Scientist Test Lens") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = "{T}: Target creature you control gets +1/+0 until end of turn."
        activatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0))
            target = Targets.CreatureYouControl
            timing = TimingRule.InstantSpeed
        }
    }

    // Creature source: the negative control.
    val testPumper = card("Scientist Test Pumper") {
        manaCost = "{1}"
        typeLine = "Creature — Soldier"
        power = 1
        toughness = 1
        oracleText = "{T}: Target creature you control gets +1/+0 until end of turn."
        activatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0))
            target = Targets.CreatureYouControl
            timing = TimingRule.InstantSpeed
        }
    }

    val copyAbilityId = ScientistSupremeOfAim.activatedAbilities.single().id

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ScientistSupremeOfAim, testLens, testPumper))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        return driver
    }

    /** The legal targets the enumerator offers for the Scientist's copy ability right now. */
    fun offeredCopyTargets(driver: GameTestDriver, me: EntityId, scientist: EntityId): List<EntityId> =
        driver.legalActions(me)
            .filter { (it.action as? ActivateAbility)?.sourceId == scientist }
            .flatMap { it.validTargets ?: emptyList() }

    test("copies an artifact-source ability, retargets the copy, and costs 2 life") {
        val driver = setup()
        val me = driver.activePlayer!!

        // No tap in the cost, so summoning sickness is irrelevant — deliberately not cleared.
        val scientist = driver.putCreatureOnBattlefield(me, "Scientist Supreme of A.I.M.")
        val lens = driver.putPermanentOnBattlefield(me, "Scientist Test Lens")
        val creatureA = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val creatureB = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lensAbilityId = driver.cardRegistry.requireCard("Scientist Test Lens").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = lens, abilityId = lensAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )
        val lensOnStack = driver.getTopOfStack()!!

        // Enumerated as a legal target, not merely accepted on submit.
        offeredCopyTargets(driver, me, scientist) shouldContain lensOnStack

        val lifeBefore = driver.getLifeTotal(me)
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = scientist, abilityId = copyAbilityId,
                targets = listOf(ChosenTarget.Spell(lensOnStack))
            )
        )
        driver.getLifeTotal(me) shouldBe lifeBefore - 2

        var guard = 0
        while (driver.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
            driver.bothPass(); guard++
        }
        (driver.state.pendingDecision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(me, listOf(creatureB)).isSuccess shouldBe true

        guard = 0
        while (driver.stackSize > 0 && guard < 20) { driver.bothPass(); guard++ }

        driver.state.projectedState.getPower(creatureA) shouldBe 3
        driver.state.projectedState.getPower(creatureB) shouldBe 3
    }

    test("a creature-source ability is neither offered nor accepted as a target") {
        val driver = setup()
        val me = driver.activePlayer!!

        val scientist = driver.putCreatureOnBattlefield(me, "Scientist Supreme of A.I.M.")
        val pumper = driver.putCreatureOnBattlefield(me, "Scientist Test Pumper")
        driver.removeSummoningSickness(pumper)
        val creatureA = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val pumpAbilityId = driver.cardRegistry.requireCard("Scientist Test Pumper").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = pumper, abilityId = pumpAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )
        val pumpOnStack = driver.getTopOfStack()!!

        offeredCopyTargets(driver, me, scientist).contains(pumpOnStack) shouldBe false

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = me, sourceId = scientist, abilityId = copyAbilityId,
                targets = listOf(ChosenTarget.Spell(pumpOnStack))
            )
        )
    }

    test("activate only once each turn") {
        val driver = setup()
        val me = driver.activePlayer!!

        val scientist = driver.putCreatureOnBattlefield(me, "Scientist Supreme of A.I.M.")
        val lens = driver.putPermanentOnBattlefield(me, "Scientist Test Lens")
        val lens2 = driver.putPermanentOnBattlefield(me, "Scientist Test Lens")
        val creatureA = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lensAbilityId = driver.cardRegistry.requireCard("Scientist Test Lens").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = lens, abilityId = lensAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = scientist, abilityId = copyAbilityId,
                targets = listOf(ChosenTarget.Spell(driver.getTopOfStack()!!))
            )
        )

        // Second artifact ability on the stack, but the Scientist is spent for the turn.
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = lens2, abilityId = lensAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )
        val secondLensOnStack = driver.getTopOfStack()!!

        offeredCopyTargets(driver, me, scientist).contains(secondLensOnStack) shouldBe false
        driver.submitExpectFailure(
            ActivateAbility(
                playerId = me, sourceId = scientist, abilityId = copyAbilityId,
                targets = listOf(ChosenTarget.Spell(secondLensOnStack))
            )
        )
    }

    test("can't be activated during an opponent's turn") {
        val driver = setup()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val scientist = driver.putCreatureOnBattlefield(opponent, "Scientist Supreme of A.I.M.")
        val lens = driver.putPermanentOnBattlefield(opponent, "Scientist Test Lens")
        val creatureA = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        // Hand priority to the non-active player so they can use their own artifact at instant speed.
        driver.passPriority(me)

        val lensAbilityId = driver.cardRegistry.requireCard("Scientist Test Lens").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(
                playerId = opponent, sourceId = lens, abilityId = lensAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )
        val lensOnStack = driver.getTopOfStack()!!

        // It is `me`'s turn, so the Scientist's controller may not activate it.
        offeredCopyTargets(driver, opponent, scientist).contains(lensOnStack) shouldBe false
        driver.submitExpectFailure(
            ActivateAbility(
                playerId = opponent, sourceId = scientist, abilityId = copyAbilityId,
                targets = listOf(ChosenTarget.Spell(lensOnStack))
            )
        )
    }
})
